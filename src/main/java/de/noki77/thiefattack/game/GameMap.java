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

package de.noki77.thiefattack.game;

import de.noki77.thiefattack.fs.DataUtil;

import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameMap {
    private transient World parentWorld;
    private transient File mapDataFile;
    private String worldName;
    private String displayName;
    private List<Vector> chests = new CopyOnWriteArrayList<>();
    private List<Vector> specialChests = new CopyOnWriteArrayList<>();
    private List<Vector> secrets = new CopyOnWriteArrayList<>();
    private List<Vector> spawns = new CopyOnWriteArrayList<>();
    private Map<String, String> gamerules = new ConcurrentHashMap<>();

    public GameMap(World parentWorld, String displayName, File mapDataFile) {
        this.parentWorld = parentWorld;
        this.worldName = parentWorld.getName();
        this.mapDataFile = mapDataFile;
        this.displayName = displayName;
    }

    public World getParentWorld() {
        return this.parentWorld;
    }

    public void setParentWorld(World parentWorld) {
        this.parentWorld = parentWorld;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public File getMapDataFile() {
        return this.mapDataFile;
    }

    public void setMapDataFile(File mapDataFile) {
        this.mapDataFile = mapDataFile;
    }

    public void addSpawn(Vector v) {
        if (this.spawns == null) {
            this.spawns = new CopyOnWriteArrayList<>();
        }
        this.spawns.add(v);
    }

    public void addSpawn(int x, int y, int z) {
        addSpawn(new Vector(x, y, z));
    }

    public void addSpecialChest(Vector v) {
        if (this.specialChests == null) {
            this.specialChests = new CopyOnWriteArrayList<>();
        }
        this.specialChests.add(v);
    }

    public void addSpecialChest(int x, int y, int z) {
        addSpecialChest(new Vector(x, y, z));
    }

    public void addChest(Vector v) {
        if (this.chests == null) {
            this.chests = new CopyOnWriteArrayList<>();
        }
        this.chests.add(v);
    }

    public void addChest(int x, int y, int z) {
        addChest(new Vector(x, y, z));
    }

    public void addSecret(Vector v) {
        if (this.secrets == null) {
            this.secrets = new CopyOnWriteArrayList<>();
        }
        this.secrets.add(v);
    }

    public void addSecret(int x, int y, int z) {
        addSecret(new Vector(x, y, z));
    }

    public boolean removeSpawn(Vector v) {
        if (this.spawns == null) {
            this.spawns = new CopyOnWriteArrayList<>();
            return false;
        }
        return this.spawns.remove(v);
    }

    public boolean removeSpawn(int x, int y, int z) {
        return removeSpawn(new Vector(x, y, z));
    }

    public boolean removeChest(Vector v) {
        if (this.chests == null) {
            this.chests = new CopyOnWriteArrayList<>();
            return false;
        }
        return this.chests.remove(v);
    }

    public boolean removeChest(int x, int y, int z) {
        return removeChest(new Vector(x, y, z));
    }

    public boolean removeSpecialChest(Vector v) {
        if (this.specialChests == null) {
            this.specialChests = new CopyOnWriteArrayList<>();
            return false;
        }
        return this.specialChests.remove(v);
    }

    public boolean removeSpecialChest(int x, int y, int z) {
        return removeSpecialChest(new Vector(x, y, z));
    }

    public boolean removeSecret(Vector v) {
        if (this.secrets == null) {
            this.secrets = new CopyOnWriteArrayList<>();
            return false;
        }
        return this.secrets.remove(v);
    }

    public boolean removeSecret(int x, int y, int z) {
        return removeSecret(new Vector(x, y, z));
    }

    public boolean hasSpawn(Vector v) {
        return this.spawns.contains(v);
    }

    public boolean hasSpawn(int x, int y, int z) {
        return hasSpawn(new Vector(x, y, z));
    }

    public boolean hasChest(Vector v) {
        return this.chests.contains(v);
    }

    public boolean hasChest(int x, int y, int z) {
        return hasChest(new Vector(x, y, z));
    }

    public boolean hasSpecialChest(Vector v) {
        return this.specialChests.contains(v);
    }

    public boolean hasSpecialChest(int x, int y, int z) {
        return hasSpecialChest(new Vector(x, y, z));
    }

    public boolean hasSecret(Vector v) {
        return this.secrets.contains(v);
    }

    public boolean hasSecret(int x, int y, int z) {
        return hasSecret(new Vector(x, y, z));
    }

    public List<Vector> getChests() {
        return this.chests;
    }

    public List<Vector> getSpecialChests() {
        return this.specialChests;
    }

    public List<Vector> getSecrets() {
        return this.secrets;
    }

    public List<Vector> getSpawns() {
        return this.spawns;
    }

    public Map<String, String> getGamerules() {
        return this.gamerules;
    }

    public void setGamerules(Map<String, String> gamerules) {
        this.gamerules = gamerules;
    }

    public void setGamerule(String key, String value) {
        if (this.gamerules == null) {
            this.gamerules = new ConcurrentHashMap<>();
        }

        this.gamerules.put(key, value);
    }

    public void removeGamerule(String key) {
        if (this.gamerules == null) {
            this.gamerules = new ConcurrentHashMap<>();
        }

        this.gamerules.remove(key);
    }

    public void applyGamerules() {
        if (this.gamerules == null) {
            this.gamerules = new ConcurrentHashMap<>();
        }

        for (String rule : this.gamerules.keySet()) {
            getParentWorld().setGameRuleValue(rule, this.gamerules.get(rule));
        }
    }

    public void save(boolean saveWorld) throws IOException {
        DataUtil.saveCompressed(this.mapDataFile, this);
        if (saveWorld) {
            this.parentWorld.save();
        }
    }

    public void save() throws IOException {
        save(true);
    }
}
