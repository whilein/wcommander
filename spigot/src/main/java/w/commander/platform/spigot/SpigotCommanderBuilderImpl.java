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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import w.commander.Commander;
import w.commander.CommanderBuilderDelegate;
import w.commander.minecraft.MinecraftConditions;
import w.commander.minecraft.validator.NonSelfPlayerValidatorFactory;
import w.commander.platform.spigot.tabcomplete.PlayerTabCompleter;
import w.commander.platform.spigot.tabcomplete.WorldTabCompleter;

/**
 * @author whilein
 */
@Getter
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SpigotCommanderBuilderImpl
        extends CommanderBuilderDelegate<SpigotCommanderBuilderImpl>
        implements SpigotCommanderBuilder<SpigotCommanderBuilderImpl> {

    Plugin plugin;

    @NonFinal
    SpigotExecutionContextFactory spigotExecutionContextFactory;

    @NonFinal
    SpigotCommandRegistrar spigotCommandRegistrar;

    @NonFinal
    SpigotErrorResultFactory spigotErrorResultFactory;

    @NonFinal
    SpigotCommandActorFactory spigotCommandActorFactory;

    @NonFinal
    boolean includeDefaultConditions;

    @NonFinal
    boolean includeDefaultValidators;

    @NonFinal
    boolean includeDefaultTabCompleters;

    public SpigotCommanderBuilderImpl(Plugin plugin) {
        super(Commander.builder());

        this.plugin = plugin;

        this.includeDefaultConditions = true;
        this.includeDefaultValidators = true;
        this.includeDefaultTabCompleters = true;
        this.spigotErrorResultFactory = SpigotErrorResultFactory.NOOP;
        this.spigotCommandActorFactory = SimpleSpigotCommandActor::new;
        this.spigotCommandRegistrar = new SpigotCommandRegistrar(plugin);

        spigotExecutionContextFactory(new SpigotExecutionContextFactory());

        addParentCustomizer(parent -> {
            val server = plugin.getServer();

            PlayerTabCompleter playerTabCompleter = null;
            WorldTabCompleter worldTabCompleter = null;

            if (includeDefaultTabCompleters) {
                parent.addTabCompleter(playerTabCompleter = new PlayerTabCompleter(server));
                parent.addTabCompleter(worldTabCompleter = new WorldTabCompleter(server));
            }

            parent.addParameterParser(new SpigotParameterParser(server, spigotErrorResultFactory,
                    playerTabCompleter, worldTabCompleter));

            if (includeDefaultConditions) {
                MinecraftConditions.install(parent, spigotErrorResultFactory);
            }

            if (includeDefaultValidators) {
                parent.addArgumentValidatorFactory(new NonSelfPlayerValidatorFactory(CommandSender.class,
                        spigotErrorResultFactory));
            }

            spigotCommandRegistrar.setSpigotCommandActorFactory(spigotCommandActorFactory);
            parent.commandRegistrar(spigotCommandRegistrar);
        });
    }

    @Override
    public @NotNull SpigotCommanderBuilderImpl spigotExecutionContextFactory(
            @NotNull SpigotExecutionContextFactory factory
    ) {
        delegate.executionContextFactory(this.spigotExecutionContextFactory = factory);
        return this;
    }

}
