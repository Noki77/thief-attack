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

import com.sk89q.intake.Intake;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.fluent.DispatcherNode;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;

import de.noki77.thiefattack.commands.intake.ThiefAttackAuthorizer;
import de.noki77.thiefattack.commands.intake.module.ThiefAttackModule;
import de.noki77.thiefattack.commands.AdminCommands;
import de.noki77.thiefattack.commands.PolicemanCommands;
import de.noki77.thiefattack.commands.ThiefCommands;
import de.noki77.thiefattack.commands.UserCommands;
import de.noki77.thiefattack.commands.WitnessCommands;

public class ThiefAttackCommand {
    private static final ParametricBuilder DEFAULT_BUILDER;

    static {
        Injector inj = Intake.createInjector();
        inj.install(new PrimitivesModule());
        inj.install(new ThiefAttackModule());

        DEFAULT_BUILDER = new ParametricBuilder(inj);
        DEFAULT_BUILDER.setAuthorizer(ThiefAttackAuthorizer.getInstance());
    }

    public static Dispatcher build(ParametricBuilder builder) {
        ParametricBuilder used = builder != null ? builder : getDefaultBuilder();

        Dispatcher mainDispatcher = buildDispatcher(used, new UserCommands(), new WitnessCommands(), new ThiefCommands(), new PolicemanCommands());
        Dispatcher adminDispatcher = buildDispatcher(used, new AdminCommands());

        mainDispatcher.registerCommand(adminDispatcher, "thiefattack", "ta");

        return mainDispatcher;
    }

    public static Dispatcher build() {
        return build(null);
    }

    private static Dispatcher buildDispatcher(ParametricBuilder builder, Object... methodClasses) {
        DispatcherNode node = new CommandGraph().builder(builder).commands();
        for (Object j : methodClasses) {
            node = node.registerMethods(j);
        }

        return node.graph().getDispatcher();
    }

    public static ParametricBuilder getDefaultBuilder() {
        return DEFAULT_BUILDER;
    }
}