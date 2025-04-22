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

package w.commander.parameter.argument.validator.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.parameter.argument.validator.ArgumentValidator;
import w.commander.parameter.argument.validator.ArgumentValidatorFactory;

import java.util.regex.Pattern;

/**
 * @author _Novit_ (novitpw), whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RegexArgumentValidatorFactory implements ArgumentValidatorFactory<Regex> {

    CommanderConfig config;

    @Override
    public boolean isSupported(@NotNull Class<?> type) {
        return type == String.class;
    }

    @Override
    public @NotNull Class<Regex> getAnnotation() {
        return Regex.class;
    }

    @Override
    public @NotNull ArgumentValidator create(@NotNull Regex regex) {
        return new RegexArgumentValidator(Pattern.compile(regex.value()), config);
    }

}
