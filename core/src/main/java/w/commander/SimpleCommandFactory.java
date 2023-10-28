package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.execution.CommandExecutionContextFactory;
import w.commander.execution.CommandExecutor;
import w.commander.execution.ManualCommandExecutor;
import w.commander.execution.MethodHandleCommandHandler;
import w.commander.manual.CommandManualFactory;
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
    CommandExecutionContextFactory commandExecutionContextFactory;
    CommandManualFactory commandManualFactory;

    public static CommandFactory create(
            CommandNodeFactory commandNodeFactory,
            CommandExecutionContextFactory commandExecutionContextFactory,
            CommandManualFactory commandManualFactory
    ) {
        return new SimpleCommandFactory(
                commandNodeFactory,
                commandExecutionContextFactory,
                commandManualFactory
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
            val manualExecutor = ManualCommandExecutor.create(commandManualFactory.create(spec));

            if (manual.hasHandler()) {
                commandExecutors.add(manualExecutor);
            }

            val subCommand = manual.getSubCommand();

            if (subCommand != null) {
                subCommands.putIfAbsent(subCommand, commandNodeFactory.create(
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
    public Command create(CommandSpec spec) {
        return SimpleCommand.create(
                spec.getName(),
                Collections.unmodifiableList(Arrays.asList(spec.getAliases())),
                createTree(spec),
                commandExecutionContextFactory
        );
    }

}
