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

package w.commander.platform.paper;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import w.commander.platform.spigot.SpigotCommandActorFactory;
import w.commander.platform.spigot.SpigotCommanderSetup;
import w.commander.platform.spigot.SpigotExecutionContextFactory;

/**
 * @author whilein
 */
public interface PaperCommanderSetup<S extends PaperCommanderSetup<S>>
        extends SpigotCommanderSetup<S>, PaperCommanderSetupRead {

    @NotNull
    Plugin plugin();

    @NotNull
    @Deprecated
    default S spigotCommandActorFactory(@NotNull SpigotCommandActorFactory commandActorFactory) {
        if (!(commandActorFactory instanceof PaperCommandActorFactory)) {
            throw new UnsupportedOperationException();
        }

        return paperCommandActorFactory((PaperCommandActorFactory) commandActorFactory);
    }

    @NotNull
    S paperCommandActorFactory(@NotNull PaperCommandActorFactory paperCommandActorFactory);

    @Override
    @Deprecated
    default @NotNull S spigotExecutionContextFactory(@NotNull SpigotExecutionContextFactory factory) {
        if (!(factory instanceof PaperExecutionContextFactory)) {
            throw new UnsupportedOperationException();
        }

        return paperExecutionContextFactory((PaperExecutionContextFactory) factory);
    }

    @NotNull
    S paperExecutionContextFactory(@NotNull PaperExecutionContextFactory factory);

}
