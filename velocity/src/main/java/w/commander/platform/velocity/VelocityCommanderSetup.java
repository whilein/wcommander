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

package w.commander.platform.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.CommandRegistrar;
import w.commander.CommanderSetup;
import w.commander.execution.ExecutionContextFactory;

/**
 * @author whilein
 */
public interface VelocityCommanderSetup<S extends VelocityCommanderSetup<S>>
        extends CommanderSetup<S>, VelocityCommanderSetupRead {

    @Nullable
    Object plugin();

    @NotNull
    ProxyServer server();

    @NotNull
    S velocityErrorResultFactory(@NotNull VelocityErrorResultFactory errorResultFactory);

    @NotNull
    S velocityCommandActorFactory(@NotNull VelocityCommandActorFactory velocityCommandActorFactory);

    @NotNull
    S includeDefaultConditions(boolean includeDefaultConditions);

    @NotNull
    S includeDefaultValidators(boolean includeDefaultValidators);

    @NotNull
    S includeDefaultTabCompleters(boolean includeDefaultTabCompleters);

    @Override
    default @NotNull S executionContextFactory(@NotNull ExecutionContextFactory factory) {
        if (!(factory instanceof VelocityExecutionContextFactory)) {
            throw new UnsupportedOperationException();
        }

        return executionContextFactory((VelocityExecutionContextFactory) factory);
    }

    @NotNull
    S executionContextFactory(@NotNull VelocityExecutionContextFactory factory);

    @NotNull
    S commandRegistrar(@NotNull VelocityCommandRegistrar commandRegistrar);

    @Override
    default @NotNull S commandRegistrar(@NotNull CommandRegistrar commandRegistrar) {
        if (!(commandRegistrar instanceof VelocityCommandRegistrar)) {
            throw new UnsupportedOperationException();
        }

        return commandRegistrar((VelocityCommandRegistrar) commandRegistrar);
    }

}
