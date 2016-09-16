/*
 * This file is part of the project ThiefAttack, licensed under the
 * Creative Commons Attribution-NoDerivatives 4.0 International license.
 *
 * Copyright (c) 2016 Noki77 <dernoki77@gmail.com>
 * Copyright (c) contributors
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nd/4.0/>.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THE TERMS
 * OF THIS CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE SOFTWARE IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE
 * OR COPYRIGHT LAW IS PROHIBITED.
 *
 * BY EXERCISING ANY RIGHTS TO THE SOFTWARE PROVIDED HERE,
 * YOU ACCEPT AND AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE.
 * TO THE EXTENT THIS LICENSE MAY BE CONSIDERED TO BE A CONTRACT,
 * THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED HERE IN CONSIDERATION
 * OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */

package de.noki77.thiefattack.util;

import com.google.common.base.Joiner;
import core.org.apache.commons.io.FileUtils;
import core.org.apache.commons.lang3.StringUtils;

import de.noki77.thiefattack.ThiefAttack;
import de.noki77.thiefattack.fs.DataUtil;
import de.noki77.thiefattack.game.GameMap;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.DedicatedServer;
import net.minecraft.server.v1_10_R1.EntityTracker;
import net.minecraft.server.v1_10_R1.EnumDifficulty;
import net.minecraft.server.v1_10_R1.IProgressUpdate;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.ServerNBTManager;
import net.minecraft.server.v1_10_R1.WorldData;
import net.minecraft.server.v1_10_R1.WorldLoaderServer;
import net.minecraft.server.v1_10_R1.WorldManager;
import net.minecraft.server.v1_10_R1.WorldServer;
import net.minecraft.server.v1_10_R1.WorldSettings;
import net.minecraft.server.v1_10_R1.WorldType;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.craftbukkit.v1_10_R1.scoreboard.CraftScoreboardManager;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class WorldHelper {
    private static Field worldsField;

    static {
        try {
            worldsField = CraftServer.class.getDeclaredField("worlds");
            worldsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static World createWorld(WorldCreator worldCreator, File worldContainer, String worldGenerator) {
        Logger log = ThiefAttack.getInstance().getLogger();
        MinecraftServer mcServer = ((CraftServer) Bukkit.getServer()).getServer();
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getHandle().getServer();
        
        String name = worldCreator.name();
        ChunkGenerator generator = worldCreator.generator();
        WorldType type = WorldType.getType(worldCreator.type().getName());
        boolean generateStructures = worldCreator.generateStructures();

        File folder = new File(worldContainer, name);

        World world = Bukkit.getWorld(name);
        if(world != null) {
            return world;
        } else if (folder.exists() && !folder.isDirectory()) {
            throw new IllegalArgumentException("File exists with the name \'" + name + "\' and isn\'t a folder");
        } else {
            if(generator == null) {
                generator = getGenerator(name, worldGenerator);
            }

            WorldLoaderServer converter = new WorldLoaderServer(worldContainer, server.getDataConverterManager());
            if(converter.isConvertable(name)) {
                log.info("Converting world \'" + name + "\'");
                converter.convert(name, new IProgressUpdate() {
                    private long b = System.currentTimeMillis();

                    public void a(String s) {
                    }

                    public void a(int i) {
                        if(System.currentTimeMillis() - this.b >= 1000L) {
                            this.b = System.currentTimeMillis();
                            MinecraftServer.LOGGER.info("Converting... " + i + "%");
                        }

                    }

                    public void c(String s) {
                    }
                });
            }

            int dimension = 10 + mcServer.worlds.size();
            boolean used = false;

            do {
                Iterator sdm = mcServer.worlds.iterator();

                while(sdm.hasNext()) {
                    WorldServer hardcore = (WorldServer)sdm.next();
                    used = hardcore.dimension == dimension;
                    if(used) {
                        ++dimension;
                        break;
                    }
                }
            } while(used);

            ServerNBTManager var25 = new ServerNBTManager(worldContainer, name, true, server.getDataConverterManager());
            WorldData worlddata = var25.getWorldData();
            WorldSettings worldSettings = null;
            if(worlddata == null) {
                worldSettings = new WorldSettings(worldCreator.seed(), mcServer.getGamemode(), generateStructures, false, type);
                worldSettings.setGeneratorSettings(worldCreator.generatorSettings());
                worlddata = new WorldData(worldSettings, name);
            }

            worlddata.checkName(name);
            WorldServer internal = (WorldServer)(new WorldServer(mcServer, var25, worlddata, dimension, mcServer.methodProfiler, worldCreator.environment(), generator)).b();
            if(!getWorlds().containsKey(name.toLowerCase(Locale.ENGLISH))) {
                return null;
            } else {
                if(worldSettings != null) {
                    internal.a(worldSettings);
                }

                internal.scoreboard = ((CraftScoreboardManager) Bukkit.getScoreboardManager()).getMainScoreboard().getHandle();
                internal.tracker = new EntityTracker(internal);
                internal.addIWorldAccess(new WorldManager(mcServer, internal));
                internal.worldData.setDifficulty(EnumDifficulty.EASY);
                internal.setSpawnFlags(true, true);
                mcServer.worlds.add(internal);
                if(generator != null) {
                    internal.getWorld().getPopulators().addAll(generator.getDefaultPopulators(internal.getWorld()));
                }

                Bukkit.getPluginManager().callEvent(new WorldInitEvent(internal.getWorld()));
                System.out.print("Preparing start region for level " + (mcServer.worlds.size() - 1) + " (Seed: " + internal.getSeed() + ")");
                if(internal.getWorld().getKeepSpawnInMemory()) {
                    short short1 = 196;
                    long i = System.currentTimeMillis();

                    for(int j = -short1; j <= short1; j += 16) {
                        for(int k = -short1; k <= short1; k += 16) {
                            long l = System.currentTimeMillis();
                            if(l < i) {
                                i = l;
                            }

                            if(l > i + 1000L) {
                                int chunkcoordinates = (short1 * 2 + 1) * (short1 * 2 + 1);
                                int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;
                                System.out.println("Preparing spawn area for " + name + ", " + j1 * 100 / chunkcoordinates + "%");
                                i = l;
                            }

                            BlockPosition var27 = internal.getSpawn();
                            internal.getChunkProviderServer().getChunkAt(var27.getX() + j >> 4, var27.getZ() + k >> 4);
                        }
                    }
                }

                Bukkit.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
                return internal.getWorld();
            }
        }
    }

    private static ChunkGenerator getGenerator(String world, String worldGenerator) {
        if (StringUtils.isEmpty(worldGenerator)) {
            return null;
        }

        String pluginName;
        String id;
        if (worldGenerator.contains(":")) {
            String[] parts = worldGenerator.split(":");
            id = parts[parts.length - 1];
            pluginName = Joiner.on(':').join(Arrays.copyOfRange(parts, 0, parts.length - 2));
        } else {
            id = null;
            pluginName = worldGenerator;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled(worldGenerator)) {
            return null;
        } else {
            return Bukkit.getPluginManager().getPlugin(worldGenerator).getDefaultWorldGenerator(world, id);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, World> getWorlds() {
        if (worldsField != null) {
            try {
                return (Map<String, World>) worldsField.get(Bukkit.getServer());
            } catch (IllegalAccessException ignored) {
                return new HashMap<>();
            }
        } else {
            return new HashMap<>();
        }
    }

    public static GameMap loadMap(String worldName, File worldContainer, boolean createIfNotExists) throws IOException {
        ThiefAttack plugin = ThiefAttack.getInstance();
        Logger log = plugin.getLogger();
        String internalName = worldName.replace(" ", "").toLowerCase();

        World w = Bukkit.getWorld(internalName);
        if (!worldContainer.isDirectory()) {
            if (worldContainer.exists()) {
                worldContainer.delete();
            }
            worldContainer.mkdirs();
        }

        File worldDirectory = new File(worldContainer, internalName);
        if (w != null) {
            File dataFile = new File(w.getWorldFolder(), "tadata");
            if (dataFile.isFile()) {
                GameMap mapData = DataUtil.loadDecompressedAs(dataFile, GameMap.class);
                mapData.setParentWorld(w);
                mapData.setMapDataFile(dataFile);
                mapData.applyGamerules();
                return mapData;
            } else {
                return new GameMap(w, worldName, dataFile);
            }
        } else {
            File dataFile = new File(worldDirectory, "tadata");
            GameMap mapData = null;
            if (worldDirectory.isDirectory()) {
                if (dataFile.isFile()) {
                    log.info("Loading map " + worldName);
                    mapData = DataUtil.loadDecompressedAs(dataFile, GameMap.class);
                } else {
                    log.info("Importing map " + worldName);
                }
            } else if (createIfNotExists) {
                log.info("Creating new map " + worldName);
            } else {
                log.warning("Map " + worldName + " is not existing and no new will be created.");
                return null;
            }


            WorldCreator wc = new WorldCreator(internalName);
            wc.generateStructures(false);
            World newWorld = createWorld(wc, worldContainer, plugin.getDescription().getName());
            if (newWorld == null) {
                log.severe("Could not create map " + worldName + ", don't know why.");
                return null;
            } else {
                if (mapData == null) {
                    mapData = new GameMap(newWorld, worldName, dataFile);
                } else {
                    mapData.setParentWorld(newWorld);
                    mapData.setMapDataFile(dataFile);
                }
                mapData.applyGamerules();
                return mapData;
            }
        }
    }

    public static GameMap loadMap(String worldName, boolean createIfNotExists) throws IOException {
        return loadMap(worldName, new File(ThiefAttack.getInstance().getPluginConfig().getMapDirectory()), createIfNotExists);
    }

    public static void copyMap(File target, File destination) throws IOException {
        destination.getAbsoluteFile().getParentFile().mkdirs();
        FileUtils.copyDirectory(target, destination);

        File toDelete = new File(destination, "level.dat");
        if (toDelete.exists()) {
            toDelete.delete();
        }

        toDelete = new File(destination, "level.dat_old");
        if (toDelete.exists()) {
            toDelete.delete();
        }

        toDelete = new File(destination, "uid.dat");
        if (toDelete.exists()) {
            toDelete.delete();
        }
    }
}
