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

package w.commander.decorator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.executor.MethodExecutor;
import w.commander.spec.HandlerSpec;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Decorators {

    private static final Decorators EMPTY = new Decorators();

    Decorator[] decorators;

    private Decorators() {
        decorators = new Decorator[0];
    }

    public static @NotNull Decorators empty() {
        return EMPTY;
    }

    public static @NotNull Decorators from(@NotNull Decorator @NotNull ... decorators) {
        if (decorators.length == 0) {
            // memory optimization :0
            return EMPTY;
        }

        return from0(Arrays.copyOf(decorators, decorators.length));
    }

    public @NotNull Decorators merge(@NotNull Decorators another) {
        if (isEmpty() && another.isEmpty()) return EMPTY;

        val decorators = this.decorators;
        val anotherDecorators = another.decorators;

        val result = new HashSet<Decorator>();
        result.addAll(Arrays.asList(decorators));
        result.addAll(Arrays.asList(anotherDecorators));

        return from0(result.toArray(new Decorator[0]));
    }

    private static Decorators from0(Decorator... decorators) {
        Arrays.sort(decorators, Comparator.naturalOrder());
        return new Decorators(decorators);
    }

    public MethodExecutor wrap(HandlerSpec handler, MethodExecutor executor) {
        for (val decorator : decorators) {
            executor = decorator.wrap(handler, executor);
        }

        return executor;
    }

    public int size() {
        return decorators.length;
    }

    public Decorator get(int index) {
        return decorators[index];
    }

    public boolean isEmpty() {
        return size() == 0;
    }

}
