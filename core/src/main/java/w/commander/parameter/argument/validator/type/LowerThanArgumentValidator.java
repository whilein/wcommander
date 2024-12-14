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

package w.commander.parameter.argument.validator.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.validator.NumberArgumentValidator;
import w.commander.result.Result;
import w.commander.result.Results;

/**
 * @author _Novit_ (novitpw), whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LowerThanArgumentValidator extends NumberArgumentValidator {

    double value;
    CommanderConfig config;

    @Override
    public @NotNull Result validate(@NotNull Number value, @NotNull ExecutionContext ctx, @NotNull Argument argument) {
        return value.doubleValue() >= this.value
                ? config.getErrorResultFactory().onFailLowerThanValidation(argument, this.value)
                : Results.ok();
    }
}
