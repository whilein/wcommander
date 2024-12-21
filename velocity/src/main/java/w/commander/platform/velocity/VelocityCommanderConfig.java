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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContextFactory;
import w.commander.minecraft.MinecraftCommanderConfig;
import w.commander.minecraft.MinecraftErrorResultFactory;
import w.commander.minecraft.validator.NonSelfPlayerValidatorFactory;
import w.commander.platform.velocity.tabcomplete.PlayerTabCompleter;

/**
 * @author whilein
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class VelocityCommanderConfig extends MinecraftCommanderConfig {

    ProxyServer server;
    @NonFinal
    VelocityErrorResultFactory velocityErrorResultFactory;
    @NonFinal
    VelocityCommandActorFactory velocityCommandActorFactory;

    public static VelocityCommanderConfig createDefaults(ProxyServer server) {
        val config = new VelocityCommanderConfig(server);
        config.initDefaults();

        return config;
    }

    @Override
    public MinecraftErrorResultFactory getMinecraftErrorResultFactory() {
        return getVelocityErrorResultFactory();
    }

    @Override
    public void setMinecraftErrorResultFactory(MinecraftErrorResultFactory factory) {
        if (!(factory instanceof VelocityErrorResultFactory)) {
            throw new UnsupportedOperationException();
        }

        setVelocityErrorResultFactory((VelocityErrorResultFactory) factory);
    }

    public void setMinecraftErrorResultFactory(VelocityErrorResultFactory factory) {
        this.velocityErrorResultFactory = factory;
    }

    @Override
    protected void initDefaults() {
        super.initDefaults();

        commandRegistrar = new PluginPerCommandVelocityCommandRegistrar(server.getCommandManager(), this);
        velocityErrorResultFactory = VelocityErrorResultFactory.NOOP;
        velocityCommandActorFactory = SimpleVelocityCommandActor::new;
        executionContextFactory = new VelocityExecutionContextFactory();

        addParameterParser(new VelocityParameterParser(server, this));

        addArgumentValidatorFactory(new NonSelfPlayerValidatorFactory(CommandSource.class,
                this));

        addTabCompleter(new PlayerTabCompleter(server));
    }

    public void setExecutionContextFactory(
            @NotNull VelocityExecutionContextFactory factory
    ) {
        super.setExecutionContextFactory(factory);
    }

    @Override
    public void setExecutionContextFactory(ExecutionContextFactory factory) {
        if (!(factory instanceof VelocityExecutionContextFactory)) {
            throw new UnsupportedOperationException();
        }

        this.setExecutionContextFactory((VelocityExecutionContextFactory) factory);
    }
}
