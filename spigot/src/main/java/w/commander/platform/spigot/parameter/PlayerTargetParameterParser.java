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

package w.commander.platform.spigot.parameter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import w.commander.parameter.AnnotatedParameterParser;
import w.commander.parameter.HandlerParameter;
import w.commander.platform.spigot.SpigotCommanderConfig;
import w.commander.platform.spigot.annotation.PlayerTarget;
import w.commander.platform.spigot.tabcomplete.PlayerTabCompleter;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerTargetParameterParser extends AnnotatedParameterParser<PlayerTarget> {

    SpigotCommanderConfig config;
    PlayerTabCompleter playerTabCompleter;

    public PlayerTargetParameterParser(SpigotCommanderConfig config, PlayerTabCompleter playerTabCompleter) {
        super(PlayerTarget.class);
        this.config = config;
        this.playerTabCompleter = playerTabCompleter;
    }

    @Override
    protected @NotNull HandlerParameter parse(@NotNull Parameter parameter, @NotNull PlayerTarget annotation) {
        val type = parameter.getType();
        if (!type.isAssignableFrom(Player.class)) {
            throw new IllegalArgumentException("@PlayerTarget annotation is not allowed on " + type.getName());
        }

        val argument = new PlayerArgument(
                annotation.value(),
                annotation.exact(),
                config
        );
        argument.getInfo().setTabCompleter(playerTabCompleter);

        return argument;
    }
}