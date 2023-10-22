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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderBuilderDelegate;
import w.commander.platform.spigot.SpigotCommandActorFactory;
import w.commander.platform.spigot.SpigotCommandRegistrar;
import w.commander.platform.spigot.SpigotCommander;
import w.commander.platform.spigot.SpigotCommanderSetup;
import w.commander.platform.spigot.SpigotErrorResultFactory;

/**
 * @author whilein
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class PaperCommanderBuilderImpl
        extends CommanderBuilderDelegate<PaperCommanderBuilderImpl>
        implements PaperCommanderBuilder<PaperCommanderBuilderImpl> {

    Plugin plugin;

    @NonFinal
    PaperExecutionContextFactory paperExecutionContextFactory;

    @NonFinal
    PaperCommandActorFactory paperCommandActorFactory;

    public PaperCommanderBuilderImpl(Plugin plugin) {
        super(SpigotCommander.builder(plugin));

        this.plugin = plugin;

        paperCommandActorFactory(SimplePaperCommandActor::new);
        paperExecutionContextFactory(new PaperExecutionContextFactory());
    }

    private SpigotCommanderSetup<?> delegate() {
        return (SpigotCommanderSetup<?>) delegate;
    }

    @Override
    public @NotNull PaperCommanderBuilderImpl paperCommandActorFactory(@NotNull PaperCommandActorFactory paperCommandActorFactory) {
        delegate().spigotCommandActorFactory(this.paperCommandActorFactory = paperCommandActorFactory);
        return this;
    }

    @Override
    public @NotNull PaperCommanderBuilderImpl paperExecutionContextFactory(
            @NotNull PaperExecutionContextFactory factory
    ) {
        delegate().spigotExecutionContextFactory(this.paperExecutionContextFactory = factory);
        return this;
    }


    @Override
    public @NotNull PaperCommanderBuilderImpl spigotErrorResultFactory(@NotNull SpigotErrorResultFactory errorResultFactory) {
        delegate().spigotErrorResultFactory(errorResultFactory);
        return this;
    }

    @Override
    public @NotNull PaperCommanderBuilderImpl includeDefaultConditions(boolean includeDefaultConditions) {
        delegate().includeDefaultConditions(includeDefaultConditions);
        return this;
    }

    @Override
    public @NotNull PaperCommanderBuilderImpl includeDefaultValidators(boolean includeDefaultValidators) {
        delegate().includeDefaultArgumentValidators(includeDefaultValidators);
        return this;
    }

    @Override
    public @NotNull PaperCommanderBuilderImpl includeDefaultTabCompleters(boolean includeDefaultTabCompleters) {
        delegate().includeDefaultTabCompleters();
        return this;
    }

    @Override
    public @NotNull PaperCommanderBuilderImpl spigotCommandRegistrar(@NotNull SpigotCommandRegistrar commandRegistrar) {
        delegate().spigotCommandRegistrar(commandRegistrar);
        return this;
    }

    @Override
    public @NotNull SpigotErrorResultFactory spigotErrorResultFactory() {
        return delegate().spigotErrorResultFactory();
    }

    @Override
    public @NotNull SpigotCommandActorFactory spigotCommandActorFactory() {
        return delegate().spigotCommandActorFactory();
    }

    @Override
    public @NotNull SpigotCommandRegistrar spigotCommandRegistrar() {
        return delegate().spigotCommandRegistrar();
    }

    @Override
    public boolean includeDefaultConditions() {
        return delegate().includeDefaultConditions();
    }

    @Override
    public boolean includeDefaultValidators() {
        return delegate().includeDefaultValidators();
    }

    @Override
    public boolean includeDefaultTabCompleters() {
        return delegate().includeDefaultTabCompleters();
    }
}
