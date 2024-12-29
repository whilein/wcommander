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

package w.commander.decorator.type;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.CommanderConfig;
import w.commander.cooldown.CooldownManager;
import w.commander.cooldown.CooldownResult;
import w.commander.decorator.Decorator;
import w.commander.execution.ExecutionContext;
import w.commander.executor.MethodExecutor;
import w.commander.result.Result;
import w.commander.spec.HandlerSpec;
import w.commander.util.Callback;

/**
 * @author whilein
 */
@EqualsAndHashCode(of = {"customId", "cooldownManager"})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CooldownDecorator implements Decorator {

    String customId;
    CooldownManager cooldownManager;
    CommanderConfig config;

    @Override
    public @NotNull MethodExecutor wrap(@NotNull HandlerSpec handler, @NotNull MethodExecutor delegate) {
        String id = customId;
        if (id == null) {
            id = handler.getPath().toString();
        }
        return new CooldownMethodExecutor(id, delegate);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private final class CooldownMethodExecutor implements MethodExecutor {

        String id;
        MethodExecutor delegate;

        @Override
        public void execute(ExecutionContext context, Callback<Result> callback, Object[] args) {
            cooldownManager.getCooldownAsync(context.getActor(), id, config.getAsyncExecutor())
                    .whenComplete((r, e) -> {
                        if (e != null) {
                            callback.completeExceptionally(e);
                            return;
                        }

                        if (r != null && !r.isNegative()) {
                            callback.complete(config.getErrorResultFactory().onCooldown(context, r));
                            return;
                        }

                        val newCallback = new CooldownCallback(
                                id,
                                context.getActor(),
                                callback
                        );

                        delegate.execute(context, newCallback, args);
                    });
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private final class CooldownCallback implements Callback<Result> {
        String id;
        CommandActor actor;
        Callback<Result> delegate;

        @Override
        public void complete(@NotNull Result value) {
            if (value instanceof CooldownResult) {
                val cr = ((CooldownResult) value);
                val duration = cr.getCooldown();
                val result = cr.getDelegate();

                cooldownManager.setCooldownAsync(
                        actor,
                        id,
                        duration,
                        config.getAsyncExecutor()
                ).whenComplete((r, e) -> {
                    if (e != null) {
                        delegate.completeExceptionally(e);
                    } else {
                        delegate.complete(result);
                    }
                });

                return;
            }

            delegate.complete(value);
        }

        @Override
        public void completeExceptionally(@NotNull Throwable cause) {
            delegate.completeExceptionally(cause);
        }
    }
}
