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

    public static @NotNull CommandHandler create(
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

    @Override
    protected void doExecute(
            @NotNull ExecutionContext context,
            @NotNull Callback<@NotNull Result> callback
    ) {
        val invocation = new MethodHandleInvocation(
                this,
                method,
                context,
                callback
        );
        invocation.process();
    }
}
