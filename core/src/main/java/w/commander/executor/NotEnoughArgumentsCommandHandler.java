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
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.execution.ExecutionContext;
import w.commander.result.Result;
import w.commander.util.Callback;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class NotEnoughArgumentsCommandHandler implements CommandHandler {

    @Delegate(types = CommandHandler.class, excludes = CommandExecutor.class)
    CommandHandler handler;

    CommanderConfig config;

    @Override
    public void execute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        handler.getConditions().test(context, callback.map(result -> result == null || result.isSuccess()
                ? config.getErrorResultFactory().onNotEnoughArguments(handler.getManualEntry().getUsage())
                : result));
    }
}
