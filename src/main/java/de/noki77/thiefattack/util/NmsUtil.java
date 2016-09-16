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

import de.noki77.thiefattack.ThiefAttack;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.ChatComponentText;
import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_10_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NmsUtil {
    private static Field gameProfileNameField;

    public static void sendTitle(Player p, String msg, int fadeInTicks, int showTimeTicks, int fadeOutTicks) {
        sendPacket(p, new PacketPlayOutTitle(EnumTitleAction.TITLE, new ChatComponentText(msg)));
        sendPacket(p, new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeInTicks, showTimeTicks, fadeOutTicks));
    }
    
    public static void sendSubTitle(Player p, String msg, int fadeInTicks, int showTimeTicks, int fadeOutTicks) {
        sendPacket(p, new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, new ChatComponentText(msg)));
        sendPacket(p, new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeInTicks, showTimeTicks, fadeOutTicks));
    }
    
    public static void clearTitle(Player p) {
        sendPacket(p, new PacketPlayOutTitle(EnumTitleAction.CLEAR, null));
    }
    
    public static PlayerConnection getConnection(Player p) {
        return ((CraftPlayer) p).getHandle().playerConnection;
    }
    
    public static void sendPacket(Player p, Packet<?> packet) {
        getConnection(p).sendPacket(packet);
    }
    
    private static String nextLetter(char[] arr, int current) {
        if (arr.length <= current) {
            return "";
        }
        
        String letter = String.valueOf(arr[current]);
        if (arr[current] == '§') {
            letter += arr.length > current + 1 ? arr[current + 1] : "";
            letter += arr.length > current + 2 ? arr[current + 2] : "";
            current += 2;
        } else if (arr[current] == '\\') {
            letter += arr.length > current + 1 ? arr[current + 1] : "";
            current += 1;
        }
        return letter;
    }
    
    private static void sendTitlePerChar(Player p, String msg, int waitTicks, int showTicks, boolean isSub, int fadeInTicks, int fadeOutTicks, boolean waitAsBuildTime) {
        Bukkit.getScheduler().runTaskAsynchronously(ThiefAttack.getInstance(), () -> {
            String sending = "";
            char[] chars = msg.toCharArray();

            int i = 0;
            String toAppend = nextLetter(chars, i);
            sending += toAppend;
            i += toAppend.length();

            if (!isSub) {
                sendTitle(p, sending, fadeInTicks, waitTicks + 1, 0);
            } else {
                sendSubTitle(p, sending, fadeInTicks, waitTicks + 1, 0);
            }

            try {
                Thread.sleep(fadeInTicks * 50);
            } catch (Exception e) {}

            for (; i < chars.length; i++) {
                toAppend = nextLetter(chars, i);
                sending += toAppend;
                i += toAppend.length() - 1;

                int alive = waitTicks + 1;
                int fadeOut = 0;
                if (i == chars.length - 1) {
                    int cAlive = showTicks - fadeInTicks - fadeOutTicks - (waitAsBuildTime ? (msg.length() - 2) * waitTicks / msg.length() : (msg.length() - 2) * waitTicks);
                    if (showTicks == -1) {
                        alive = -1;
                    } else if (cAlive > waitTicks) {
                        alive = cAlive;
                    }
                    fadeOut = fadeOutTicks;
                }

                if (!isSub) {
                    sendTitle(p, sending, 0, alive , fadeOut);
                } else {
                    sendSubTitle(p, sending, 0, alive, fadeOut);
                }

                try {
                    if (!waitAsBuildTime) {
                        Thread.sleep(waitTicks * 50);
                    } else {
                        Thread.sleep(waitTicks * 50 / msg.length());
                    }
                } catch (Exception e) {}
            }
        });
    }

    public static void sendTitlePerChar(Player p, String msg, int waitTicks, int showTicks, int fadeInTicks, int fadeOutTicks, boolean waitAsBuildTime) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, false, fadeInTicks, fadeOutTicks, waitAsBuildTime);
    }

    public static void sendSubTitlePerChar(Player p, String msg, int waitTicks, int showTicks, int fadeInTicks, int fadeOutTicks, boolean waitAsBuildTime) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, true, fadeInTicks, fadeOutTicks, waitAsBuildTime);
    }

    public static void sendTitlePerChar(Player p, String msg, int waitTicks, int showTicks, boolean waitAsBuildTime) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, false, waitTicks, waitTicks, waitAsBuildTime);
    }

    public static void sendSubTitlePerChar(Player p, String msg, int waitTicks, int showTicks, boolean waitAsBuildTime) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, true, waitTicks, waitTicks, waitAsBuildTime);
    }

    public static void sendTitlePerChar(Player p, String msg, int waitTicks, int showTicks, int fadeInTicks, int fadeOutTicks) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, false, fadeInTicks, fadeOutTicks, false);
    }

    public static void sendSubTitlePerChar(Player p, String msg, int waitTicks, int showTicks, int fadeInTicks, int fadeOutTicks) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, true, fadeInTicks, fadeOutTicks, false);
    }

    public static void sendTitlePerChar(Player p, String msg, int waitTicks, int showTicks) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, false, waitTicks, waitTicks, false);
    }

    public static void sendSubTitlePerChar(Player p, String msg, int waitTicks, int showTicks) {
        sendTitlePerChar(p, msg, waitTicks, showTicks, true, waitTicks, waitTicks, false);
    }


    public static void sendFakeBlockUpdate(Player p, World world, int x, int y, int z, Material newMat) {
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld) world).getHandle(), new BlockPosition(x, y, z));
        packet.block = CraftMagicNumbers.getBlock(newMat).getBlockData();
        sendPacket(p, packet);
    }

    public static void sendFakeBlockUpdate(Player p, Location loc, Material newMat) {
        sendFakeBlockUpdate(p, loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), newMat);
    }

    public static void hidePlayer(Player viewer, Player target) {
        sendPacket(viewer, new PacketPlayOutEntityDestroy(target.getEntityId()));
    }

    public static void showPlayer(Player viewer, Player target) {
        sendPacket(viewer, new PacketPlayOutNamedEntitySpawn(((CraftPlayer) target).getHandle()));
    }
}