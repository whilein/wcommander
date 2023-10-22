/*
 *    Copyright 2024 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package w.commander.platform.spigot;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandRegistrar;
import w.commander.CommanderSetup;
import w.commander.execution.ExecutionContextFactory;

/**
 * @author whilein
 */
public interface SpigotCommanderSetup<S extends SpigotCommanderSetup<S>>
        extends CommanderSetup<S>, SpigotCommanderSetupRead {

    @NotNull
    Plugin plugin();

    @NotNull
    S spigotErrorResultFactory(@NotNull SpigotErrorResultFactory errorResultFactory);

    @NotNull
    S spigotCommandActorFactory(@NotNull SpigotCommandActorFactory spigotCommandActorFactory);

    @NotNull
    S includeDefaultConditions(boolean includeDefaultConditions);

    @NotNull
    S includeDefaultValidators(boolean includeDefaultValidators);

    @NotNull
    S includeDefaultTabCompleters(boolean includeDefaultTabCompleters);

    @Override
    @Deprecated
    default @NotNull S executionContextFactory(@NotNull ExecutionContextFactory factory) {
        if (!(factory instanceof SpigotExecutionContextFactory)) {
            throw new UnsupportedOperationException();
        }

        return spigotExecutionContextFactory((SpigotExecutionContextFactory) factory);
    }

    @NotNull
    S spigotExecutionContextFactory(@NotNull SpigotExecutionContextFactory factory);

    @NotNull
    S spigotCommandRegistrar(@NotNull SpigotCommandRegistrar commandRegistrar);

    @Override
    @Deprecated
    default @NotNull S commandRegistrar(@NotNull CommandRegistrar commandRegistrar) {
        if (!(commandRegistrar instanceof SpigotCommandRegistrar)) {
            throw new UnsupportedOperationException();
        }

        return spigotCommandRegistrar((SpigotCommandRegistrar) commandRegistrar);
    }

}
