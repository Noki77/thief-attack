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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SoundInvClickHandler implements Listener {
    public static final int INV_SIZE = 54;

    private static Map<HumanEntity, Integer> pages = new ConcurrentHashMap<>();
    private static List<Sound> sounds = new CopyOnWriteArrayList<Sound>() {
        private static final long serialVersionUID = -7391021167541285378L;
        {
            List<Sound> sl = Arrays.asList(Sound.values());
            int i = 0;
            for (Sound s : sl) {
                if ((i + 1) % 9 == 0) {
                    add(null);
                    add(s);
                } else {
                    add(s);
                }
                i++;
            }
        }
    };
    public static final ItemStack stackUp;
    public static final ItemStack stackDown;
    public static final ItemStack stackDisabled;

    static {
        stackUp = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
        stackDown = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        stackDisabled = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STAINED_GLASS_PANE);
        meta.setDisplayName("§bUp!");
        stackUp.setItemMeta(meta);
        meta.setDisplayName("§cDown!");
        stackDown.setItemMeta(meta);
        meta.setDisplayName("§0Nothing");
        stackDisabled.setItemMeta(meta);
    }

    public static void fillInv(Inventory inv, int page) {
        for (int i = page * INV_SIZE; i < INV_SIZE + page * INV_SIZE; i++) {
            Sound s = sounds.get(i);
            if (s != null) {
                ItemStack stack = new ItemStack(Material.RECORD_10);
                ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.RECORD_10);
                meta.setDisplayName("§3" + s.name());
                stack.setItemMeta(meta);
                inv.setItem(i, stack);
            }
        }

        if (page > 0) {
            inv.setItem(8, stackUp);
            inv.setItem(17, stackUp);
            inv.setItem(26, stackUp);
        } else {
            inv.setItem(8, stackDisabled);
            inv.setItem(17, stackDisabled);
            inv.setItem(26, stackDisabled);
        }


        if (page < (sounds.size() / INV_SIZE)) {
            inv.setItem(35, stackDown);
            inv.setItem(44, stackDown);
            inv.setItem(53, stackDown);
        } else {
            inv.setItem(35, stackDisabled);
            inv.setItem(44, stackDisabled);
            inv.setItem(53, stackDisabled);
        }
    }

    @EventHandler
    public void onInvOpen(InventoryOpenEvent ev) {
        if (ev.getInventory() != null && ev.getInventory().getTitle() != null) {
            if (ev.getInventory().getTitle().startsWith("§5Soundplayer§c, click any item")) {
                pages.remove(ev.getPlayer());
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent ev) {
        if (ev.getInventory() != null && ev.getInventory().getTitle() != null) {
            if (ev.getInventory().getTitle().startsWith("§5Soundplayer§c, click any item")) {
                ev.setCancelled(true);
                if (ev.getCurrentItem() != null) {
                    if (ev.getCurrentItem().equals(stackUp)) {
                        int page = pages.containsKey(ev.getWhoClicked()) ? pages.get(ev.getWhoClicked()) + 1 : 1;
                        pages.put(ev.getWhoClicked(), page);
                        fillInv(ev.getInventory(), page);

                        ev.getWhoClicked().sendMessage("§3Page §6" + page + "/" + (sounds.size() / INV_SIZE));
                    } else if (ev.getCurrentItem().equals(stackDown)) {
                        int page = pages.containsKey(ev.getWhoClicked()) ? pages.get(ev.getWhoClicked()) - 1 : 0;
                        pages.put(ev.getWhoClicked(), page);
                        fillInv(ev.getInventory(), page);

                        ev.getWhoClicked().sendMessage("§3Page §6" + page + "/" + (sounds.size() / INV_SIZE));
                    } else {
                        ((Player) ev.getWhoClicked()).playSound(ev.getWhoClicked().getLocation(),
                                Sound.valueOf(ev.getCurrentItem().getItemMeta().getDisplayName().substring(2)), 0F, 1F);
                    }
                }
            }
        }
    }
}
