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

import org.bukkit.entity.Player;

public class GamePlayer {
    private Player parent;
    private PlayerTeam team;

    public GamePlayer(Player parent, PlayerTeam team) {
        this.parent = parent;
        this.team = team;
    }

    public PlayerTeam getTeam() {
        return this.team;
    }

    public void setTeam(PlayerTeam team) {
        this.team = team;
    }

    public Player getParent() {
        return this.parent;
    }

    public static enum PlayerTeam {
        POLICEMEN("policemen"),
        WITNESSES("witness"),
        THIEVES("thief");

        private String teamName;

        PlayerTeam(String teamName) {
            this.teamName = teamName;
        }

        public String getTeamName() {
            return this.teamName;
        }
    }
}
