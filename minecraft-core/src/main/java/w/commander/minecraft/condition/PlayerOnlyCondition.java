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

package w.commander.minecraft.condition;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.condition.Condition;
import w.commander.execution.ExecutionContext;
import w.commander.minecraft.MinecraftCommandActor;
import w.commander.minecraft.MinecraftCommanderConfig;
import w.commander.minecraft.annotation.PlayerOnly;
import w.commander.result.Result;
import w.commander.result.Results;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class PlayerOnlyCondition implements Condition {

    MinecraftCommanderConfig config;

    @Override
    public @NotNull Result test(@NotNull ExecutionContext ctx) {
        return !((MinecraftCommandActor) ctx.getActor()).isPlayer()
                ? config.getMinecraftErrorResultFactory().onFailPlayerOnlyCondition(ctx)
                : Results.ok();
    }

    @Override
    public int hashCode() {
        return PlayerOnlyCondition.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == PlayerOnlyCondition.class;
    }
}
