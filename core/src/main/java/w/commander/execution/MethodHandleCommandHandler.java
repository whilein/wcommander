package w.commander.execution;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.internal.InvocationParametersProcessor;
import w.commander.manual.usage.Usage;
import w.commander.parameter.HandlerParameters;
import w.commander.result.Result;
import w.commander.result.Results;

import java.lang.invoke.MethodHandle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MethodHandleCommandHandler extends AbstractCommandHandler {

    MethodHandle method;
    ExecutionThrowableInterceptor executionThrowableInterceptor;

    private MethodHandleCommandHandler(
            String path,
            Usage usage,
            MethodHandle method,
            HandlerParameters parameters,
            ExecutionThrowableInterceptor executionThrowableInterceptor
    ) {
        super(path, parameters, usage);

        this.method = method;
        this.executionThrowableInterceptor = executionThrowableInterceptor;
    }

    public static @NotNull CommandHandler create(
            @NotNull String path,
            @NotNull Usage usage,
            @NotNull MethodHandle mh,
            @NotNull HandlerParameters parameters,
            @NotNull ExecutionThrowableInterceptor executionThrowableInterceptor
    ) {
        return new MethodHandleCommandHandler(
                path,
                usage,
                mh,
                parameters,
                executionThrowableInterceptor
        );
    }

    private void processReturnedValue(
            Object returnedValue,
            Consumer<Result> callback,
            Consumer<Throwable> failureCallback
    ) {
        if (returnedValue instanceof Result) {
            callback.accept((Result) returnedValue);
        } else if (returnedValue instanceof CompletableFuture<?>) {
            val future = (CompletableFuture<?>) returnedValue;

            future.whenComplete((v, t) -> {
                if (t != null) {
                    failureCallback.accept(t);
                    return;
                }

                processReturnedValue(v, callback, failureCallback);
            });
        } else if (returnedValue instanceof Supplier<?>) {
            processReturnedValue(((Supplier<?>) returnedValue).get(), callback, failureCallback);
        } else {
            callback.accept(Results.ok());
        }
    }

    private Result processThrowable(Throwable t) {
        if (t instanceof Result) {
            return (Result) t;
        }

        return executionThrowableInterceptor.intercept(t);
    }

    private Consumer<Throwable> throwableToResultMapper(Consumer<Result> callback) {
        return t -> callback.accept(processThrowable(t));
    }

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Consumer<@NotNull Result> callback
    ) {
        val failureCallback = throwableToResultMapper(callback);

        val commandParameterExtractor = new InvocationParametersProcessor(context, this);

        commandParameterExtractor.setResultCallback(callback);
        commandParameterExtractor.setFailureCallback(failureCallback);

        commandParameterExtractor.setParameterCallback(parameters -> {
            Object result;

            try {
                result = method.invokeWithArguments(parameters);
            } catch (Throwable e) {
                failureCallback.accept(e);
                return;
            }

            processReturnedValue(result, callback, failureCallback);
        });

        commandParameterExtractor.process();
    }
}
