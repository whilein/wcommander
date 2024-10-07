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

package w.commander.condition;

import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.result.Result;
import w.commander.result.Results;
import w.commander.util.Callback;
import w.commander.util.Ordered;

/**
 * @author whilein
 */
public interface Condition extends Ordered {

    default void testAsync(@NotNull ExecutionContext ctx, @NotNull Callback<@NotNull Result> callback) {
        try {
            callback.complete(test(ctx));
        } catch (Throwable t) {
            callback.completeExceptionally(t);
        }
    }

    default @NotNull Result test(@NotNull ExecutionContext ctx) {
        return Results.ok();
    }

    default boolean shouldCheckForVisibility() {
        return false;
    }

}