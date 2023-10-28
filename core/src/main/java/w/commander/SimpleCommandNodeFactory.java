package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.CommandExecutor;
import w.commander.execution.CommandHandler;
import w.commander.execution.EmptyCommandExecutor;
import w.commander.execution.NotEnoughArgumentsCommandExecutor;
import w.commander.manual.usage.Usage;

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

    ErrorResultFactory errorResultFactory;

    public static @NotNull CommandNodeFactory create(
            @NotNull ErrorResultFactory errorResultFactory
    ) {
        return new SimpleCommandNodeFactory(errorResultFactory);
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
    public @NotNull CommandNode create(
            @NotNull List<@NotNull CommandExecutor> executors,
            @NotNull Map<@NotNull String, @NotNull CommandNode> subCommands
    ) {
        if (executors.isEmpty()) {
            return new CommandNodeImpl(EMPTY, subCommands);
        }

        int maxArguments = 0;

        for (val executor : executors) {
            if (executor instanceof CommandHandler) {
                val handler = (CommandHandler) executor;
                maxArguments = Math.max(maxArguments, handler.getArgumentCount());
            }
        }

        val executorByArgumentMap = new HashMap<Integer, CommandExecutor>();

        for (val executor : executors) {
            if (executor instanceof CommandHandler) {
                val handler = (CommandHandler) executor;

                for (int i = handler.getRequiredArgumentCount(), j = handler.getArgumentCount(); i <= j; i++) {
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

            Usage usage;

            executorByArgument[i] = nextHandler != null && (usage = nextHandler.getUsage()) != null
                    ? NotEnoughArgumentsCommandExecutor.create(usage, errorResultFactory)
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
        public @Nullable CommandNode subCommand(@NotNull String name) {
            return subCommands.get(name);
        }

        @Override
        public @NotNull CommandExecutor executor(int arguments) {
            val executors = this.executors;
            val maxArguments = executors.length;

            if (arguments >= maxArguments) {
                return executors[maxArguments - 1];
            }

            return executors[arguments];
        }
    }
}
