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
import w.commander.parameter.argument.validator.NumberArgumentValidator;
import w.commander.parameter.argument.validator.NumberArgumentValidatorFactory;

/**
 * @author _Novit_ (novitpw), whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LowerThanArgumentValidatorFactory implements NumberArgumentValidatorFactory<LowerThan> {

    CommanderConfig config;

    @Override
    public @NotNull Class<LowerThan> getAnnotation() {
        return LowerThan.class;
    }

    @Override
    public @NotNull NumberArgumentValidator create(@NotNull LowerThan lowerThan) {
        return new LowerThanArgumentValidator(lowerThan.value(), config);
    }
}