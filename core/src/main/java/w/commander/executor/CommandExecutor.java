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

import org.jetbrains.annotations.NotNull;
import w.commander.condition.Conditions;
import w.commander.execution.ExecutionContext;
import w.commander.result.Result;
import w.commander.util.Callback;

/**
 * @author whilein
 */
public interface CommandExecutor {

    static @NotNull CommandExecutor empty() {
        return EmptyCommandExecutor.INSTANCE;
    }

    void execute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    );

    default void test(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        getConditions().testVisibility(context, callback);
    }

    @NotNull
    default Conditions getConditions() {
        return Conditions.alwaysTrue();
    }

    default boolean tryYield(CommandExecutor executor) {
        return false;
    }

}
