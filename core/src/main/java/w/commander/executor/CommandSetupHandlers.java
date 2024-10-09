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

package w.commander.executor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.result.Result;
import w.commander.result.Results;
import w.commander.util.Callback;

import java.util.List;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandSetupHandlers {

    private static final CommandSetupHandlers EMPTY = new CommandSetupHandlers(new CommandSetupHandler[0]);

    CommandSetupHandler[] handlers;

    public static CommandSetupHandlers of(List<CommandSetupHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) return EMPTY;

        return new CommandSetupHandlers(handlers.toArray(new CommandSetupHandler[0]));
    }

    public static CommandSetupHandlers empty() {
        return EMPTY;
    }

    private static void setupRecursive(
            ExecutionContext ctx,
            Callback<Result> callback,
            CommandSetupHandler[] handlers,
            int index
    ) {
        if (handlers.length == index) {
            callback.complete(Results.ok());
            return;
        }

        handlers[index].execute(ctx, Callback.of((result, cause) -> {
            if (cause != null) {
                callback.completeExceptionally(cause);
            } else if (result != null && !result.isSuccess()) {
                callback.complete(result);
            } else {
                setupRecursive(ctx, callback, handlers, index + 1);
            }
        }));
    }

    public void setup(@NotNull ExecutionContext ctx, @NotNull Callback<Result> callback) {
        setupRecursive(ctx, callback, handlers, 0);
    }

}
