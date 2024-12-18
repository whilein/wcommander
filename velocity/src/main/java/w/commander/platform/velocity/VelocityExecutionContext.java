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
import w.commander.RawArguments;
import w.commander.execution.SimpleExecutionContext;
import w.commander.platform.adventure.AdventureExecutionContext;

/**
 * @author whilein
 */
public class VelocityExecutionContext
        extends SimpleExecutionContext
        implements AdventureExecutionContext {
    public VelocityExecutionContext(
            @NotNull VelocityCommandActor actor,
            @NotNull RawArguments rawArguments
    ) {
        super(actor, rawArguments);
    }

    @Override
    public @NotNull VelocityCommandActor getActor() {
        return (VelocityCommandActor) super.getActor();
    }
    
}
