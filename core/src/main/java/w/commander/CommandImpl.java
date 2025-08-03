/*
 *    Copyright 2025 Whilein
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
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.condition.Conditions;
import w.commander.execution.ExecutionContext;
import w.commander.executor.CommandExecutor;
import w.commander.executor.CommandHandler;
import w.commander.result.Result;
import w.commander.result.Results;
import w.commander.tabcomplete.Suggestions;
import w.commander.util.Callback;
import w.commander.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class CommandImpl implements Command {

    private static final RawArguments EMPTY_TABCOMPLETE = RawArguments.fromTrustedArray("");

    String name;
    List<String> aliases;

    @Getter(AccessLevel.NONE)
    CommandNode tree;
    CommandInfo info;

    @Getter(AccessLevel.NONE)
    CommanderConfig config;

    Conditions testConditions;

    public CommandImpl(
            @NotNull String name,
            @NotNull List<@NotNull String> aliases,
            @NotNull CommandNode tree,
            @NotNull CommandInfo info,
            @NotNull CommanderConfig config
    ) {
        this.name = name;
        this.aliases = aliases;
        this.tree = tree;
        this.info = info;
        this.config = config;

        val executors = new ArrayList<CommandExecutor>();
        addExecutors(tree, executors);

        Conditions testConditions = Conditions.empty();
        for (val executor : executors) {
            testConditions = testConditions.merge(executor.getConditions().visibilityConditions());
        }
        this.testConditions = testConditions;
    }

    private static void addExecutors(CommandNode node, List<CommandExecutor> executors) {
        executors.add(node.executor(0));

        node.forEach((name, subcommand) -> addExecutors(subcommand, executors));
    }

    private void addSubCommandToSuggestions(
            ExecutionContext context,
            String subCommandName,
            CommandNode subCommand,
            String argumentValue,
            Suggestions suggestions
    ) {
        if (!StringUtils.startsWithIgnoreCase(subCommandName, argumentValue)) {
            return;
        }

        val executor = subCommand.executor(0);
        if (executor instanceof CommandHandler && ((CommandHandler) executor).getManualEntry().isHidden()) {
            return;
        }

        suggestions.retain();

        executor.test(context, Callback.of((result, cause) -> {
            try {
                if (cause != null) {
                    suggestions.exceptionCaught(cause);
                } else if (result != null && result.isSuccess()) {
                    suggestions.next(subCommandName);
                }
            } finally {
                suggestions.release();
            }
        }));
    }

    private void addHandlerToSuggestions(
            ExecutionContext context,
            CommandHandler handler,
            int argument,
            String argumentValue,
            Suggestions suggestions
    ) {
        if (handler != null && !handler.getManualEntry().isHidden()) {
            val arguments = handler.getParameters().getArguments();

            if (argument < arguments.size()) {
                suggestions.retain();

                handler.test(context, Callback.of((result, cause) -> {
                    try {
                        if (cause != null) {
                            suggestions.exceptionCaught(cause);
                        } else if (result != null && result.isSuccess()) {
                            val tabCompleter = arguments.get(argument)
                                    .getTabCompleter();

                            if (tabCompleter != null) {
                                tabCompleter.getSuggestions(context, argumentValue, suggestions);
                            }
                        }
                    } finally {
                        suggestions.release();
                    }
                }));
            }
        }
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<String>> tabComplete(@NotNull CommandActor actor, @NotNull RawArguments arguments) {
        val context = createContext(actor, arguments);

        if (arguments.isEmpty()) {
            arguments = EMPTY_TABCOMPLETE;
        }

        CommandNode tree = this.tree;
        int offset = 0;

        for (int i = 0, j = arguments.size() - 1; i < j; i++) {
            val nextSubcommand = tree.subCommand(arguments.value(i).toLowerCase());
            if (nextSubcommand == null) {
                break;
            }

            offset = i + 1;
            tree = nextSubcommand;
        }

        val executor = tree.executor(arguments.size());
        val lastIndex = arguments.size() - 1;
        val lastValue = arguments.value(lastIndex);

        val finalTree = tree;
        val finalOffset = offset;

        val hasHandler = executor instanceof CommandHandler && lastIndex >= finalOffset;
        val hasSubCommands = lastIndex <= finalOffset;

        if (hasHandler || hasSubCommands) {
            val future = new CompletableFuture<List<String>>();
            val suggestions = new Suggestions(future);

            finalTree.getSetupHandlers().setup(context, Callback.of((result, cause) -> {
                try {
                    if (cause != null) {
                        suggestions.exceptionCaught(cause);
                    } else if (result != null && result.isSuccess()) {
                        if (hasHandler) {
                            val handler = (CommandHandler) executor;

                            addHandlerToSuggestions(context, handler,
                                    lastIndex - finalOffset,
                                    lastValue, suggestions);
                        }

                        if (hasSubCommands) {
                            finalTree.forEach((key, value) -> addSubCommandToSuggestions(context, key, value,
                                    lastValue, suggestions));
                        }
                    }
                } finally {
                    suggestions.release();
                }
            }));

            return future;
        }

        return CompletableFuture.completedFuture(Collections.emptyList());
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
        val context = createContext(actor, newArguments);

        val future = new CompletableFuture<Result>();

        tree.getSetupHandlers().setup(context, Callback.mappingException(
                sr -> {
                    if (!sr.isSuccess()) {
                        dispatch(context, sr, future);
                        return;
                    }

                    executor.execute(context, Callback.mappingException(
                            er -> dispatch(context, er, future),
                            cause -> fromCause(context, cause)));
                },
                cause -> fromCause(context, cause)
        ));

        return future;
    }

    private static void dispatch(ExecutionContext context, Result result, CompletableFuture<Result> future) {
        try {
            result.dispatch(context);

            future.complete(result);
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
    }

    private Result fromCause(ExecutionContext context, Throwable cause) {
        if (cause instanceof Result) {
            return (Result) cause;
        } else {
            return config.getErrorResultFactory().onInternalError(context, cause);
        }
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Result> test(@NotNull CommandActor actor) {
        if (testConditions.isEmpty() || testConditions.isAlwaysTrue()) {
            return Results.ok().asFuture();
        }

        val context = createContext(actor, RawArguments.empty());

        val future = new CompletableFuture<Result>();
        testConditions.testAnyVisibility(context, Callback.ofFuture(future));
        return future;
    }

    private ExecutionContext createContext(CommandActor actor, RawArguments args) {
        return config.getExecutionContextFactory().create(actor, args,
                config.getAttributeStoreFactory().create());
    }

}
