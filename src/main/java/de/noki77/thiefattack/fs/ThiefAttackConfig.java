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

import core.org.apache.commons.lang3.StringUtils;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ThiefAttackConfig {
    private static final Yaml yaml;
    
    static {
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(FlowStyle.BLOCK);

        Representer repr = new Representer();
        repr.setDefaultFlowStyle(FlowStyle.BLOCK);
        repr.addClassTag(ThiefAttackConfig.class, Tag.MAP);

        yaml = new Yaml(new Constructor() {
            @Override
            protected Class<?> getClassForName(String name) throws ClassNotFoundException {
                return Class.forName(name, true, ThiefAttackConfig.class.getClassLoader());
            }
        }, repr, opts);
    }

    private transient File configFile;
    private String mapDirectory = "./TAMaps";
    private String gameMapDirectory = "./GameMaps";
    private String kickWorld = "world";
    private String lobbyMapName = "lobby";
    private boolean serverBased = false;
    private boolean tenSecondsTitle = true;
    private int secondsTillStart = 60;

    public ThiefAttackConfig(File configFile) {
        this.configFile = configFile;
    }

    public ThiefAttackConfig(String mapDirectory, boolean serverBased, boolean tenSecondsTitle, int secondsTillStart) {
        this.mapDirectory = mapDirectory;
        this.serverBased = serverBased;
        this.tenSecondsTitle = tenSecondsTitle;
        this.secondsTillStart = secondsTillStart;
    }

    public boolean isServerBased() {
        return this.serverBased;
    }
    
    public void setServerBased(boolean serverBased) {
        this.serverBased = serverBased;
    }
    
    public int getSecondsTillStart() {
        return this.secondsTillStart;
    }
    
    public void setSecondsTillStart(int secondsTillStart) {
        this.secondsTillStart = secondsTillStart;
    }

    public String getMapDirectory() {
        return this.mapDirectory;
    }

    public void setMapDirectory(String mapDirectory) {
        this.mapDirectory = mapDirectory;
    }

    public boolean isTenSecondsTitle() {
        return this.tenSecondsTitle;
    }

    public void setTenSecondsTitle(boolean tenSecondsTitle) {
        this.tenSecondsTitle = tenSecondsTitle;
    }

    public void saveToFile(File configFile) throws IOException {
        if (configFile.isFile()) {
            configFile.delete();
        }
        configFile.getAbsoluteFile().getParentFile().mkdirs();
        configFile.createNewFile();
        
        FileUtils.writeStringToFile(configFile, yaml.dump(this));
    }

    public void save() throws IOException {
        saveToFile(getConfigFile());
    }

    public String getKickWorldName() {
        return this.kickWorld;
    }

    public World getKickWorld() {
        if (StringUtils.isEmpty(getKickWorldName())) {
            return Bukkit.getWorlds().get(0);
        }

        World w = Bukkit.getWorld(getKickWorldName());
        if (w == null) {
            return Bukkit.getWorlds().get(0);
        } else {
            return w;
        }
    }

    public void setKickWorld(String kickWorld) {
        this.kickWorld = kickWorld;
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public String getGameMapDirectory() {
        return this.gameMapDirectory;
    }

    public void setGameMapDirectory(String gameMapDirectory) {
        this.gameMapDirectory = gameMapDirectory;
    }

    public String getLobbyMapName() {
        return this.lobbyMapName;
    }

    public void setLobbyMapName(String lobbyMapName) {
        this.lobbyMapName = lobbyMapName;
    }

    public static ThiefAttackConfig loadFromFile(File configFile) throws IOException {
        ThiefAttackConfig config = yaml.loadAs(new FileInputStream(configFile), ThiefAttackConfig.class);
        config.setConfigFile(configFile);
        return config;
    }
}