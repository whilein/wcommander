package w.commander.spec;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.annotation.*;
import w.commander.manual.description.CommandDescription;
import w.commander.manual.description.CommandDescriptionFactory;
import w.commander.manual.usage.CommandUsage;
import w.commander.manual.usage.CommandUsageFactory;
import w.commander.parameter.CommandParameter;
import w.commander.parameter.CommandParameterResolvers;
import w.commander.parameter.argument.CommandArgument;
import w.commander.spec.path.CommandHandlerPathStrategy;
import w.commander.spec.template.CommandTemplate;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationBasedCommandSpecFactory implements CommandSpecFactory {

    CommandHandlerPathStrategy commandHandlerPathStrategy;
    CommandParameterResolvers commandParameterResolvers;

    CommandUsageFactory commandUsageFactory;
    CommandDescriptionFactory commandDescriptionFactory;

    public static CommandSpecFactory create(
            CommandHandlerPathStrategy commandHandlerPathStrategy,
            CommandParameterResolvers commandParameterResolvers,
            CommandUsageFactory commandUsageFactory,
            CommandDescriptionFactory commandDescriptionFactory

    ) {
        return new AnnotationBasedCommandSpecFactory(
                commandHandlerPathStrategy,
                commandParameterResolvers,
                commandUsageFactory,
                commandDescriptionFactory
        );
    }

    private static String[] getAliases(AnnotatedElement element) {
        val alias = element.getDeclaredAnnotation(WithAlias.class);

        if (alias != null) {
            val aliasValue = alias.value();
            CommandSpecValidation.checkCommandName(aliasValue);

            return new String[]{aliasValue};
        }

        val aliases = element.getDeclaredAnnotation(WithAliases.class);

        if (aliases != null) {
            val aliasesValues = Arrays.stream(aliases.value())
                    .map(WithAlias::value)
                    .toArray(String[]::new);

            CommandSpecValidation.checkCommandNames(aliasesValues);

            return aliasesValues;
        }

        return new String[0];
    }

    private static boolean isHidden(Method method) {
        return method.isAnnotationPresent(Hidden.class);
    }

    private static ManualSpec getManual(Class<?> commandType) {
        val manualHandler = commandType.isAnnotationPresent(WithManual.class);
        val manualSubCommand = commandType.getDeclaredAnnotation(WithManualSubCommand.class);

        if (!manualHandler && manualSubCommand == null) {
            return ManualSpec.EMPTY;
        }

        return ImmutableManualSpec.builder()
                .hasHandler(manualHandler)
                .subCommand(Optional.ofNullable(manualSubCommand)
                        .map(WithManualSubCommand::value))
                .build();
    }

    private CommandParameter[] getParameters(Method method) {
        val methodParameters = method.getParameters();
        val parameters = new CommandParameter[methodParameters.length];

        for (int i = 0, j = methodParameters.length; i < j; i++) {
            parameters[i] = commandParameterResolvers.getParameter(methodParameters[i]);
        }

        return parameters;
    }

    private static List<String> path(List<String> parentPath, String name) {
        val path = new ArrayList<>(parentPath);
        path.add(name);

        return path;
    }

    private CommandUsage getUsage(List<String> path, CommandArgument[] arguments) {
        return commandUsageFactory.create(String.join(" ", path), arguments);
    }

    private CommandDescription getDescription(Method method) {
        val description = method.getDeclaredAnnotation(WithDescription.class);

        return commandDescriptionFactory.create(description != null
                ? description.value()
                : "");
    }

    private static CommandArgument[] getArguments(CommandParameter[] parameters) {
        return Arrays.stream(parameters)
                .filter(CommandArgument.class::isInstance)
                .map(CommandArgument.class::cast)
                .toArray(CommandArgument[]::new);
    }

    private HandlerSpec createHandler(List<String> path, String name, Method method) {
        val hidden = isHidden(method);
        val parameters = getParameters(method);

        return ImmutableHandlerSpec.builder()
                .method(method)
                .usage(getUsage(path, getArguments(parameters)))
                .description(hidden ? null : getDescription(method))
                .path(commandHandlerPathStrategy.getPath(path(path, name)))
                .parameters(parameters)
                .build();
    }

    private CommandSpec create(
            List<String> parentPath,
            String name,
            CommandTemplate template
    ) {
        CommandSpecValidation.checkCommandName(name);

        val commandInstance = template.instance();
        val commandType = commandInstance.getClass();

        val path = path(parentPath, name);

        val handlers = new ArrayList<HandlerSpec>();
        val subCommands = new ArrayList<CommandSpec>();

        for (val method : commandType.getMethods()) {
            val commandHandler = method.getDeclaredAnnotation(CommandHandler.class);

            if (commandHandler != null) {
                handlers.add(createHandler(path, commandHandler.value(), method));
            }

            val subCommandHandler = method.getDeclaredAnnotation(SubCommandHandler.class);

            if (subCommandHandler != null) {
                val subCommandName = subCommandHandler.value();
                CommandSpecValidation.checkCommandName(subCommandName);

                subCommands.add(ImmutableCommandSpec.builder()
                        .name(subCommandHandler.value())
                        .path(commandHandlerPathStrategy.getPath(path(path, subCommandName)))
                        .type(commandType)
                        .instance(commandInstance)
                        .aliases(getAliases(method))
                        .manual(ManualSpec.EMPTY)
                        .addHandlers(createHandler(path, subCommandName, method))
                        .build());
            }
        }

        for (val subCommand : template.subCommands()) {
            subCommands.add(createSubCommand(path, subCommand));
        }

        return ImmutableCommandSpec.builder()
                .name(name)
                .path(commandHandlerPathStrategy.getPath(path))
                .type(commandType)
                .instance(commandInstance)
                .aliases(getAliases(commandType))
                .manual(getManual(commandType))
                .handlers(Collections.unmodifiableList(handlers))
                .subCommands(Collections.unmodifiableList(subCommands))
                .build();
    }

    private CommandSpec createSubCommand(List<String> path, CommandTemplate template) {
        val subCommand = template.instance().getClass().getDeclaredAnnotation(SubCommand.class);

        if (subCommand == null) {
            throw new IllegalArgumentException("Sub command must be annotated with @SubCommand");
        }

        return create(path, subCommand.value(), template);
    }

    private CommandSpec createCommand(List<String> path, CommandTemplate template) {
        val command = template.instance().getClass().getDeclaredAnnotation(Command.class);

        if (command == null) {
            throw new IllegalArgumentException("Command must be annotated with @Command");
        }

        return create(path, command.value(), template);
    }

    @Override
    public CommandSpec create(CommandTemplate template) {
        return createCommand(new ArrayList<>(), template);
    }


}
