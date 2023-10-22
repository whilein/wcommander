package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.error.CommandErrorFactory;
import w.commander.execution.CommandExecutor;
import w.commander.execution.CommandHandler;
import w.commander.execution.EmptyCommandExecutor;
import w.commander.execution.NotEnoughArgumentsCommandExecutor;
import w.commander.manual.usage.CommandUsage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleCommandNodeFactory implements CommandNodeFactory {

    private static final CommandExecutor[] EMPTY = new CommandExecutor[]{EmptyCommandExecutor.getInstance()};

    CommandErrorFactory commandErrorFactory;

    public static CommandNodeFactory create(
            CommandErrorFactory commandErrorFactory
    ) {
        return new SimpleCommandNodeFactory(commandErrorFactory);
    }

    private static boolean trySetExecutor(
            Map<Integer, CommandExecutor> executors,
            CommandExecutor executor,
            int index
    ) {
        val existing = executors.get(index);

        if (existing != null && !existing.isOverrideable()) {
            if (executor.isOverrideable()) {
                return false;
            }

            throw new IllegalStateException("Executors conflict: " + executor + ", "
                                            + existing);
        }

        executors.put(index, executor);
        return true;
    }

    @Override
    public CommandNode create(List<CommandExecutor> executors, Map<String, CommandNode> subCommands) {
        if (executors.isEmpty()) {
            return new CommandNodeImpl(EMPTY, subCommands);
        }

        int maxArguments = 0;

        for (val executor : executors) {
            if (executor instanceof CommandHandler) {
                val handler = (CommandHandler) executor;
                maxArguments = Math.max(maxArguments, handler.argumentCount());
            }
        }

        val executorByArgumentMap = new HashMap<Integer, CommandExecutor>();

        for (val executor : executors) {
            if (executor instanceof CommandHandler) {
                val handler = (CommandHandler) executor;

                for (int i = handler.requiredArgumentCount(), j = handler.argumentCount(); i <= j; i++) {
                    trySetExecutor(executorByArgumentMap, executor, i);
                }
            } else {
                int i = 0;

                while (!trySetExecutor(executorByArgumentMap, executor, i)) {
                    i++;
                }
            }
        }

        int executorByArgumentLength = 0;

        for (val index : executorByArgumentMap.keySet()) {
            executorByArgumentLength = Math.max(executorByArgumentLength, index);
        }

        val executorByArgument = new CommandExecutor[executorByArgumentLength + 1];

        CommandHandler nextHandler = null;

        for (int i = executorByArgument.length - 1; i >= 0; i--) {
            val executor = executorByArgumentMap.get(i);

            if (executor != null) {
                executorByArgument[i] = executor;

                if (executor instanceof CommandHandler) {
                    nextHandler = (CommandHandler) executor;
                }

                continue;
            }

            CommandUsage usage;

            executorByArgument[i] = nextHandler != null && (usage = nextHandler.usage()) != null
                    ? NotEnoughArgumentsCommandExecutor.create(usage, commandErrorFactory)
                    : EmptyCommandExecutor.getInstance();
        }

        return new CommandNodeImpl(executorByArgument, subCommands);
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static final class CommandNodeImpl implements CommandNode {

        CommandExecutor[] executors;
        Map<String, CommandNode> subCommands;

        @Override
        public CommandNode subCommand(String name) {
            return subCommands.get(name);
        }

        @Override
        public CommandExecutor executor(int arguments) {
            val executors = this.executors;
            val maxArguments = executors.length;

            if (arguments >= maxArguments) {
                return executors[maxArguments - 1];
            }

            return executors[arguments];
        }
    }
}
