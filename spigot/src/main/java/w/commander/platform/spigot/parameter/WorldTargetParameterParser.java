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
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import w.commander.parameter.AnnotatedParameterParser;
import w.commander.parameter.HandlerParameter;
import w.commander.platform.spigot.SpigotCommanderConfig;
import w.commander.platform.spigot.annotation.WorldTarget;
import w.commander.platform.spigot.tabcomplete.WorldTabCompleter;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorldTargetParameterParser extends AnnotatedParameterParser<WorldTarget> {

    SpigotCommanderConfig config;
    WorldTabCompleter worldTabCompleter;

    public WorldTargetParameterParser(SpigotCommanderConfig config, WorldTabCompleter worldTabCompleter) {
        super(WorldTarget.class);
        this.config = config;
        this.worldTabCompleter = worldTabCompleter;
    }

    @Override
    protected @NotNull HandlerParameter parse(@NotNull Parameter parameter, @NotNull WorldTarget annotation) {
        val type = parameter.getType();
        if (!type.isAssignableFrom(World.class)) {
            throw new IllegalArgumentException("@WorldTarget annotation is not allowed on " + type.getName());
        }

        val argument = new WorldArgument(
                annotation.value(),
                config
        );
        argument.getInfo().setTabCompleter(worldTabCompleter);

        return argument;
    }
}
