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

package w.commander.platform.spigot.argument;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.AbstractArgument;
import w.commander.parameter.argument.cursor.ArgumentCursor;
import w.commander.platform.spigot.SpigotCommanderConfig;
import w.commander.platform.spigot.SpigotExecutionContext;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class WorldArgument extends AbstractArgument {

    @Getter
    String name;

    @Getter
    boolean required;

    SpigotCommanderConfig config;

    @Override
    public Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
        if (cursor.hasNext(required)) {
            val name = context.getRawArguments().value(cursor.next());

            val world = Bukkit.getWorld(name);
            return world == null
                    ? config.getSpigotErrorResultFactory().onUnknownWorld((SpigotExecutionContext) context, name)
                    : world;
        }

        return null;
    }
}