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

package w.commander;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.lang.invoke.MethodHandles;

/**
 * @author whilein
 */
@Getter
@Immutable
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class CommandInfo {

    @NotNull Object instance;

    @NotNull MethodHandles.Lookup lookup;

    public CommandInfo(Object instance) {
        this(instance, MethodHandles.lookup());
    }

    public @NotNull Class<?> getInstanceType() {
        return getInstance().getClass();
    }

    public @NotNull CommandInfo withInstance(@NotNull Object instance) {
        return new CommandInfo(instance, lookup);
    }

    @Override
    public String toString() {
        return instance.toString();
    }
}
