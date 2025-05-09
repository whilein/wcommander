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

package w.commander.decorator.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.annotation.Async;
import w.commander.decorator.Decorator;
import w.commander.execution.ExecutionContext;
import w.commander.executor.MethodExecutor;
import w.commander.result.Result;
import w.commander.spec.HandlerSpec;
import w.commander.util.Callback;

import java.util.concurrent.CompletionException;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AsyncDecorator implements Decorator {
    CommanderConfig config;

    @Override
    public int getOrder() {
        return Decorator.LAST;
    }

    @Override
    public @NotNull MethodExecutor wrap(@NotNull HandlerSpec handler, @NotNull MethodExecutor delegate) {
        return new AsyncMethodExecutor(delegate, config);
    }

    @Override
    public int hashCode() {
        return Async.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == AsyncDecorator.class;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static final class AsyncMethodExecutor implements MethodExecutor {
        MethodExecutor delegate;
        CommanderConfig config;

        @Override
        public void execute(ExecutionContext context, Callback<Result> callback, Object[] args) {
            config.getAsyncExecutor().execute(() -> {
                try {
                    delegate.execute(context, callback, args);
                } catch (Throwable e) {
                    throw new CompletionException(e);
                }
            });
        }
    }
}
