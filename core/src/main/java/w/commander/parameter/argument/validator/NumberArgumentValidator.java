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

package w.commander.parameter.argument.validator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.result.Result;
import w.commander.result.Results;

/**
 * @author whilein
 */
public abstract class NumberArgumentValidator implements ArgumentValidator {
    @Override
    public final @NotNull Result validate(
            @Nullable Object value,
            @NotNull ExecutionContext ctx,
            @NotNull Argument argument
    ) {
        return value instanceof Number
                ? validate((Number) value, ctx, argument)
                : Results.ok();

    }

    public abstract @NotNull Result validate(
            @NotNull Number number,
            @NotNull ExecutionContext ctx,
            @NotNull Argument argument
    );

}
