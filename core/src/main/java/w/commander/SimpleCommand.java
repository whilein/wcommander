package w.commander;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContextFactory;
import w.commander.execution.ExecutionThrowableInterceptor;
import w.commander.result.Result;
import w.commander.util.Callback;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleCommand implements Command {

    @Getter
    String name;

    @Getter
    List<String> aliases;

    CommandNode tree;

    ExecutionContextFactory executionContextFactory;

    ExecutionThrowableInterceptor executionThrowableInterceptor;

    public static @NotNull Command create(
            @NotNull String name,
            @NotNull List<@NotNull String> aliases,
            @NotNull CommandNode tree,
            @NotNull ExecutionContextFactory executionContextFactory,
            @NotNull ExecutionThrowableInterceptor executionThrowableInterceptor
    ) {
        return new SimpleCommand(
                name,
                aliases,
                tree,
                executionContextFactory,
                executionThrowableInterceptor
        );
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Result> execute(
            @NotNull CommandActor actor,
            @NotNull RawArguments arguments
    ) {
        CommandNode tree = this.tree;
        int offset = 0;

        for (int i = 0, j = arguments.size(); i < j; i++) {
            val nextSubcommand = tree.subCommand(arguments.value(i).toLowerCase());

            if (nextSubcommand == null) {
                break;
            }

            offset = i + 1;
            tree = nextSubcommand;
        }

        val newArguments = arguments.withOffset(offset);

        val executor = tree.executor(newArguments.size());
        val context = executionContextFactory.create(actor, executor, newArguments);

        val future = new CompletableFuture<Result>();

        executor.execute(context, Callback.of((result, cause) -> {
            if (result != null) {
                result.dispatch(context);

                future.complete(result);
            } else if (cause != null) {
                executionThrowableInterceptor.intercept(cause)
                        .dispatch(context);

                future.completeExceptionally(cause);
            }
        }));

        return future;
    }


}
