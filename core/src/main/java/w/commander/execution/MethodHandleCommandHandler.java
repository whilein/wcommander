package w.commander.execution;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.internal.MethodHandleInvocation;
import w.commander.manual.usage.Usage;
import w.commander.parameter.HandlerParameters;
import w.commander.result.Result;
import w.commander.util.Callback;

import java.lang.invoke.MethodHandle;
import java.util.concurrent.Executor;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MethodHandleCommandHandler extends AbstractCommandHandler {

    MethodHandle method;
    Executor executor;

    private MethodHandleCommandHandler(
            String path,
            Usage usage,
            MethodHandle method,
            HandlerParameters parameters,
            Executor executor
    ) {
        super(path, parameters, usage);

        this.method = method;
        this.executor = executor;
    }

    public static @NotNull CommandHandler create(
            @NotNull String path,
            @NotNull Usage usage,
            @NotNull MethodHandle mh,
            @NotNull HandlerParameters parameters,
            @NotNull Executor executor
    ) {
        return new MethodHandleCommandHandler(
                path,
                usage,
                mh,
                parameters,
                executor
        );
    }

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        val invocation = new MethodHandleInvocation(
                this,
                method,
                context,
                callback,
                executor
        );
        invocation.process();
    }
}
