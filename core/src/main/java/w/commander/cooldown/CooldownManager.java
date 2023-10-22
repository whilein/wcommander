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

package w.commander.cooldown;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.CommandActor;

import javax.annotation.concurrent.ThreadSafe;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * @author whilein
 */
@ThreadSafe
public interface CooldownManager {

    @NotNull
    String getId();

    default Executor getExecutor() {
        return ForkJoinPool.commonPool();
    }

    boolean hasCooldown(@NotNull CommandActor actor, @NotNull String action);

    @Nullable
    Duration getCooldown(@NotNull CommandActor actor, @NotNull String action);

    void setCooldown(@NotNull CommandActor actor, @NotNull String action, @NotNull Duration duration);

    default @NotNull CompletableFuture<@NotNull Boolean> hasCooldownAsync(
            @NotNull CommandActor actor,
            @NotNull String action
    ) {
        return hasCooldownAsync(actor, action, getExecutor());
    }

    default @NotNull CompletableFuture<@Nullable Duration> getCooldownAsync(
            @NotNull CommandActor actor,
            @NotNull String action
    ) {
        return getCooldownAsync(actor, action, getExecutor());
    }

    default @NotNull CompletableFuture<Void> setCooldownAsync(
            @NotNull CommandActor actor,
            @NotNull String action,
            @NotNull Duration duration
    ) {
        return setCooldownAsync(actor, action, duration, getExecutor());
    }

    default @NotNull CompletableFuture<@NotNull Boolean> hasCooldownAsync(
            @NotNull CommandActor actor,
            @NotNull String action,
            @NotNull Executor executor
    ) {
        return CompletableFuture.supplyAsync(() -> hasCooldown(actor, action), executor);
    }

    default @NotNull CompletableFuture<@Nullable Duration> getCooldownAsync(
            @NotNull CommandActor actor,
            @NotNull String action,
            @NotNull Executor executor
    ) {
        return CompletableFuture.supplyAsync(() -> getCooldown(actor, action), executor);
    }

    default @NotNull CompletableFuture<Void> setCooldownAsync(
            @NotNull CommandActor actor,
            @NotNull String action,
            @NotNull Duration duration,
            @NotNull Executor executor
    ) {
        return CompletableFuture.runAsync(() -> setCooldown(actor, action, duration), executor);
    }

}
