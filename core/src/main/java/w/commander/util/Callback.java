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

package w.commander.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author whilein
 */
public interface Callback<T> {

    static <T> @NotNull Callback<@NotNull T> of(
            @NotNull Consumer<@NotNull T> success,
            @NotNull Consumer<@NotNull Throwable> failure
    ) {
        return new OfConsumer<>(success, failure);
    }

    static <T> @NotNull Callback<@NotNull T> of(
            @NotNull BiConsumer<@Nullable T, @Nullable Throwable> callback
    ) {
        return new OfBiConsumer<>(callback);
    }

    static <T> @NotNull Callback<@NotNull T> mappingException(
            @NotNull Consumer<T> callback,
            @NotNull Function<Throwable, T> causeMapper
    ) {
        return Callback.of((result, cause) -> {
            if (cause != null) {
                callback.accept(causeMapper.apply(cause));
            } else if (result != null) {
                callback.accept(result);
            }
        });
    }

    static <T> @NotNull Callback<T> ofFuture(@NotNull CompletableFuture<T> future) {
        return of((result, cause) -> {
            if (cause != null) {
                future.completeExceptionally(cause);
            } else {
                future.complete(result);
            }
        });
    }

    void complete(@NotNull T value);

    void completeExceptionally(@NotNull Throwable cause);

    default @NotNull Consumer<T> asConsumer() {
        return this::complete;
    }

    default @NotNull Consumer<Throwable> asExceptionallyConsumer() {
        return this::completeExceptionally;
    }

    default @NotNull BiConsumer<T, Throwable> asBiConsumer() {
        return (r, e) -> {
            if (e != null) {
                completeExceptionally(e);
            } else {
                complete(r);
            }
        };
    }

    default <U> @NotNull Callback<@NotNull U> map(
            @NotNull Function<U, T> mapper
    ) {
        return Callback.of((result, cause) -> {
            if (cause != null) {
                completeExceptionally(cause);
            } else if (result != null) {
                complete(mapper.apply(result));
            }
        });
    }

    default @NotNull Callback<@NotNull T> mapException(
            @NotNull Function<Throwable, T> causeMapper
    ) {
        return Callback.of((result, cause) -> {
            if (cause != null) {
                complete(causeMapper.apply(cause));
            } else if (result != null) {
                complete(result);
            }
        });
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class OfConsumer<T> implements Callback<T> {
        Consumer<T> successCallback;
        Consumer<Throwable> failureCallback;

        @Override
        public void complete(@NotNull T value) {
            successCallback.accept(value);
        }

        @Override
        public void completeExceptionally(@NotNull Throwable cause) {
            failureCallback.accept(cause);
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class OfBiConsumer<T> implements Callback<T> {
        BiConsumer<T, Throwable> callback;

        @Override
        public void complete(@NotNull T value) {
            callback.accept(value, null);
        }

        @Override
        public void completeExceptionally(@NotNull Throwable cause) {
            callback.accept(null, cause);
        }

    }

}
