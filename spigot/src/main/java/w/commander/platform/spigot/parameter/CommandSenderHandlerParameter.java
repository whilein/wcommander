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
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.argument.cursor.ArgumentCursor;
import w.commander.platform.spigot.SpigotExecutionContext;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandSenderHandlerParameter implements HandlerParameter {

    private static final HandlerParameter INSTANCE = new CommandSenderHandlerParameter();

    public static @NotNull HandlerParameter getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
        return ((SpigotExecutionContext<?>) context).getActor().getSender();
    }

}
