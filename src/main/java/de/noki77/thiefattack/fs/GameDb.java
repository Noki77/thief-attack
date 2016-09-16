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

package de.noki77.thiefattack.fs;

import de.noki77.thiefattack.game.GameData;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameDb {
    private Map<Integer, GameData> games = new ConcurrentHashMap<>();

    public GameDb() {}

    public GameDb(Map<Integer, GameData> games) {
        this.games = games;
    }

    public void setGame(int id, GameData data) {
        data.setGameId(id);
        this.games.put(id, data);
    }

    public GameData getGame(int id) {
        return this.games.get(id);
    }

    public static GameDb loadFromFile(File f) throws IOException {
        return DataUtil.loadDecompressedAs(f, GameDb.class);
    }

    public void saveToFile(File f) throws IOException {
        if (f.isFile()) {
            f.delete();
        }
        f.createNewFile();

        DataUtil.saveCompressed(f, this);
    }
}
