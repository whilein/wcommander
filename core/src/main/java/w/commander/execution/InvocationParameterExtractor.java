package w.commander.execution;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.parameter.HandlerParameter;
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
final class InvocationParameterExtractor {

    ExecutionContext context;
    CommandHandler commandHandler;
    HandlerParameter[] handlerParameters;

    private void extractLazy(
            int index,
            Object value,
            Queue<CompletableFuture<?>> futures,
            AtomicReference<Result> result,
            Object[] parameters
    ) {
        // already completed with error
        if (result.get() != null) {
            return;
        }

        if (value instanceof Supplier<?>) {
            extractLazy(index, ((Supplier<?>) value).get(), futures, result, parameters);
            return;
        }

        if (value instanceof CompletableFuture<?>) {
            val future = (CompletableFuture<?>) value;

            futures.add(future.whenComplete((v, e) -> {
                if (e == null) {
                    extractLazy(index, value, futures, result, parameters);
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

    private void awaitFutureCompletion(
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

    public void extract(Consumer<Object[]> parameterCallback, Consumer<Result> resultCallback) {
        val commandParameters = this.handlerParameters;
        val parameters = new Object[commandParameters.length];

        val rawArguments = context.getRawArguments();

        val argumentCount = rawArguments.size();
        val requiredArgumentCount = commandHandler.getRequiredArgumentCount();

        val cursor = SimpleArgumentCursor.create(
                argumentCount,
                requiredArgumentCount
        );

        List<Lazy> lazyParameters = null;

        for (int i = 0, j = parameters.length; i < j; i++) {
            val parameter = commandParameters[i];

            val value = parameter.extract(context, cursor);

            if (value instanceof Supplier<?> || value instanceof CompletableFuture<?>) {
                if (lazyParameters == null) {
                    lazyParameters = new ArrayList<>(2);
                }

                lazyParameters.add(new Lazy(i, value));
            } else if (value instanceof Result) {
                resultCallback.accept((Result) value);
                return;
            } else {
                parameters[i] = value;

                // todo validation
            }
        }

        if (lazyParameters != null) {
            val futures = new ConcurrentLinkedQueue<CompletableFuture<?>>();
            val resultRef = new AtomicReference<Result>();

            for (val lazyParameter : lazyParameters) {
                extractLazy(
                        lazyParameter.index,
                        lazyParameter.value,
                        futures,
                        resultRef,
                        parameters
                );
            }

            awaitFutureCompletion(futures, () -> {
                val result = resultRef.get();

                if (result != null) {
                    resultCallback.accept(result);
                    return;
                }

                parameterCallback.accept(parameters);
            });

            return;
        }

        parameterCallback.accept(parameters);
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static final class Lazy {
        int index;
        Object value;
    }

}
