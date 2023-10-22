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

package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.parser.ArgumentParser;

import java.util.function.Function;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberArgumentParser implements ArgumentParser {

    ErrorResultFactory errorResultFactory;
    Function<? super String, ? extends Number> fn;

    public static @NotNull ArgumentParser create(
            @NotNull ErrorResultFactory errorResultFactory,
            @NotNull Function<? super @NotNull String, ? extends @NotNull Number> fn
    ) {
        return new NumberArgumentParser(errorResultFactory, fn);
    }

    @Override
    public @NotNull Object parse(
            @NotNull String value,
            @NotNull Argument argument,
            @NotNull ExecutionContext context
    ) {
        try {
            return fn.apply(value);
        } catch (NumberFormatException e) {
            return errorResultFactory.onInvalidNumber(argument, value);
        }
    }
}
