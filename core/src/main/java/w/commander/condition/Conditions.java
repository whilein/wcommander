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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;
import w.commander.result.Result;
import w.commander.result.Results;
import w.commander.util.Callback;
import w.commander.util.Immutables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Conditions {

    private static final Conditions EMPTY = new Conditions();

    Condition[] conditions;
    Condition[] visibilityConditions;

    private Conditions() {
        conditions = visibilityConditions = new Condition[0];
    }

    public static @NotNull Conditions empty() {
        return EMPTY;
    }

    public static @NotNull Conditions from(@NotNull Condition @NotNull ... conditions) {
        if (conditions.length == 0) {
            // memory optimization :0
            return EMPTY;
        }

        return from0(Arrays.copyOf(conditions, conditions.length));
    }

    private static Conditions from0(Condition... conditions) {
        Arrays.sort(conditions, Comparator.naturalOrder());

        return new Conditions(
                conditions,
                Arrays.stream(conditions)
                        .filter(Condition::shouldCheckForVisibility)
                        .toArray(Condition[]::new)
        );
    }

    public @NotNull List<@NotNull Condition> getConditions() {
        return Immutables.immutableList(conditions);
    }

    public @NotNull List<@NotNull Condition> getVisibilityConditions() {
        return Immutables.immutableList(visibilityConditions);
    }

    public @NotNull Conditions visibilityConditions() {
        return new Conditions(visibilityConditions, visibilityConditions);
    }

    public @NotNull Conditions merge(@NotNull Conditions another) {
        if (isEmpty() && another.isEmpty()) return EMPTY;

        val conditions = this.conditions;
        val anotherConditions = another.conditions;

        val result = new HashSet<Condition>();
        result.addAll(Arrays.asList(conditions));
        result.addAll(Arrays.asList(anotherConditions));

        return from0(result.toArray(new Condition[0]));
    }

    private static void testRecursive(
            ExecutionContext ctx,
            Callback<Result> callback,
            Condition[] conditions,
            int index
    ) {
        if (conditions.length == index) {
            callback.complete(Results.ok());
            return;
        }

        conditions[index].testAsync(ctx, Callback.of((result, cause) -> {
            if (cause != null) {
                callback.completeExceptionally(cause);
            } else if (result != null && !result.isSuccess()) {
                callback.complete(result);
            } else {
                testRecursive(ctx, callback, conditions, index + 1);
            }
        }));
    }

    public int size() {
        return conditions.length;
    }

    public Condition get(int index) {
        return conditions[index];
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void test(ExecutionContext ctx, Callback<Result> callback) {
        testRecursive(ctx, callback, conditions, 0);
    }

    public void testVisibility(ExecutionContext ctx, Callback<Result> callback) {
        testRecursive(ctx, callback, visibilityConditions, 0);
    }

}
