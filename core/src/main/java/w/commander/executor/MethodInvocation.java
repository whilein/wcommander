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

package w.commander.executor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.HandlerParameters;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.cursor.SimpleArgumentCursor;
import w.commander.result.Result;
import w.commander.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class MethodInvocation {

    HandlerParameters handlerParameters;

    MethodExecutor methodExecutor;

    ExecutionContext context;

    Callback<Result> callback;

    @NonFinal
    volatile Result result;

    @NonFinal
    volatile Throwable failure;

    @NonFinal
    Object[] parameters;

    private synchronized void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    private synchronized void setParameter(int index, Object parameter) {
        this.parameters[index] = parameter;
    }

    private void invokeFast(Object[] parameters) {
        methodExecutor.execute(context, callback, parameters);
    }

    private synchronized void invoke() {
        invokeFast(parameters);
    }

    private static Result validate(ExecutionContext context, Object value, HandlerParameter parameter) {
        if (parameter instanceof Argument) {
            val argument = (Argument) parameter;
            for (val validator : argument.getValidators()) {
                val result = validator.validate(value, context, argument);
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }

        return null;
    }

    private void extractLazy(
            int index,
            Object value,
            HandlerParameter parameter,
            Queue<CompletableFuture<?>> futures
    ) {
        // already completed with error
        if (result != null || failure != null) {
            return;
        }

        if (value instanceof Supplier<?>) {
            Object newValue;

            try {
                newValue = ((Supplier<?>) value).get();
            } catch (Throwable t) {
                failure = t;
                return;
            }

            extractLazy(index, newValue, parameter, futures);
            return;
        }

        if (value instanceof CompletableFuture<?>) {
            val future = (CompletableFuture<?>) value;

            futures.add(future.whenComplete((v, t) -> {
                if (t == null) {
                    extractLazy(index, v, parameter, futures);
                } else {
                    failure = t;
                }
            }));

            return;
        }

        if (value instanceof Result) {
            result = (Result) value;
            return;
        }

        val validateResult = validate(context, value, parameter);
        if (validateResult != null) {
            result = validateResult;
            return;
        }

        setParameter(index, value);
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

    public void process() {
        val handlerParameters = this.handlerParameters;
        val parameters = handlerParameters.getParameters();

        val parameterCount = parameters.size();

        val values = new Object[parameterCount];

        val rawArguments = context.getRawArguments();

        val argumentCount = rawArguments.size();
        val requiredArgumentCount = handlerParameters.getRequiredArgumentCount();

        val cursor = SimpleArgumentCursor.create(
                argumentCount,
                requiredArgumentCount
        );

        List<LazyParameter> lazyParameters = null;

        for (int i = 0; i < parameterCount; i++) {
            val parameter = parameters.get(i);

            Object value;
            try {
                value = parameter.extract(context, cursor);
            } catch (Throwable t) {
                callback.completeExceptionally(t);
                return;
            }

            if (value instanceof Supplier<?> || value instanceof CompletableFuture<?>) {
                if (lazyParameters == null) {
                    lazyParameters = new ArrayList<>(2);
                }

                lazyParameters.add(new LazyParameter(i, value, parameter));
            } else if (value instanceof Result) {
                callback.complete((Result) value);
                return;
            } else {
                val validateResult = validate(context, value, parameter);
                if (validateResult != null) {
                    callback.complete(validateResult);
                    return;
                }

                values[i] = value;
            }
        }

        if (lazyParameters != null) {
            setParameters(values);

            val futures = new ConcurrentLinkedQueue<CompletableFuture<?>>();

            for (val lazyParameter : lazyParameters) {
                extractLazy(
                        lazyParameter.index,
                        lazyParameter.value,
                        lazyParameter.parameter,
                        futures
                );
            }

            awaitFutureCompletion(futures, () -> {
                val failure = this.failure;
                if (failure != null) {
                    callback.completeExceptionally(failure);
                    return;
                }

                val result = this.result;
                if (result != null) {
                    callback.complete(result);
                    return;
                }

                invoke();
            });

            return;
        }

        invokeFast(values);
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static final class LazyParameter {
        int index;
        Object value;
        HandlerParameter parameter;
    }

}
