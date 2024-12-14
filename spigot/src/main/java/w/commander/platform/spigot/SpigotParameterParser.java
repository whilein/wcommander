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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import w.commander.parameter.AbstractParameterParser;
import w.commander.parameter.HandlerParameter;
import w.commander.platform.spigot.annotation.PlayerTarget;
import w.commander.platform.spigot.annotation.WorldTarget;
import w.commander.platform.spigot.argument.PlayerArgument;
import w.commander.platform.spigot.argument.WorldArgument;
import w.commander.platform.spigot.parameter.CommandSenderHandlerParameter;
import w.commander.platform.spigot.tabcomplete.PlayerTabCompleter;
import w.commander.platform.spigot.tabcomplete.WorldTabCompleter;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SpigotParameterParser extends AbstractParameterParser {

    Server server;
    SpigotCommanderConfig config;

    PlayerTabCompleter playerTabCompleter;
    WorldTabCompleter worldTabCompleter;

    @Override
    public boolean isSupported(@NotNull Parameter parameter) {
        return parameter.isAnnotationPresent(PlayerTarget.class)
               || parameter.isAnnotationPresent(WorldTarget.class)
               || CommandSender.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public @NotNull HandlerParameter parse(@NotNull Parameter parameter) {
        val type = parameter.getType();

        val playerTarget = parameter.getDeclaredAnnotation(PlayerTarget.class);
        if (playerTarget != null) {
            if (!type.isAssignableFrom(Player.class)) {
                throw new IllegalArgumentException("@PlayerTarget annotation is not allowed on " + type.getName());
            }

            val argument = new PlayerArgument(
                    playerTarget.value(),
                    isRequired(parameter),
                    playerTarget.exact(),
                    server,
                    config
            );
            argument.setTabCompleter(playerTabCompleter);

            return argument;
        }

        val worldTarget = parameter.getDeclaredAnnotation(WorldTarget.class);
        if (worldTarget != null) {
            if (!type.isAssignableFrom(World.class)) {
                throw new IllegalArgumentException("@WorldTarget annotation is not allowed on " + type.getName());
            }

            val argument = new WorldArgument(
                    worldTarget.value(),
                    isRequired(parameter),
                    server,
                    config
            );
            argument.setTabCompleter(worldTabCompleter);

            return argument;
        }

        if (CommandSender.class.isAssignableFrom(type)) {
            return CommandSenderHandlerParameter.getInstance();
        }

        throw new IllegalArgumentException("Unsupported parameter: " + parameter);
    }
}
