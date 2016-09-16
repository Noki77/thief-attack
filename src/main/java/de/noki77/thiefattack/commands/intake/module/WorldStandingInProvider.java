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

package de.noki77.thiefattack.commands.intake.module;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class WorldStandingInProvider implements Provider<World> {
    private static final WorldStandingInProvider INSTANCE = new WorldStandingInProvider();

    private WorldStandingInProvider() {}

    @Override
    public boolean isProvided() {
        return true;
    }

    @Nullable
    @Override
    public World get(CommandArgs commandArgs, List<? extends Annotation> list) throws ArgumentException, ProvisionException {
        CommandSender sender = commandArgs.getNamespace().get(CommandSender.class);
        if (sender == null) {
            throw new ProvisionException("§cSender not given!");
        } else if (!(sender instanceof Player)) {
            throw new ProvisionException("§cSender is no player!");
        }

        return ((Player) sender).getLocation().getWorld();
    }

    @Override
    public List<String> getSuggestions(String s) {
        return Collections.emptyList();
    }

    public static WorldStandingInProvider getInstance() {
        return INSTANCE;
    }
}
