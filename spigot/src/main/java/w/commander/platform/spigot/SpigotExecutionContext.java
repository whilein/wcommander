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

package w.commander.platform.spigot;

import org.jetbrains.annotations.NotNull;
import w.commander.RawArguments;
import w.commander.execution.AbstractExecutionContext;

/**
 * @author whilein
 */
public class SpigotExecutionContext<T extends SpigotCommandActor>
        extends AbstractExecutionContext<T> {
    public SpigotExecutionContext(
            @NotNull T actor,
            @NotNull RawArguments rawArguments
    ) {
        super(actor, rawArguments);
    }
}
