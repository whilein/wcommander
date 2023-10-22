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
import lombok.val;
import w.commander.execution.ExecutionContext;
import w.commander.result.Result;
import w.commander.result.Results;
import w.commander.util.Callback;

import java.lang.invoke.MethodHandle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MethodHandleExecutor implements MethodExecutor {

    MethodHandle mh;

    @Override
    public void execute(ExecutionContext context, Callback<Result> callback, Object[] args) {
        Object result;
        try {
            result = mh.invokeWithArguments(args);
        } catch (Throwable e) {
            callback.completeExceptionally(e);
            return;
        }

        processReturnedValue(callback, result);
    }

    private void processReturnedValue(Callback<Result> callback, Object returnedValue) {
        if (returnedValue instanceof Result) {
            callback.complete((Result) returnedValue);
        } else if (returnedValue instanceof CompletableFuture<?>) {
            val future = (CompletableFuture<?>) returnedValue;

            future.whenComplete((v, t) -> {
                if (t != null) {
                    callback.completeExceptionally(t);
                    return;
                }

                processReturnedValue(callback, v);
            });
        } else if (returnedValue instanceof Supplier<?>) {
            final Object result;

            try {
                result = ((Supplier<?>) returnedValue).get();
            } catch (Throwable t) {
                callback.completeExceptionally(t);
                return;
            }

            processReturnedValue(callback, result);
        } else {
            callback.complete(Results.ok());
        }
    }

}
