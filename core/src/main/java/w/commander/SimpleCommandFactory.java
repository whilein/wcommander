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
                handler.path(),
                handler.usage(),
                LOOKUP.unreflect(handler.method())
                        .bindTo(command.instance()),
                handler.parameters()
        );
    }

    private CommandNode createTree(CommandSpec spec) {
        val subCommands = spec.subCommands().stream()
                .collect(Collectors.toMap(
                        CommandSpec::name,
                        this::createTree
                ));

        val commandExecutors = spec.handlers().stream()
                .map(handler -> createExecutor(handler, spec))
                .collect(Collectors.toList());

        val manual = spec.manual();

        if (manual.hasHandler() || manual.getSubCommand().isPresent()) {
            val manualExecutor = ManualCommandExecutor.create(commandManualFactory.create(spec));

            if (manual.hasHandler()) {
                commandExecutors.add(manualExecutor);
            }

            manual.getSubCommand().ifPresent(name -> subCommands.putIfAbsent(name, commandNodeFactory.create(
                    Collections.singletonList(manualExecutor),
                    Collections.emptyMap())));
        }

        return commandNodeFactory.create(
                commandExecutors,
                subCommands
        );
    }

    @Override
    public Command create(CommandSpec spec) {
        return SimpleCommand.create(
                spec.name(),
                Collections.unmodifiableList(Arrays.asList(spec.aliases())),
                createTree(spec),
                commandExecutionContextFactory
        );
    }

}
