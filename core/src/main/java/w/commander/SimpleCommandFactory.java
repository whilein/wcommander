package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.*;
import w.commander.manual.ManualFactory;
import w.commander.spec.CommandSpec;
import w.commander.spec.HandlerSpec;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleCommandFactory implements CommandFactory {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    CommandNodeFactory commandNodeFactory;
    ExecutionContextFactory executionContextFactory;
    ExecutionThrowableInterceptor executionThrowableInterceptor;
    ManualFactory manualFactory;

    public static @NotNull CommandFactory create(
            @NotNull CommandNodeFactory commandNodeFactory,
            @NotNull ExecutionContextFactory executionContextFactory,
            @NotNull ExecutionThrowableInterceptor executionThrowableInterceptor,
            @NotNull ManualFactory manualFactory
    ) {
        return new SimpleCommandFactory(
                commandNodeFactory,
                executionContextFactory,
                executionThrowableInterceptor,
                manualFactory
        );
    }

    @SneakyThrows
    private CommandExecutor createExecutor(HandlerSpec handler, CommandSpec command) {
        return MethodHandleCommandHandler.create(
                handler.getPath(),
                handler.getUsage(),
                LOOKUP.unreflect(handler.getMethod())
                        .bindTo(command.getInstance()),
                handler.getParameters()
        );
    }

    private CommandNode createTree(CommandSpec spec) {
        val subCommands = spec.getSubCommands().stream()
                .collect(Collectors.toMap(
                        CommandSpec::getName,
                        this::createTree
                ));

        val commandExecutors = spec.getHandlers().stream()
                .map(handler -> createExecutor(handler, spec))
                .collect(Collectors.toList());

        val manual = spec.getManual();

        if (manual.hasHandler() || manual.getSubCommand() != null) {
            val manualExecutor = ManualCommandExecutor.create(manualFactory.create(spec));

            if (manual.hasHandler()) {
                commandExecutors.add(manualExecutor);
            }

            val subCommand = manual.getSubCommand();

            if (subCommand != null) {
                subCommands.putIfAbsent(subCommand, commandNodeFactory.create(
                        Collections.singletonList(manualExecutor),
                        Collections.emptyMap()));
            }

            val subcommandAliases = manual.getSubcommandAliases();

            for (val alias : subcommandAliases) {
                subCommands.putIfAbsent(alias, commandNodeFactory.create(
                        Collections.singletonList(manualExecutor),
                        Collections.emptyMap()));
            }
        }

        return commandNodeFactory.create(
                commandExecutors,
                subCommands
        );
    }

    @Override
    public @NotNull Command create(@NotNull CommandSpec spec) {
        return SimpleCommand.create(
                spec.getName(),
                Collections.unmodifiableList(Arrays.asList(spec.getAliases())),
                createTree(spec),
                executionContextFactory,
                executionThrowableInterceptor
        );
    }

}
