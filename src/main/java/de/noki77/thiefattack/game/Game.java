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

import core.org.apache.commons.io.FileUtils;

import de.noki77.thiefattack.ThiefAttack;
import de.noki77.thiefattack.game.GamePlayer.PlayerTeam;
import de.noki77.thiefattack.util.NmsUtil;
import de.noki77.thiefattack.util.WorldHelper;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {
    private GameData data;
    private boolean timerDone = false;
    private List<GamePlayer> players = new CopyOnWriteArrayList<>();
    private GameMap activeMap;
    private GameMap lobby;

    private Scoreboard witnessScoreboard;
    private Scoreboard thiefScoreboard;

    public Game(GameData data) {
        this.data = data;
    }

    private Team getTeam(Scoreboard board, String team) {
        Team t = board.getTeam(team);
        if (t == null) {
            t = board.registerNewTeam(team);
        }
        return t;
    }

    public void updateScoreboards() {
        if (witnessScoreboard == null) {
            witnessScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        if (thiefScoreboard == null) {
            thiefScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        Team policemanTeamWn = getTeam(witnessScoreboard, "policemen");
        Team witnessTeamWn = getTeam(witnessScoreboard, "witness");

        Team witnessTeam = getTeam(thiefScoreboard, "witness");
        Team policemanTeam = getTeam(thiefScoreboard, "policemen");
        Team thiefTeam = getTeam(thiefScoreboard, "thief");

        witnessTeam.setPrefix("§4[KILL] §a");
        policemanTeam.setPrefix("§4[KILL] §1");
        thiefTeam.setPrefix("§2[FRIEND] §4");

        witnessTeamWn.setPrefix("§a");
        policemanTeamWn.setPrefix("§1");

        for (GamePlayer player : this.players) {
            thiefScoreboard.getTeam(player.getTeam().getTeamName()).addEntry(player.getParent().getName());
            if (player.getTeam() == PlayerTeam.POLICEMEN) {
                policemanTeamWn.addEntry(player.getParent().getName());
            } else {
                witnessTeamWn.addEntry(player.getParent().getName());
            }

            if (player.getTeam() == PlayerTeam.THIEVES) {
                player.getParent().setScoreboard(thiefScoreboard);
            } else {
                player.getParent().setScoreboard(witnessScoreboard);
            }
        }
    }

    public void prepareLobby() {
        if (!ThiefAttack.getInstance().getPluginConfig().isServerBased()) {
            File lobbyFile = new File(ThiefAttack.getInstance().getPluginConfig().getMapDirectory(),
                    ThiefAttack.getInstance().getPluginConfig().getLobbyMapName());
            File lobbyDestination = new File(getData().getGameWorldContainer(), getData().getGameName() + "Lobby");
            lobbyDestination.getAbsoluteFile().getParentFile().mkdirs();

            try {
                FileUtils.copyDirectory(lobbyFile, lobbyDestination);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.lobby = WorldHelper.loadMap(ThiefAttack.getInstance().getPluginConfig().getLobbyMapName(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void restart() {
        if (ThiefAttack.getInstance().getPluginConfig().isServerBased()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer("§3This lobby is §crestarting§3.");
            }
            Bukkit.getServer().shutdown();
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(getActiveMap().getParentWorld())) {
                    p.teleport(ThiefAttack.getInstance().getPluginConfig().getKickWorld().getSpawnLocation());
                    p.sendMessage("§3You have been teleported out of the world, because the game has endet.");
                }
            }

            ThiefAttack.getInstance().getMapRegistry().unloadMap(this.activeMap.getWorldName());
            if (getData().getGameWorldContainer().isDirectory()) {
                try {
                    FileUtils.deleteDirectory(getData().getGameWorldContainer());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startIntro() {

    }

    public void startTimer() {
        if (!timerDone) {
            timerDone = true;
            Bukkit.getScheduler().runTaskAsynchronously(ThiefAttack.getInstance(), () -> {
                int timeRemaining = ThiefAttack.getInstance().getPluginConfig().getSecondsTillStart();

                while (timeRemaining > 0) {
                    announceBroadcast(timeRemaining);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeRemaining--;
                }
            });
        }
    }

    private void announceTimer(CommandSender receiver, int time) {
        if (time / 60 >= 1) {
            if (time / 60 == 1) {
                receiver.sendMessage("§6Round starts in §3" + (time / 60) + " minute");
            } else {
                receiver.sendMessage("§6Round starts in §3" + (time / 60) + " minutes");
            }
        } else {
            if (time < 11 || time == 30) {
                if (ThiefAttack.getInstance().getPluginConfig().isTenSecondsTitle() && receiver instanceof Player) {
                    NmsUtil.sendTitle((Player) receiver, "§6Round starts in §3" + time + " second" + (time != 1 ? "s" : ""), 5, 10, 5);
                } else {
                    receiver.sendMessage("§6Round starts in §3" + time + " second" + (time != 1 ? "s" : ""));
                }

                if (receiver instanceof Player) {
                    ((Player) receiver).playSound(((Player) receiver).getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, .5F, 2F);
                }
            }
        }
    }

    public List<GamePlayer> getPlayers() {
        return this.players;
    }

    public GameMap getActiveMap() {
        return this.activeMap;
    }

    public GameData getData() {
        return this.data;
    }

    public void setActiveMap(GameMap activeMap) {
        this.activeMap = activeMap;
    }

    private void broadcast(String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
        }
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    private void announceBroadcast(int time) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            announceTimer(p, time);
        }
        announceTimer(Bukkit.getConsoleSender(), time);
    }
    
}