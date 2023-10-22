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

package w.commander.platform.velocity;

import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.RawArguments;
import w.commander.execution.ExecutionContext;
import w.commander.execution.ExecutionContextFactory;

/**
 * @author whilein
 */
public class VelocityExecutionContextFactory implements ExecutionContextFactory {

    @Override
    public @NotNull ExecutionContext create(
            @NotNull CommandActor actor,
            @NotNull RawArguments arguments
    ) {
        return new VelocityExecutionContext(
                (VelocityCommandActor) actor,
                arguments
        );
    }
}
