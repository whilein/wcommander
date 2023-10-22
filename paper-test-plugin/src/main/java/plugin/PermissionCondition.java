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

package plugin;

import org.jetbrains.annotations.NotNull;
import w.commander.condition.Condition;
import w.commander.condition.ConditionFactory;
import w.commander.execution.ExecutionContext;
import w.commander.platform.paper.PaperCommandActor;
import w.commander.result.Result;
import w.commander.result.Results;

/**
 * @author whilein
 */
public class PermissionCondition implements ConditionFactory<Permission> {
    @Override
    public @NotNull Class<? extends Permission> getAnnotation() {
        return Permission.class;
    }

    @Override
    public @NotNull Condition create(@NotNull Permission annotation) {
        return new Condition() {
            @Override
            public @NotNull Result test(@NotNull ExecutionContext ctx) {
                return ((PaperCommandActor) ctx.getActor()).getSender()
                        .hasPermission(annotation.value())
                        ? Results.ok()
                        : Results.error("no permission");
            }

            @Override
            public boolean shouldCheckForVisibility() {
                return true;
            }
        };
    }
}
