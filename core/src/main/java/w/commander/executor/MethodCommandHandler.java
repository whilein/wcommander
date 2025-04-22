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
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.condition.Conditions;
import w.commander.execution.ExecutionContext;
import w.commander.manual.ManualEntry;
import w.commander.parameter.HandlerParameters;
import w.commander.result.Result;
import w.commander.util.Callback;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MethodCommandHandler extends AbstractCommandHandler {

    MethodExecutor methodExecutor;

    public MethodCommandHandler(
            HandlerPath path,
            ManualEntry manualEntry,
            HandlerParameters parameters,
            Conditions conditions,
            MethodExecutor methodExecutor
    ) {
        super(path, parameters, manualEntry, conditions);

        this.methodExecutor = methodExecutor;
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public void execute(@NotNull ExecutionContext context, @NotNull Callback<@NotNull Result> callback) {
        conditions.test(context, Callback.of((result, cause) -> {
            if (cause != null) {
                callback.completeExceptionally(cause);
            } else if (result != null && !result.isSuccess()) {
                callback.complete(result);
            } else {
                val invocation = new MethodInvocation(
                        parameters,
                        methodExecutor,
                        context,
                        callback
                );
                invocation.process();
            }
        }));
    }

}
