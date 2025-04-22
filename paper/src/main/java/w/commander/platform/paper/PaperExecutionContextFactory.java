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

package w.commander.platform.paper;

import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.RawArguments;
import w.commander.attribute.AttributeStore;
import w.commander.execution.ExecutionContext;
import w.commander.platform.spigot.SpigotExecutionContextFactory;

/**
 * @author whilein
 */
public class PaperExecutionContextFactory extends SpigotExecutionContextFactory {

    @Override
    public @NotNull ExecutionContext create(
            @NotNull CommandActor actor,
            @NotNull RawArguments arguments,
            @NotNull AttributeStore attributeStore) {
        return new PaperExecutionContext(
                (PaperCommandActor) actor,
                arguments,
                attributeStore
        );
    }

}
