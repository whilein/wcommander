package w.commander.execution;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
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

    private MethodHandleCommandHandler(
            String path,
            Usage usage,
            MethodHandle method,
            HandlerParameters parameters
    ) {
        super(path, parameters, usage);

        this.method = method;
    }

    public static CommandHandler create(
            @NotNull String path,
            @NotNull Usage usage,
            @NotNull MethodHandle mh,
            @NotNull HandlerParameters parameters
    ) {
        return new MethodHandleCommandHandler(
                path,
                usage,
                mh,
                parameters
        );
    }

    private void processReturnedValue(Object returnedValue, Consumer<Result> callback) {
        if (returnedValue instanceof Result) {
            callback.accept((Result) returnedValue);
        } else if (returnedValue instanceof CompletableFuture<?>) {
            val future = (CompletableFuture<?>) returnedValue;

            future.whenComplete((v, t) -> {
                if (t != null) {
                    // TODO
                    return;
                }

                processReturnedValue(v, callback);
            });
        } else if (returnedValue instanceof Supplier<?>) {
            processReturnedValue(((Supplier<?>) returnedValue).get(), callback);
        } else {
            callback.accept(Results.ok());
        }
    }

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Consumer<@NotNull Result> callback
    ) {
        val commandParameterExtractor = new InvocationParametersProcessor(context, this);
        commandParameterExtractor.setResultCallback(callback);
        commandParameterExtractor.setParameterCallback(parameters -> {
            try {
                processReturnedValue(method.invokeWithArguments(parameters), callback);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        commandParameterExtractor.process();
    }
}
