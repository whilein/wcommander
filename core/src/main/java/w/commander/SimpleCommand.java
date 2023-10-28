package w.commander;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContextFactory;

import java.util.List;

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

    public static @NotNull Command create(
            @NotNull String name,
            @NotNull List<@NotNull String> aliases,
            @NotNull CommandNode tree,
            @NotNull ExecutionContextFactory executionContextFactory
    ) {
        return new SimpleCommand(
                name,
                aliases,
                tree,
                executionContextFactory
        );
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull RawArguments arguments) {
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
        val context = executionContextFactory.create(sender, executor, newArguments);
        executor.execute(context, result -> result.answer(context));
    }


}
