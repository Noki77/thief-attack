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

package de.noki77.thiefattack;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.parametric.ProvisionException;
import com.sk89q.intake.util.auth.AuthorizationException;
import core.com.google.common.base.Joiner;

import de.noki77.thiefattack.commands.ThiefAttackCommand;
import de.noki77.thiefattack.fs.GameDb;
import de.noki77.thiefattack.fs.ThiefAttackConfig;
import de.noki77.thiefattack.game.GameMap;
import de.noki77.thiefattack.game.MapRegistry;
import de.noki77.thiefattack.handler.SoundInvClickHandler;
import de.noki77.thiefattack.handler.ToolHandler;
import de.noki77.thiefattack.handler.WorldChangeHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.bukkit.plugin.java.JavaPlugin;

public class ThiefAttack extends JavaPlugin {
    private static ThiefAttack instance;
    
    private ThiefAttackConfig config;
    private Dispatcher dispatcher;
    private MapRegistry loadedMaps;
    private GameDb gameDb;
    
    @Override
    public void onLoad() {
        ThiefAttack.instance = this;

        getLogger().info("Loading config");
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.isFile()) {
            try {
                this.config = ThiefAttackConfig.loadFromFile(configFile);
            } catch (Exception e) {
                getLogger().severe("Failed to load config:");
                e.printStackTrace();
            }
        } else {
            this.config = new ThiefAttackConfig();
            try {
                this.config.saveToFile(configFile);
            } catch (IOException e) {
                getLogger().severe("Failed to save config:");
                e.printStackTrace();
            }
        }

        getLogger().info("Loading game database");
        configFile = new File(getDataFolder(), "games.jdb");
        if (configFile.isFile()) {
            try {
                this.gameDb = GameDb.loadFromFile(configFile);
            } catch (Exception e) {
                getLogger().severe("Failed to load game database:");
                e.printStackTrace();
            }
        } else {
            this.gameDb = new GameDb();
            try {
                this.gameDb.saveToFile(configFile);
            } catch (IOException e) {
                getLogger().severe("Failed to save game database:");
                e.printStackTrace();
            }
        }

        this.loadedMaps = new MapRegistry();
    }
    
    @Override
    public void onEnable() {
        getLogger().info("Loading command framework");
        this.dispatcher = ThiefAttackCommand.build(null);

        getLogger().info("Registering listeners");
        Bukkit.getPluginManager().registerEvents(new SoundInvClickHandler(), this);
        Bukkit.getPluginManager().registerEvents(new WorldChangeHandler(), this);
        Bukkit.getPluginManager().registerEvents(new ToolHandler(), this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (this.dispatcher != null) {
            Namespace namespace = new Namespace();
            namespace.put(CommandSender.class, sender);

            try {
                return this.dispatcher.call(cmd.getName() + " " + Joiner.on(" ").join(args), namespace, Collections.<String>emptyList());
            } catch (InvalidUsageException e) {
                sender.sendMessage("§cUsage: §7" + e.getSimpleUsageString("/"));
            } catch (AuthorizationException e) {
                sender.sendMessage("§cYou don't have permissions to use that command!");
            } catch (InvocationCommandException | CommandException e) {
                Throwable source = e;
                while (source.getCause() != null) {
                    source = source.getCause();
                }

                if (!((source instanceof ProvisionException) || (source instanceof InvocationTargetException))) {
                    sender.sendMessage(String.format("§c[%s] %s", source.getClass().getSimpleName(), source.getLocalizedMessage()));
                    e.printStackTrace();
                } else {
                    sender.sendMessage(e.getMessage());
                }
            }
        } else {
            sender.sendMessage("§cCommands are disabled, because the command framework is not initialized correctly!");
            throw new IllegalStateException("Command framework not or not completely initialized!");
        }

        return true;
    }

    @Override
    public void onDisable() {
        getLogger().info("Unloading maps...");
        for (GameMap map : getMapRegistry().getLoadedMaps()) {
            getMapRegistry().unloadMap(map.getWorldName());
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidWorldGen();
    }

    public ThiefAttackConfig getPluginConfig() {
        return this.config;
    }

    public MapRegistry getMapRegistry() {
        return this.loadedMaps;
    }

    public GameDb getGameDb() {
        return this.gameDb;
    }

    public static ThiefAttack getInstance() {
        return instance;
    }
}