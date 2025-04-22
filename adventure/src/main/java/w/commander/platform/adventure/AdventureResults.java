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

package w.commander.platform.adventure;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * @author _Novit_ (novitpw)
 */
@UtilityClass
public class AdventureResults {

    public @NotNull AdventureSuccessResult ok(@NotNull Component message) {
        return new ComponentResultSuccess(message);
    }

    public @NotNull AdventureFailedResult error(@NotNull Component message) {
        return new ComponentResultFailed(message);
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static abstract class ComponentResult implements AdventureResult {

        Component message;

        @Override
        public void dispatch(@NotNull AdventureExecutionContext context) {
            context.sendMessage(message);
        }

    }

    private static class ComponentResultSuccess extends ComponentResult implements AdventureSuccessResult {
        ComponentResultSuccess(Component message) {
            super(message);
        }
    }

    private static class ComponentResultFailed extends ComponentResult implements AdventureFailedResult {
        ComponentResultFailed(Component message) {
            super(message);
        }
    }

}
