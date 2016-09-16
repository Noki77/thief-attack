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

import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapRegistry {
    private Map<String, GameMap> loadedMaps = new ConcurrentHashMap<>();

    public void addMap(GameMap map) {
        this.loadedMaps.put(map.getWorldName().toLowerCase(), map);
    }

    public GameMap getMap(String key) {
        return this.loadedMaps.get(key.toLowerCase());
    }

    public boolean hasMap(String key) {
        return this.loadedMaps.containsKey(key.toLowerCase());
    }

    public void removeMap(String key) {
        this.loadedMaps.remove(key.toLowerCase());
    }

    public List<GameMap> getLoadedMaps() {
        return new ArrayList<>(this.loadedMaps.values());
    }

    public void unloadMap(String key) {
        GameMap map = getMap(key);
        if (map != null) {
            try {
                map.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.unloadWorld(map.getParentWorld(), false);
            removeMap(key);
        }
    }
}
