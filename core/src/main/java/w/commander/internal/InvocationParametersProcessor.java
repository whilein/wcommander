package w.commander.internal;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import w.commander.execution.CommandHandler;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.cursor.SimpleArgumentCursor;
import w.commander.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class InvocationParametersProcessor {

    ExecutionContext context;

    CommandHandler commandHandler;

    @Setter
    @NonFinal
    Consumer<Object[]> parameterCallback;

    @Setter
    @NonFinal
    Consumer<Result> resultCallback;

    @Setter
    @NonFinal
    Consumer<Throwable> failureCallback;

    private static <T> void done(T value, Consumer<T> callback) {
        if (callback != null) {
            callback.accept(value);
        }
    }

    private void done(Result result) {
        done(result, resultCallback);
    }

    private void done(Object[] parameters) {
        done(parameters, parameterCallback);
    }

    private void done(Throwable failure) {
        done(failure, failureCallback);
    }

    private void extractLazy(
            int index,
            Object value,
            Queue<CompletableFuture<?>> futures,
            AtomicReference<Result> result,
            AtomicReference<Throwable> failure,
            Object[] parameters
    ) {
        // already completed with error
        if (result.get() != null || failure.get() != null) {
            return;
        }

        if (value instanceof Supplier<?>) {
            Object newValue;

            try {
                newValue = ((Supplier<?>) value).get();
            } catch (Throwable t) {
                failure.set(t);
                return;
            }

            extractLazy(index, newValue, futures, result, failure, parameters);
            return;
        }

        if (value instanceof CompletableFuture<?>) {
            val future = (CompletableFuture<?>) value;

            futures.add(future.whenComplete((v, t) -> {
                if (t == null) {
                    extractLazy(index, v, futures, result, failure, parameters);
                } else {
                    failure.set(t);
                }
            }));

            return;
        }

        if (value instanceof Result) {
            result.set((Result) value);
            return;
        }

        parameters[index] = value;

        // todo validation
    }

    private static void awaitFutureCompletion(
            Queue<CompletableFuture<?>> futures,
            Runnable completionCallback
    ) {
        while (!futures.isEmpty()) {
            val future = futures.poll();

            if (!future.isDone()) {
                future.whenComplete((v, e) -> awaitFutureCompletion(futures, completionCallback));
                return;
            }
        }

        completionCallback.run();
    }

    public void process() {
        val parameters = commandHandler.getParameters();

        val values = new Object[parameters.size()];

        val rawArguments = context.getRawArguments();

        val argumentCount = rawArguments.size();
        val requiredArgumentCount = parameters.getRequiredArgumentCount();

        val cursor = SimpleArgumentCursor.create(
                argumentCount,
                requiredArgumentCount
        );

        List<Lazy> lazyParameters = null;

        for (int i = 0, j = values.length; i < j; i++) {
            val parameter = parameters.get(i);

            val value = parameter.extract(context, cursor);

            if (value instanceof Supplier<?> || value instanceof CompletableFuture<?>) {
                if (lazyParameters == null) {
                    lazyParameters = new ArrayList<>(2);
                }

                lazyParameters.add(new Lazy(i, value));
            } else if (value instanceof Result) {
                done((Result) value);
                return;
            } else {
                values[i] = value;

                // todo validation
            }
        }

        if (lazyParameters != null) {
            val futures = new ConcurrentLinkedQueue<CompletableFuture<?>>();
            val resultRef = new AtomicReference<Result>();
            val failureRef = new AtomicReference<Throwable>();

            for (val lazyParameter : lazyParameters) {
                extractLazy(
                        lazyParameter.index,
                        lazyParameter.value,
                        futures,
                        resultRef,
                        failureRef,
                        values
                );
            }

            awaitFutureCompletion(futures, () -> {
                val failure = failureRef.get();

                if (failure != null) {
                    done(failure);
                    return;
                }

                val result = resultRef.get();

                if (result != null) {
                    done(result);
                    return;
                }

                done(values);
            });

            return;
        }

        done(values);
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static final class Lazy {
        int index;
        Object value;
    }

}
