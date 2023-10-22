package w.commander;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.execution.CommandExecutionContextFactory;

import java.util.List;

/**
 * @author whilein
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleCommand implements Command {

    @Getter
    String name;

    @Getter
    List<String> aliases;

    CommandNode tree;

    CommandExecutionContextFactory commandExecutionContextFactory;

    public static Command create(
            String name,
            List<String> aliases,
            CommandNode tree,
            CommandExecutionContextFactory commandExecutionContextFactory
    ) {
        return new SimpleCommand(
                name,
                aliases,
                tree,
                commandExecutionContextFactory
        );
    }

    @Override
    public void execute(CommandSender sender, RawCommandArguments arguments) {
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
        val context = commandExecutionContextFactory.create(sender, executor, newArguments);
        executor.execute(context, result -> result.answer(context));
    }


}
