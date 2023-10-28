package w.commander.execution;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.manual.usage.Usage;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.argument.Argument;
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
    HandlerParameter[] parameters;

    private MethodHandleCommandHandler(
            String path,
            int argumentCount,
            int requiredArgumentCount,
            Usage usage,
            MethodHandle method,
            HandlerParameter[] parameters
    ) {
        super(path, argumentCount, requiredArgumentCount, usage);

        this.method = method;
        this.parameters = parameters;
    }

    public static CommandHandler create(
            @NotNull String path,
            @NotNull Usage usage,
            @NotNull MethodHandle mh,
            @NotNull HandlerParameter @NotNull  [] parameters
    ) {
        int argumentCount = 0;
        int requiredArgumentCount = 0;

        for (val parameter : parameters) {
            if (!(parameter instanceof Argument)) {
                continue;
            }

            argumentCount++;

            val argument = (Argument) parameter;

            if (argument.isRequired()) {
                requiredArgumentCount += argument.getMinLength();
            }
        }

        return new MethodHandleCommandHandler(
                path,
                argumentCount,
                requiredArgumentCount,
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
        val commandParameterExtractor = new InvocationParameterExtractor(context, this, parameters);

        commandParameterExtractor.extract(
                parameters -> {
                    try {
                        processReturnedValue(method.invokeWithArguments(parameters), callback);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                },
                callback
        );
    }
}
