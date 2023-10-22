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

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import w.commander.Commander;
import w.commander.CommanderBuilderDelegate;
import w.commander.minecraft.MinecraftConditions;
import w.commander.minecraft.validator.NonSelfPlayerValidatorFactory;
import w.commander.platform.velocity.tabcomplete.PlayerTabCompleter;

/**
 * @author whilein
 */
@Getter
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class VelocityCommanderBuilderImpl
        extends CommanderBuilderDelegate<VelocityCommanderBuilderImpl>
        implements VelocityCommanderBuilder<VelocityCommanderBuilderImpl> {

    Object plugin;
    ProxyServer server;

    @NonFinal
    VelocityExecutionContextFactory executionContextFactory;

    @NonFinal
    VelocityCommandRegistrar commandRegistrar;

    @NonFinal
    VelocityErrorResultFactory velocityErrorResultFactory;

    @NonFinal
    VelocityCommandActorFactory velocityCommandActorFactory;

    @NonFinal
    boolean includeDefaultConditions;

    @NonFinal
    boolean includeDefaultValidators;

    @NonFinal
    boolean includeDefaultTabCompleters;

    public VelocityCommanderBuilderImpl(Object plugin, ProxyServer server) {
        super(Commander.builder());

        this.plugin = plugin;
        this.server = server;

        this.includeDefaultConditions = true;
        this.includeDefaultValidators = true;
        this.includeDefaultTabCompleters = true;
        this.velocityErrorResultFactory = VelocityErrorResultFactory.NOOP;
        this.velocityCommandActorFactory = SimpleVelocityCommandActor::new;
        this.commandRegistrar = new VelocityCommandRegistrar(plugin, server.getCommandManager());

        executionContextFactory(new VelocityExecutionContextFactory());

        addParentCustomizer(parent -> {
            parent.addParameterParser(new VelocityParameterParser(server, velocityErrorResultFactory));

            if (includeDefaultConditions) {
                MinecraftConditions.install(parent, velocityErrorResultFactory);
            }

            if (includeDefaultValidators) {
                parent.addArgumentValidatorFactory(new NonSelfPlayerValidatorFactory(CommandSource.class,
                        velocityErrorResultFactory));
            }

            if (includeDefaultTabCompleters) {
                parent.addTabCompleter(new PlayerTabCompleter(server));
            }

            commandRegistrar.setVelocityCommandActorFactory(velocityCommandActorFactory);
            parent.commandRegistrar(commandRegistrar);
        });
    }

    @Override
    public @NotNull VelocityCommanderBuilderImpl executionContextFactory(
            @NotNull VelocityExecutionContextFactory factory
    ) {
        delegate.executionContextFactory(this.executionContextFactory = factory);
        return this;
    }


}
