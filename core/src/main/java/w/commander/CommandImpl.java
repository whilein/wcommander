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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.execution.ExecutionContext;
import w.commander.executor.CommandExecutor;
import w.commander.executor.CommandHandler;
import w.commander.executor.ManualCommandExecutor;
import w.commander.manual.Manual;
import w.commander.result.Result;
import w.commander.result.Results;
import w.commander.tabcomplete.Suggestions;
import w.commander.util.Callback;
import w.commander.util.CallbackArrayCollector;
import w.commander.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
final class CommandImpl implements Command {

    @Getter
    @NotNull
    String name;

    @Getter
    @NotNull
    List<@NotNull String> aliases;

    @NotNull
    CommandNode tree;

    @NotNull
    CommanderConfig config;

    @Getter
    @NotNull
    CommandInfo info;

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

        suggestions.retain();

        val executor = subCommand.executor(0);
        executor.test(context, Callback.of((result, cause) -> {
            try {
                if (result != null && result.isSuccess()) {
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

                handler.getConditions().testVisibility(context, Callback.of((result, cause) -> {
                    try {
                        if (result != null && result.isSuccess()) {
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
        if (arguments.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        val context = config.getExecutionContextFactory().create(actor, arguments);

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

        val future = new CompletableFuture<List<String>>();
        val suggestions = new Suggestions(future);

        if (executor instanceof CommandHandler && lastIndex >= offset) {
            val handler = (CommandHandler) executor;

            addHandlerToSuggestions(context, handler,
                    lastIndex - offset,
                    lastValue,
                    suggestions);
        }

        if (lastIndex <= offset) {
            tree.forEach((key, value) -> addSubCommandToSuggestions(context, key, value, lastValue, suggestions));
        }

        suggestions.release();

        return future;
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
        val context = config.getExecutionContextFactory().create(actor, newArguments);

        val future = new CompletableFuture<Result>();

        executor.execute(context, Callback.of((result, cause) -> {
            if (cause != null) {
                result = config.getErrorResultFactory().onInternalError(context, cause);
            }

            if (result != null) {
                try {
                    result.dispatch(context);

                    future.complete(result);
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        }));

        return future;
    }

    private void addExecutors(CommandNode node, List<CommandExecutor> executors) {
        executors.add(node.executor(0));

        node.forEach((name, subcommand) -> addExecutors(subcommand, executors));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Result> test(@NotNull CommandActor actor) {
        val context = config.getExecutionContextFactory().create(actor, RawArguments.empty());

        val executors = new ArrayList<CommandExecutor>();
        addExecutors(tree, executors);

        val future = new CompletableFuture<Result>();

        val arrayCollector = new CallbackArrayCollector<Result>(
                Callback.of((results, cause) -> {
                    if (cause != null) {
                        future.completeExceptionally(cause);
                    } else if (results != null) {
                        for (val result : results) {
                            if (result.isSuccess()) {
                                future.complete(Results.ok());
                                return;
                            }
                        }

                        future.complete(Results.error());
                    }
                }), executors.size());

        int i = 0;
        for (val executor : executors) {
            executor.test(context, arrayCollector.element(i++));
        }

        return future;
    }

    @Override
    public @Nullable Manual getManual() {
        return tree.getManual();
    }

}
