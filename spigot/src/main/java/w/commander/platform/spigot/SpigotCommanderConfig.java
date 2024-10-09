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
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.bukkit.command.CommandSender;
import w.commander.minecraft.MinecraftCommanderConfig;
import w.commander.minecraft.MinecraftErrorResultFactory;
import w.commander.minecraft.validator.NonSelfPlayerValidatorFactory;
import w.commander.parameter.type.CommandActorParameterParser;
import w.commander.platform.spigot.parameter.CommandSenderParameterParser;
import w.commander.platform.spigot.parameter.PlayerTargetParameterParser;
import w.commander.platform.spigot.parameter.WorldTargetParameterParser;
import w.commander.platform.spigot.tabcomplete.PlayerTabCompleter;
import w.commander.platform.spigot.tabcomplete.WorldTabCompleter;

/**
 * @author whilein
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class SpigotCommanderConfig extends MinecraftCommanderConfig {

    @NonFinal
    SpigotErrorResultFactory spigotErrorResultFactory;

    @NonFinal
    SpigotCommandActorFactory spigotCommandActorFactory;

    public static SpigotCommanderConfig createDefaults() {
        val config = new SpigotCommanderConfig();
        config.initDefaults();

        return config;
    }

    @Override
    public MinecraftErrorResultFactory getMinecraftErrorResultFactory() {
        return getSpigotErrorResultFactory();
    }

    @Override
    public void setMinecraftErrorResultFactory(MinecraftErrorResultFactory factory) {
        if (!(factory instanceof SpigotErrorResultFactory)) {
            throw new UnsupportedOperationException();
        }

        setSpigotErrorResultFactory((SpigotErrorResultFactory) factory);
    }

    public void setMinecraftErrorResultFactory(SpigotErrorResultFactory factory) {
        this.spigotErrorResultFactory = factory;
    }

    @Override
    protected void initDefaults() {
        super.initDefaults();

        commandRegistrar = new SpigotCommandRegistrar(this);
        spigotErrorResultFactory = SpigotErrorResultFactory.NOOP;
        spigotCommandActorFactory = SimpleSpigotCommandActor::new;

        val playerTabCompleter = new PlayerTabCompleter();
        val worldTabCompleter = new WorldTabCompleter();

        addTabCompleter(playerTabCompleter);
        addTabCompleter(worldTabCompleter);

        addTypedParameterParser(new CommandSenderParameterParser());
        addAnnotatedParameterParser(new PlayerTargetParameterParser(this, playerTabCompleter));
        addAnnotatedParameterParser(new WorldTargetParameterParser(this, worldTabCompleter));
        addArgumentValidatorFactory(new NonSelfPlayerValidatorFactory(CommandSender.class,
                this));
    }
}
