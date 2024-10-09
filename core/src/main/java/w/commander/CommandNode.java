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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.executor.CommandExecutor;
import w.commander.executor.CommandSetupHandlers;
import w.commander.manual.Manual;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@Immutable
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class CommandNode {

    @NotNull CommandExecutor[] executors;

    @Getter
    @NotNull CommandSetupHandlers setupHandlers;

    @NotNull Map<String, @NotNull CommandNode> subCommands;

    @Getter
    @Nullable Manual manual;

    public CommandNode(CommandExecutor executor) {
        this(new CommandExecutor[]{executor}, CommandSetupHandlers.empty(), Collections.emptyMap(), null);
    }

    @Contract(pure = true)
    public @Nullable CommandNode subCommand(@NotNull String name) {
        return subCommands.get(name);
    }

    @Contract(pure = true)
    public @NotNull Set<@NotNull String> subCommands() {
        return subCommands.keySet();
    }

    public void forEach(BiConsumer<String, CommandNode> fn) {
        subCommands.forEach(fn);
    }

    @Contract(pure = true)
    public @NotNull CommandExecutor executor(int arguments) {
        val executors = this.executors;
        val maxArguments = executors.length;

        if (arguments >= maxArguments) {
            return executors[maxArguments - 1];
        }

        return executors[arguments];
    }

}
