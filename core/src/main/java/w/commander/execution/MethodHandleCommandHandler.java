package w.commander.execution;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.manual.usage.CommandUsage;
import w.commander.parameter.CommandParameter;
import w.commander.parameter.argument.CommandArgument;
import w.commander.result.CommandResult;
import w.commander.result.CommandResults;

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
    CommandParameter[] parameters;

    private MethodHandleCommandHandler(
            String path,
            int argumentCount,
            int requiredArgumentCount,
            CommandUsage usage,
            MethodHandle method,
            CommandParameter[] parameters
    ) {
        super(path, argumentCount, requiredArgumentCount, usage);

        this.method = method;
        this.parameters = parameters;
    }

    public static CommandHandler create(
            String path,
            CommandUsage usage,
            MethodHandle mh,
            CommandParameter[] parameters
    ) {
        int argumentCount = 0;
        int requiredArgumentCount = 0;

        for (val parameter : parameters) {
            if (!(parameter instanceof CommandArgument)) {
                continue;
            }

            argumentCount++;

            val argument = (CommandArgument) parameter;

            if (argument.isRequired()) {
                requiredArgumentCount++;
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

    private void processReturnedValue(Object returnedValue, Consumer<CommandResult> callback) {
        if (returnedValue instanceof CommandResult) {
            callback.accept((CommandResult) returnedValue);
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
            callback.accept(CommandResults.ok());
        }
    }

    @Override
    protected void doExecute(CommandExecutionContext context, Consumer<CommandResult> callback) {
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
