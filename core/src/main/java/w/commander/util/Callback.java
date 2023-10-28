package w.commander.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author whilein
 */
public interface Callback<T> {

    void complete(@NotNull T value);

    void completeExceptionally(@NotNull Throwable cause);

    default @NotNull Consumer<Throwable> asExceptionallyConsumer() {
        return this::completeExceptionally;
    }

    default @NotNull Consumer<T> asConsumer() {
        return this::complete;
    }

    static <T> @NotNull Callback<@NotNull T> of(
            @NotNull BiConsumer<@Nullable T, @Nullable Throwable> callback
    ) {
        return new OfBiConsumer<>(callback);
    }

    static <T> @NotNull Callback<@NotNull T> of(
            @NotNull Consumer<@NotNull T> success,
            @NotNull Consumer<@NotNull Throwable> failure
    ) {
        return new OfConsumer<>(success, failure);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class OfConsumer<T> implements Callback<T> {
        Consumer<T> successCallback;
        Consumer<Throwable> failureCallback;

        AtomicBoolean complete = new AtomicBoolean();

        @Override
        public void complete(@NotNull T value) {
            if (complete.compareAndSet(false, true)) {
                successCallback.accept(value);
                return;
            }

            throw new IllegalStateException("Already completed");
        }

        @Override
        public void completeExceptionally(@NotNull Throwable cause) {
            if (complete.compareAndSet(false, true)) {
                failureCallback.accept(null);
                return;
            }

            throw new IllegalStateException("Already completed");
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class OfBiConsumer<T> implements Callback<T> {
        BiConsumer<T, Throwable> callback;

        AtomicBoolean complete = new AtomicBoolean();

        @Override
        public void complete(@NotNull T value) {
            if (complete.compareAndSet(false, true)) {
                callback.accept(value, null);
                return;
            }

            throw new IllegalStateException("Already completed");
        }

        @Override
        public void completeExceptionally(@NotNull Throwable cause) {
            if (complete.compareAndSet(false, true)) {
                callback.accept(null, cause);
                return;
            }

            throw new IllegalStateException("Already completed");
        }

    }

}
