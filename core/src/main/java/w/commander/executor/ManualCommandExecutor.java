/*
 *    Copyright 2025 Whilein
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.condition.Conditions;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContext;
import w.commander.manual.Manual;
import w.commander.manual.ManualFormatter;
import w.commander.result.Result;
import w.commander.util.Callback;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class ManualCommandExecutor implements CommandExecutor {

    Manual manual;
    ManualFormatter manualFormatter;

    @Getter
    Conditions conditions;

    ErrorResultFactory errorResultFactory;

    @Override
    public void execute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        val manual = this.manual;

        conditions.test(context, Callback.of((result, cause) -> {
            if (cause != null) {
                callback.completeExceptionally(cause);
            } else if (result != null && !result.isSuccess()) {
                callback.complete(errorResultFactory.onManualUnavailable(context, manual));
            } else {
                callback.complete(manualFormatter.format(context, new Manual(manual.name(), manual.entries())));
            }
        }));
    }

    @Override
    public boolean tryYield(CommandExecutor executor) {
        return true;
    }

}
