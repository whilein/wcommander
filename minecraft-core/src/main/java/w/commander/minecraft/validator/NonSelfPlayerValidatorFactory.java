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

package w.commander.minecraft.validator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.minecraft.MinecraftErrorResultFactory;
import w.commander.minecraft.annotation.NonSelfPlayer;
import w.commander.parameter.argument.validator.ArgumentValidator;
import w.commander.parameter.argument.validator.ArgumentValidatorFactory;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NonSelfPlayerValidatorFactory implements ArgumentValidatorFactory<NonSelfPlayer> {

    Class<?> identityType;
    MinecraftErrorResultFactory minecraftErrorResultFactory;

    @Override
    public boolean isSupported(@NotNull Class<?> type) {
        return identityType.isAssignableFrom(type);
    }

    @Override
    public @NotNull Class<? extends NonSelfPlayer> getAnnotation() {
        return NonSelfPlayer.class;
    }

    @Override
    public @NotNull ArgumentValidator create(@NotNull NonSelfPlayer annotation) {
        return new NonSelfPlayerValidator(annotation.value(), minecraftErrorResultFactory);
    }

}
