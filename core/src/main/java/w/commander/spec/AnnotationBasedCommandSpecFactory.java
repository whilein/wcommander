package w.commander.spec;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.annotation.*;
import w.commander.manual.description.Description;
import w.commander.manual.description.DescriptionFactory;
import w.commander.manual.usage.Usage;
import w.commander.manual.usage.UsageFactory;
import w.commander.parameter.*;
import w.commander.parameter.argument.Argument;
import w.commander.spec.path.HandlerPathStrategy;
import w.commander.spec.template.CommandTemplate;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnotationBasedCommandSpecFactory implements CommandSpecFactory {

    HandlerPathStrategy handlerPathStrategy;
    UsageFactory usageFactory;
    DescriptionFactory descriptionFactory;
    Iterable<? extends HandlerParameterResolver> parameterResolvers;

    public static @NotNull CommandSpecFactory create(
            @NotNull HandlerPathStrategy handlerPathStrategy,
            @NotNull UsageFactory usageFactory,
            @NotNull DescriptionFactory descriptionFactory,
            @NotNull Iterable<? extends @NotNull HandlerParameterResolver> parameterResolvers
    ) {
        return new AnnotationBasedCommandSpecFactory(
                handlerPathStrategy,
                usageFactory,
                descriptionFactory,
                parameterResolvers
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
                .subCommand(manualSubCommand == null ? null : manualSubCommand.value())
                .build();
    }

    private HandlerParameter resolveParameter(Parameter parameter) {
        for (val resolver : parameterResolvers) {
            if (resolver.isSupported(parameter)) {
                return resolver.resolve(parameter);
            }
        }

        throw new UnsupportedOperationException("There are no available parameter resolvers for " + parameter);
    }

    private HandlerParameters getParameters(Method method) {
        val methodParameters = method.getParameters();

        if (methodParameters.length == 0) {
            return EmptyHandlerParameters.getInstance();
        }

        val parameters = new HandlerParameter[methodParameters.length];

        for (int i = 0, j = methodParameters.length; i < j; i++) {
            parameters[i] = resolveParameter(methodParameters[i]);
        }

        return SimpleHandlerParameters.create(parameters);
    }

    private static List<String> path(List<String> parentPath, String name) {
        val path = new ArrayList<>(parentPath);
        path.add(name);

        return path;
    }

    private Usage getUsage(List<String> path, List<? extends Argument> arguments) {
        return usageFactory.create(String.join(" ", path), arguments);
    }

    private Description getDescription(Method method) {
        val description = method.getDeclaredAnnotation(WithDescription.class);

        return descriptionFactory.create(description != null
                ? description.value()
                : "");
    }

    private HandlerSpec createHandler(List<String> path, String name, Method method) {
        val hidden = isHidden(method);
        val parameters = getParameters(method);

        return ImmutableHandlerSpec.builder()
                .method(method)
                .usage(getUsage(path, parameters.getArguments()))
                .description(hidden ? null : getDescription(method))
                .path(handlerPathStrategy.getPath(path(path, name)))
                .parameters(parameters)
                .build();
    }

    private CommandSpec create(
            List<String> parentPath,
            String name,
            CommandTemplate template
    ) {
        CommandSpecValidation.checkCommandName(name);

        val commandInstance = template.getInstance();
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
                        .path(handlerPathStrategy.getPath(path(path, subCommandName)))
                        .type(commandType)
                        .instance(commandInstance)
                        .aliases(getAliases(method))
                        .manual(ManualSpec.EMPTY)
                        .addHandlers(createHandler(path, subCommandName, method))
                        .build());
            }
        }

        for (val subCommand : template.getSubCommands()) {
            subCommands.add(createSubCommand(path, subCommand));
        }

        return ImmutableCommandSpec.builder()
                .name(name)
                .path(handlerPathStrategy.getPath(path))
                .type(commandType)
                .instance(commandInstance)
                .aliases(getAliases(commandType))
                .manual(getManual(commandType))
                .handlers(Collections.unmodifiableList(handlers))
                .subCommands(Collections.unmodifiableList(subCommands))
                .build();
    }

    private CommandSpec createSubCommand(List<String> path, CommandTemplate template) {
        val subCommand = template.getInstance().getClass().getDeclaredAnnotation(SubCommand.class);

        if (subCommand == null) {
            throw new IllegalArgumentException("Sub command must be annotated with @SubCommand");
        }

        return create(path, subCommand.value(), template);
    }

    private CommandSpec createCommand(List<String> path, CommandTemplate template) {
        val command = template.getInstance().getClass().getDeclaredAnnotation(Command.class);

        if (command == null) {
            throw new IllegalArgumentException("Command must be annotated with @Command");
        }

        return create(path, command.value(), template);
    }

    @Override
    public @NotNull CommandSpec create(@NotNull CommandTemplate template) {
        return createCommand(new ArrayList<>(), template);
    }


}
