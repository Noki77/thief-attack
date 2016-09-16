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

package de.noki77.thiefattack.commands;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;
import com.sk89q.intake.parametric.annotation.Text;
import core.org.apache.commons.lang3.StringUtils;

import de.noki77.thiefattack.commands.intake.module.PlayerSender;
import de.noki77.thiefattack.commands.intake.module.Sender;
import de.noki77.thiefattack.commands.intake.module.StandingIn;
import de.noki77.thiefattack.game.GameMap;
import de.noki77.thiefattack.handler.SoundInvClickHandler;
import de.noki77.thiefattack.handler.ToolHandler;
import de.noki77.thiefattack.util.NmsUtil;
import de.noki77.thiefattack.util.WorldHelper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AdminCommands extends BaseCommand {
    private static final Set<Material> invisible = new HashSet<Material>() {
        private static final long serialVersionUID = 7658826507325345509L;
        {
            add(Material.AIR);
            add(Material.WATER);
            add(Material.STATIONARY_WATER);
            add(Material.LAVA);
            add(Material.STATIONARY_LAVA);
            add(Material.WEB);
        }
    };
    
    @Command(aliases = "test", desc = "Test command", max = 1, min = 0, usage = "[text]")
    @Require("thiefattack.admin.test")
    public void test(@Sender CommandSender sender, @Text String given) {
        if (given != null && !given.trim().isEmpty()) {
            sender.sendMessage("§cThe test is working! (you gave me the message §7" + given + "§c)");
        } else {
            sender.sendMessage("§cThe test is working!");
        }
    }
    
    @Command (aliases = {"", "info"}, desc = "Info command", min = 0, max = 0)
    @Require("thiefattack.user.info")
    public void info(@Sender CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2F, .5F);
        }

        String dashes = StringUtils.repeat("=", 10);
        sender.sendMessage(String.format("§6-%s[ §3%s §6]%s-", dashes, getPlugin().getDescription().getName(), dashes));
        sender.sendMessage(String.format("§6Version: §7%s", getPlugin().getDescription().getVersion()));
        sender.sendMessage(String.format("§6Author: §7%s", getPlugin().getDescription().getAuthors().toString().replaceAll("[\\[\\]]", "")));
    }

    @Command(aliases = "createmap", desc = "", min = 1, max = -1, usage = "<mapName>")
    @Require("thiefattack.admin.createmap")
    public void createMap(@PlayerSender Player sender, @Text String mapName) {
        if (StringUtils.isEmpty(mapName)) {
            sender.sendMessage("§cGive me a name!");
            return;
        }

        if (new File(getConfig().getMapDirectory(), mapName.toLowerCase()).isDirectory()) {
            sender.sendMessage("§CThe map is already existing!");
            return;
        }

        sender.sendMessage("§cCreating map...");
        try {
            GameMap map = WorldHelper.loadMap(mapName, true);
            getPlugin().getMapRegistry().addMap(map);
            sender.playSound(sender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0F, .5F);
            NmsUtil.sendTitlePerChar(sender, "§6Map §ccreated successfully!", 1, 40, 0, 10);
            NmsUtil.sendSubTitlePerChar(sender, "§7You can now enter the map using §3/ta tp " + mapName.toLowerCase(), 1, 200, 0, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"tp", "teleport"}, desc = "", min = 1, max = -1, usage = "<mapName>")
    @Require("thiefattack.admin.tp")
    public void tp(@PlayerSender Player sender, GameMap map) {
        sender.teleport(map.getParentWorld().getSpawnLocation());
        sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 2F, .5F);
        NmsUtil.clearTitle(sender);
//        NmsUtil.sendTitlePerChar(sender, "§5Woosh!", 1, 20, 0, 10);
    }

    @Command(aliases = {"load", "loadmap"}, desc = "", min = 1, max = -1, usage = "<mapName>")
    @Require("thiefattack.admin.load")
    public void loadMap(@PlayerSender Player sender, String mapName) {
        if (!new File(getConfig().getMapDirectory(), mapName.toLowerCase()).isDirectory()) {
            sender.sendMessage("§CThe map is not existing!");
            return;
        }

        try {
            GameMap map = WorldHelper.loadMap(mapName, true);
            getPlugin().getMapRegistry().addMap(map);
            sender.playSound(sender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0F, .5F);
            NmsUtil.sendTitlePerChar(sender, "§6Map §cloaded successfully!", 1, 40, 0, 10);
            NmsUtil.sendSubTitlePerChar(sender, "§7You can now enter the map using §3/ta tp " + mapName.toLowerCase(), 1, 200, 0, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = "unload", desc = "", min = 1, max = -1, usage = "<mapName>")
    @Require("thiefattack.admin.unload")
    public void unloadMap(@PlayerSender Player sender, GameMap map) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().getWorld().equals(map.getParentWorld())) {
                p.teleport(getConfig().getKickWorld().getSpawnLocation());
                p.sendMessage("§3The world you were standing in has been unloaded.");
            }
        }
        getPlugin().getMapRegistry().unloadMap(map.getWorldName());
    }

    @Command(aliases = "save", desc = "", min = 0, max = 0)
    @Require("thiefattack.admin.save")
    public void saveMap(@PlayerSender Player sender, @StandingIn GameMap map) {
        try {
            map.save();
            sender.sendMessage("§3Map saved!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred when saving the map, please ask an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = "sounds", desc = "")
    @Require("thiefattack.admin.sounds")
    public void soundsInv(@PlayerSender Player sender) {
        Inventory inv = Bukkit.createInventory(null, SoundInvClickHandler.INV_SIZE, "§5Soundplayer§c, click any item");
        SoundInvClickHandler.fillInv(inv, 0);
        sender.openInventory(inv);
    }

    @Command(aliases = "gold", desc = "")
    @Require("thiefattack.admin.gold")
    public void gold(@PlayerSender Player sender) {
        NmsUtil.sendFakeBlockUpdate(sender, sender.getLocation().add(0, -1, 0), Material.GOLD_BLOCK);
    }

    @Command(aliases = "loadedworlds", desc = "")
    @Require("thiefattack.admin.loaded")
    public void loaded(@PlayerSender Player sender) {
        sender.sendMessage("§3Loaded worlds:");
        for (World w : Bukkit.getWorlds()) {
            sender.sendMessage("§6" + w.getName());
        }
    }

    @Command(aliases = {"name", "displayname", "setname", "newname"}, desc = "", min = 1, usage = "<new map name>")
    @Require("thiefattack.admin.newname")
    public void setName(@PlayerSender Player sender, @StandingIn GameMap map, @Text String newName) {
        if (StringUtils.isEmpty(newName)) {
            sender.sendMessage("§cGive me a name!");
            return;
        }

        map.setDisplayName(newName);
        try {
            map.save(false);
            sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, .5F);
            sender.sendMessage("§3New name set!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = {"setspawn", "settp"}, desc = "", min = 0, max = 0)
    @Require("thiefattack.admin.setspawn")
    public void setSpawn(@PlayerSender Player sender, @StandingIn World world) {
        world.setSpawnLocation(sender.getLocation().getBlockX(), sender.getLocation().getBlockY(), sender.getLocation().getBlockZ());
        world.save();
        sender.sendMessage("§3Spawn set!");
    }

    @Command(aliases = {"showspawns", "spawns"}, desc = "")
    @Require("thiefattack.admin.showspawns")
    public void showSpawns(@PlayerSender Player sender, @StandingIn GameMap map) {
        for (Vector spawn : map.getSpawns()) {
            Location loc = new Location(map.getParentWorld(), spawn.getBlockX(), spawn.getBlockY() - 1, spawn.getBlockZ());
            NmsUtil.sendFakeBlockUpdate(sender, loc, Material.RED_NETHER_BRICK);
        }
        sender.sendMessage("§3Spawn blocks are now highlighted!");
    }

    @Command(aliases = {"addspawn"}, desc = "")
    @Require("thiefattack.admin.addspawn")
    public void addSpawn(@PlayerSender Player sender, @StandingIn GameMap map) {
        Location blockLocation = sender.getTargetBlock(invisible, 10).getLocation();
        Vector vec = new Vector(blockLocation.getBlockX(), blockLocation.getBlockY() + 1, blockLocation.getBlockZ());

        if (map.hasSpawn(vec)) {
            sender.sendMessage("§cSpawn already registered!");
            sender.playSound(sender.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1F, 2F);
            return;
        }

        map.addSpawn(vec);
        try {
            map.save(false);
            sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, .5F);
            sender.sendMessage("§3Spawn added!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = {"addchest"}, desc = "")
    @Require("thiefattack.admin.addchest")
    public void addChest(@PlayerSender Player sender, @StandingIn GameMap map) {
        Location blockLocation = sender.getTargetBlock(invisible, 10).getLocation();
        Vector vec = new Vector(blockLocation.getBlockX(), blockLocation.getBlockY() + 1, blockLocation.getBlockZ());

        if (map.hasChest(vec)) {
            sender.sendMessage("§cChest already registered!");
            sender.playSound(sender.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1F, 2F);
            return;
        }

        map.addChest(vec);
        try {
            map.save(false);
            sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, .5F);
            sender.sendMessage("§3Chest added!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = {"addspecialchest", "addenderchest"}, desc = "")
    @Require("thiefattack.admin.addspecialchest")
    public void addSpecialChest(@PlayerSender Player sender, @StandingIn GameMap map) {
        Location blockLocation = sender.getTargetBlock(invisible, 10).getLocation();
        Vector vec = new Vector(blockLocation.getBlockX(), blockLocation.getBlockY() + 1, blockLocation.getBlockZ());

        if (map.hasSpecialChest(vec)) {
            sender.sendMessage("§Special Chest already registered!");
            sender.playSound(sender.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1F, 2F);
            return;
        }

        map.addSpecialChest(vec);
        try {
            map.save(false);
            sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, .5F);
            sender.sendMessage("§3Special Chest added!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = {"addsecret"}, desc = "")
    @Require("thiefattack.admin.addsecret")
    public void addSecret(@PlayerSender Player sender, @StandingIn GameMap map) {
        Location blockLocation = sender.getTargetBlock(invisible, 10).getLocation();
        Vector vec = new Vector(blockLocation.getBlockX(), blockLocation.getBlockY() + 1, blockLocation.getBlockZ());

        if (map.hasSecret(vec)) {
            sender.sendMessage("§Secret already registered!");
            sender.playSound(sender.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1F, 2F);
            return;
        }

        map.addSecret(vec);
        try {
            map.save(false);
            sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, .5F);
            sender.sendMessage("§3Secret added!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = "tools", desc = "")
    @Require("thiefattack.admin.tools")
    public void getTools(@PlayerSender Player sender) {
        Inventory inv = Bukkit.createInventory(null, 9, "§6ThiefAttack Tools");
        inv.addItem(ToolHandler.spawnTool);
        inv.addItem(ToolHandler.spawnRemovalTool);
        inv.addItem(ToolHandler.chestTool);
        inv.addItem(ToolHandler.chestRemovalTool);
        inv.addItem(ToolHandler.enderChestTool);
        inv.addItem(ToolHandler.enderChestRemovalTool);
        inv.addItem(ToolHandler.secretTool);
        inv.addItem(ToolHandler.secretRemovalTool);
        sender.openInventory(inv);
    }

    @Command(aliases = "gamerule", desc = "", min = 1, max = 2, usage = "<gamerule> [value]")
    @Require("thiefattack.admin.gamerule")
    public void gamerules(@PlayerSender Player sender, @StandingIn GameMap map, String rule, @Optional String value) {
        if (StringUtils.isEmpty(value)) {
            if (map.getGamerules().containsKey(rule)) {
                sender.sendMessage("§cThis gamerule is not set!");
            } else {
                sender.sendMessage("§3Value of gamerule §6" + rule + "§3: §6" + map.getGamerules().get(rule));
            }
        } else {
            map.setGamerule(rule, value);
            try {
                map.save(false);
            } catch (IOException e) {
                sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
                e.printStackTrace();
            }
            sender.sendMessage("§3Gamerule set!");
        }
    }

    @Command(aliases = "applyrules", desc = "")
    @Require("thiefattack.admin.applyrules")
    public void applyGamerules(@PlayerSender Player sender, @StandingIn GameMap map) {
        map.applyGamerules();
        try {
            map.save();
            sender.sendMessage("§3Gamerules applied!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = "hideplayer", desc = "", min = 1, max = 2)
    @Require("thiefattack.admin.hide")
    public void hidePlayer(@PlayerSender Player sender, Player target, @Optional Player viewer) {
        Player used = viewer != null ? viewer : sender;
        NmsUtil.hidePlayer(used, target);
    }

    @Command(aliases = "showplayer", desc = "", min = 1, max = 2)
    @Require("thiefattack.admin.show")
    public void showPlayer(@PlayerSender Player sender, Player target, @Optional Player viewer) {
        Player used = viewer != null ? viewer : sender;
        NmsUtil.showPlayer(used, target);
    }

    @Command(aliases = {"kickpoint", "kickworld"}, desc = "")
    @Require("thiefattack.admin.setkickworld")
    public void setKickWorld(@PlayerSender Player sender, @StandingIn World world) {
        getConfig().setKickWorld(world.getName());
        try {
            getConfig().save();
            sender.sendMessage("§3Kick world set!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }

    @Command(aliases = "setlobby", desc = "")
    @Require("thiefattack.admin.setlobby")
    public void setLobbyWorld(@PlayerSender Player sender, @StandingIn World world) {
        getConfig().setLobbyMapName(world.getName());
        try {
            getConfig().save();
            sender.sendMessage("§3Lobby world name saved!");
        } catch (IOException e) {
            sender.sendMessage("§cAn error occurred during saving, please contact an administrator for further help.");
            e.printStackTrace();
        }
    }
}