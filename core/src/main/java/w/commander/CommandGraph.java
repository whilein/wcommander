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

package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.util.Immutables;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author whilein
 */
@Value
@ThreadSafe
@RequiredArgsConstructor
public class CommandGraph {

    CommandInfo info;
    List<CommandGraph> subCommands;

    private CommandGraph(CommandInfo info) {
        this(info, Collections.emptyList());
    }

    public static @NotNull CommandGraph.Builder builder(@NotNull CommandInfo info) {
        return new CommandGraph.Builder(info);
    }

    public static @NotNull CommandGraph of(@NotNull CommandInfo info) {
        return new CommandGraph(info);
    }

    @NotThreadSafe
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        CommandInfo info;

        List<CommandGraph> subCommands = new ArrayList<>();

        public @NotNull Builder subCommand(@NotNull CommandGraph template) {
            subCommands.add(template);
            return this;
        }

        public @NotNull Builder subCommand(@NotNull Object instance) {
            return subCommand(info.withInstance(instance));
        }

        public @NotNull Builder subCommand(@NotNull CommandInfo info) {
            return subCommand(new CommandGraph(info));
        }

        public @NotNull Builder subCommand(
                @NotNull Object instance,
                @NotNull Consumer<Builder> build
        ) {
            return subCommand(info.withInstance(instance), build);
        }

        public @NotNull Builder subCommand(
                @NotNull CommandInfo info,
                @NotNull Consumer<Builder> build
        ) {
            val builder = new Builder(info);
            build.accept(builder);
            return subCommand(builder.build());
        }

        public @NotNull CommandGraph build() {
            return new CommandGraph(
                    info,
                    Immutables.immutableList(subCommands)
            );
        }

    }


}
