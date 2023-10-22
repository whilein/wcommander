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

import org.jetbrains.annotations.NotNull;
import w.commander.CommandRegistrar;
import w.commander.CommanderSetupRead;
import w.commander.execution.ExecutionContextFactory;

/**
 * @author whilein
 */
public interface SpigotCommanderSetupRead extends CommanderSetupRead {
    @NotNull
    SpigotErrorResultFactory spigotErrorResultFactory();

    @NotNull
    SpigotCommandActorFactory spigotCommandActorFactory();

    @NotNull
    SpigotExecutionContextFactory spigotExecutionContextFactory();

    @NotNull
    SpigotCommandRegistrar spigotCommandRegistrar();

    @Override
    @Deprecated
    default @NotNull ExecutionContextFactory executionContextFactory() {
        return spigotExecutionContextFactory();
    }

    @Override
    default @NotNull CommandRegistrar commandRegistrar() {
        return spigotCommandRegistrar();
    }

    boolean includeDefaultConditions();

    boolean includeDefaultValidators();

    boolean includeDefaultTabCompleters();

}
