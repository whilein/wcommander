/*
 *    Copyright 2024 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContextFactory;
import w.commander.executor.CommandExecutor;
import w.commander.executor.CommandHandler;
import w.commander.executor.ManualCommandExecutor;
import w.commander.executor.MethodCommandHandler;
import w.commander.executor.MethodHandleExecutor;
import w.commander.executor.NotEnoughArgumentsCommandHandler;
import w.commander.manual.Manual;
import w.commander.manual.ManualFactory;
import w.commander.manual.ManualFormatter;
import w.commander.spec.CommandSpec;
import w.commander.spec.HandlerSpec;
import w.commander.util.Immutables;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class CommandFactory {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final CommandExecutor[] EMPTY = new CommandExecutor[]{
            CommandExecutor.empty()
    };

    ExecutionContextFactory executionContextFactory;
    ManualFactory manualFactory;
    ManualFormatter manualFormatter;
    ErrorResultFactory errorResultFactory;

    @SneakyThrows
    private CommandExecutor createExecutor(HandlerSpec handler, CommandSpec command) {
        val executor = new MethodHandleExecutor(LOOKUP.unreflect(handler.getMethod())
                .bindTo(command.getInstance()));

        val wrappedExecutor = handler.getDecorators().wrap(handler, executor);

        return new MethodCommandHandler(
                handler.getPath(),
                handler.getManualEntry(),
                handler.getParameters(),
                handler.getConditions(),
                wrappedExecutor
        );
    }

    private CommandExecutor[] createExecutors(
            List<CommandExecutor> executors
    ) {
        if (executors.isEmpty()) {
            return EMPTY;
        }

        val executorByArgumentMap = new HashMap<Integer, CommandExecutor>();

        for (val executor : executors) {
            if (executor instanceof CommandHandler) {
                val handler = (CommandHandler) executor;
                val parameters = handler.getParameters();

                for (int i = parameters.getRequiredArgumentCount(), j = parameters.getArgumentCount(); i <= j; i++) {
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

            executorByArgument[i] = nextHandler != null
                    ? new NotEnoughArgumentsCommandHandler(nextHandler, errorResultFactory)
                    : CommandExecutor.empty();
        }

        return executorByArgument;
    }

    private static boolean trySetExecutor(
            Map<Integer, CommandExecutor> executors,
            CommandExecutor executor,
            int index
    ) {
        val existing = executors.get(index);

        if (existing != null && !existing.isYielding()) {
            if (executor.isYielding()) {
                return false;
            }

            throw new IllegalStateException("Executors conflict: " + executor + ", "
                                            + existing);
        }

        executors.put(index, executor);
        return true;
    }

    private CommandNode createNode(CommandSpec spec) {
        val subCommands = new HashMap<String, CommandNode>();
        for (val subCommand : spec.getSubCommands()) {
            for (val name : subCommand.getNames()) {
                subCommands.put(name.toLowerCase(), createNode(subCommand));
            }
        }

        val commandExecutors = spec.getHandlers().stream()
                .map(handler -> createExecutor(handler, spec))
                .collect(Collectors.toList());

        Manual manual;
        val manualSpec = spec.getManual();
        if (!manualSpec.isEmpty()) {
            val manualExecutor = new ManualCommandExecutor(manual = manualFactory.create(spec),
                    manualFormatter, errorResultFactory);

            if (manualSpec.isHasHandler()) {
                commandExecutors.add(manualExecutor);
            }

            val subCommand = manualSpec.getSubCommand();
            if (subCommand != null) {
                val manualNode = new CommandNode(manualExecutor);

                for (val name : subCommand.getNames()) {
                    subCommands.putIfAbsent(name.toLowerCase(), manualNode);
                }
            }
        } else {
            manual = null;
        }

        return new CommandNode(
                createExecutors(commandExecutors),
                Immutables.immutableMap(subCommands),
                manual
        );
    }

    public @NotNull Command create(@NotNull CommandSpec spec) {
        return new CommandImpl(
                spec.getName(),
                spec.getAliases(),
                createNode(spec),
                executionContextFactory,
                errorResultFactory,
                spec.getInstance()
        );
    }

}
