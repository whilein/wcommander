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

package w.commander.platform.adventure;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;

/**
 * @author _Novit_ (novitpw)
 */
public interface AdventureExecutionContext extends ExecutionContext {

    @NotNull AdventureCommandActor getActor();

    default void dispatch(@NotNull Component message) {
        getActor().sendMessage(message);
    }

}