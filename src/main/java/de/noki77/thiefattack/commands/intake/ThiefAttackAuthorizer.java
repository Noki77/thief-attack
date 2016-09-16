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

package de.noki77.thiefattack.commands.intake;

import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.util.auth.Authorizer;

import org.bukkit.command.CommandSender;

public class ThiefAttackAuthorizer implements Authorizer {
    private static final ThiefAttackAuthorizer INSTANCE = new ThiefAttackAuthorizer();
    
    private ThiefAttackAuthorizer() {}

    @Override
    public boolean testPermission(Namespace namespace, String s) {
        return namespace.containsKey(CommandSender.class) && namespace.get(CommandSender.class).hasPermission(s);
    }

    public static ThiefAttackAuthorizer getInstance() {
        return INSTANCE;
    }
}