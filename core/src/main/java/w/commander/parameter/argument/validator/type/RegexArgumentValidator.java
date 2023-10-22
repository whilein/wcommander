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
import org.jetbrains.annotations.Nullable;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.validator.ArgumentValidator;
import w.commander.result.Result;
import w.commander.result.Results;

import java.util.regex.Pattern;

/**
 * @author _Novit_ (novitpw), whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RegexArgumentValidator implements ArgumentValidator {

    Pattern pattern;
    ErrorResultFactory errorResultFactory;

    @Override
    public @NotNull Result validate(@Nullable Object value, @NotNull ExecutionContext ctx, @NotNull Argument argument) {
        return value != null && !pattern.matcher(value.toString()).matches()
                ? errorResultFactory.onFailRegexValidation(argument, pattern)
                : Results.ok();
    }
}
