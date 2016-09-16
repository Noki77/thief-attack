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

package de.noki77.thiefattack.handler;

import de.noki77.thiefattack.ThiefAttack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ToolHandler implements Listener {
    public static final ItemStack spawnTool;
    public static final ItemStack chestTool;
    public static final ItemStack secretTool;
    public static final ItemStack enderChestTool;
    public static final ItemStack spawnRemovalTool;
    public static final ItemStack chestRemovalTool;
    public static final ItemStack secretRemovalTool;
    public static final ItemStack enderChestRemovalTool;

    static {
        spawnTool = new ItemStack(Material.STICK, 1);
        chestTool = new ItemStack(Material.STICK, 1);
        secretTool = new ItemStack(Material.STICK, 1);
        enderChestTool = new ItemStack(Material.STICK, 1);
        spawnRemovalTool = new ItemStack(Material.BLAZE_ROD, 1);
        chestRemovalTool = new ItemStack(Material.BLAZE_ROD, 1);
        secretRemovalTool = new ItemStack(Material.BLAZE_ROD, 1);
        enderChestRemovalTool = new ItemStack(Material.BLAZE_ROD, 1);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STICK);
        meta.setDisplayName("§aAdd player spawn");
        spawnTool.setItemMeta(meta);
        meta.setDisplayName("§aAdd tool chest");
        chestTool.setItemMeta(meta);
        meta.setDisplayName("§aAdd secret");
        secretTool.setItemMeta(meta);
        meta.setDisplayName("§aAdd special tool chest");
        enderChestTool.setItemMeta(meta);

        meta = Bukkit.getItemFactory().getItemMeta(Material.BLAZE_ROD);
        meta.setLore(Collections.singletonList("§cWork in progress"));
        meta.setDisplayName("§cRemove player spawn");
        spawnRemovalTool.setItemMeta(meta);
        meta.setDisplayName("§cRemove tool chest");
        chestRemovalTool.setItemMeta(meta);
        meta.setDisplayName("§cRemove secret");
        secretRemovalTool.setItemMeta(meta);
        meta.setDisplayName("§cRemove special tool chest");
        enderChestRemovalTool.setItemMeta(meta);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        if (ev.getAction() == Action.RIGHT_CLICK_BLOCK && ev.hasItem()) {
            if (ThiefAttack.getInstance().getMapRegistry().hasMap(ev.getPlayer().getWorld().getName())) {
                if (ev.getItem().equals(spawnTool)) {
                    ev.getPlayer().getServer().dispatchCommand(ev.getPlayer(), "ta addspawn");
                } else if (ev.getItem().equals(chestTool)) {
                    ev.getPlayer().getServer().dispatchCommand(ev.getPlayer(), "ta addchest");
                } else if (ev.getItem().equals(secretTool)) {
                    ev.getPlayer().getServer().dispatchCommand(ev.getPlayer(), "ta addsecret");
                } else if (ev.getItem().equals(enderChestTool)) {
                    ev.getPlayer().getServer().dispatchCommand(ev.getPlayer(), "ta addenderchest");
                }
            }
        }
    }

}
