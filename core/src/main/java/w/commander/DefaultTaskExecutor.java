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

package w.commander;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author whilein
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultTaskExecutor implements TaskExecutor {

    ExecutorService main;
    ThreadGroup mainGroup;
    ExecutorService async;
    ThreadGroup asyncGroup;

    public DefaultTaskExecutor(boolean daemon) {
        mainGroup = new ThreadGroup("WCommander Main");
        main = Executors.newSingleThreadExecutor(r -> {
            val thread = new Thread(mainGroup, r, "WCommander Main");
            thread.setDaemon(daemon);

            return thread;
        });

        asyncGroup = new ThreadGroup("WCommander Async");
        async = Executors.newCachedThreadPool(new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                val thread = new Thread(asyncGroup, r, "WCommander Async #" + counter.getAndIncrement());
                thread.setDaemon(daemon);

                return thread;
            }
        });
    }

    @Override
    public void run(Runnable runnable) {
        main.execute(runnable);
    }

    @Override
    public void runAsync(Runnable runnable) {
        async.execute(runnable);
    }

    @Override
    public <T> CompletableFuture<T> run(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, main);
    }

    @Override
    public <T> CompletableFuture<T> runAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, async);
    }

    @Override
    public void shutdown() {
        main.shutdown();
        async.shutdown();
    }

    @Override
    public boolean isMainContext() {
        return Thread.currentThread().getThreadGroup() == mainGroup;
    }

    @Override
    public boolean isAsyncContext() {
        return Thread.currentThread().getThreadGroup() == asyncGroup;
    }
}
