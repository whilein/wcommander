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

package w.commander.spec;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandGraph;
import w.commander.CommanderConfig;
import w.commander.annotation.WithManualSubCommandData;
import w.commander.condition.Condition;
import w.commander.condition.ConditionFactory;
import w.commander.condition.Conditions;
import w.commander.decorator.Decorator;
import w.commander.decorator.DecoratorFactory;
import w.commander.decorator.Decorators;
import w.commander.executor.HandlerPath;
import w.commander.manual.FormattingText;
import w.commander.manual.ManualEntry;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.HandlerParameters;
import w.commander.parameter.argument.Argument;

import java.lang.annotation.Annotation;
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
@RequiredArgsConstructor
public final class CommandSpecFactory {

    CommanderConfig config;

    private static ManualSubCommandSpec getManualSubCommand(WithManualSubCommandData subCommand) {
        if (subCommand == null) return null;

        return ManualSubCommandSpec.of(
                subCommand.getValue(),
                Arrays.asList(subCommand.getAliases())
        );
    }

    private List<String> getAliases(AnnotatedElement element) {
        val aliases = config.getAnnotationScanner().getAliases(element);

        CommandSpecValidation.checkCommandNames(aliases);

        return aliases;
    }

    private ManualSpec getManual(Class<?> commandType) {
        val manualHandler = config.getAnnotationScanner().isHasManual(commandType);
        val manualSubCommand = config.getAnnotationScanner().getManualSubCommand(commandType);

        return ManualSpec.of(manualHandler, getManualSubCommand(manualSubCommand));
    }

    private HandlerParameter parseParameter(Parameter parameter) {
        for (val parser : config.getParameterParsers()) {
            if (parser.isSupported(parameter)) {
                HandlerParameter handlerParameter = parser.parse(parameter);

                for (val parameterPostProcessor : config.getParameterPostProcessors()) {
                    handlerParameter = parameterPostProcessor.process(parameter, handlerParameter);
                }

                return handlerParameter;
            }
        }

        throw new UnsupportedOperationException("There are no available parameters for " + parameter);
    }

    private HandlerParameters getParameters(Method method) {
        val methodParameters = method.getParameters();
        val parameters = new HandlerParameter[methodParameters.length];

        for (int i = 0, j = methodParameters.length; i < j; i++) {
            parameters[i] = parseParameter(methodParameters[i]);
        }

        return HandlerParameters.from(parameters);
    }

    private FormattingText getUsage(CommandSpec commandSpec, List<? extends Argument> arguments) {
        return config.getUsageFormatter().create(commandSpec, arguments);
    }

    private FormattingText getDescription(CommandSpec commandSpec, Method method) {
        val description = config.getAnnotationScanner().getDescription(method);

        return config.getDescriptionFormatter().create(commandSpec, description);
    }

    private static <A extends Annotation> Decorator tryCreateDecorator(
            DecoratorFactory<A> factory,
            Method method
    ) {
        val annotation = method.getDeclaredAnnotation(factory.getAnnotation());
        if (annotation != null) {
            return factory.create(annotation);
        }

        return null;
    }

    private Decorators createDecorators(Method method) {
        val result = new ArrayList<Decorator>();

        for (val decoratorFactory : config.getDecoratorFactories()) {
            val decorator = tryCreateDecorator(decoratorFactory, method);
            if (decorator != null) {
                result.add(decorator);
            }
        }

        return Decorators.from(result.toArray(new Decorator[0]));
    }

    private static <A extends Annotation> Condition tryCreateCondition(
            ConditionFactory<A> factory,
            Method method
    ) {
        val annotation = method.getDeclaredAnnotation(factory.getAnnotation());
        if (annotation != null) {
            return factory.create(annotation);
        }

        return null;
    }

    private Conditions createConditions(Method method) {
        val result = new ArrayList<Condition>();

        for (val conditionFactory : config.getConditionFactories()) {
            val condition = tryCreateCondition(conditionFactory, method);
            if (condition != null) {
                result.add(condition);
            }
        }

        return Conditions.from(result.toArray(new Condition[0]));
    }

    private HandlerSpec createHandler(CommandSpec commandSpec, HandlerPath path, Method method) {
        val parameters = getParameters(method);
        val conditions = createConditions(method);
        val decorators = createDecorators(method);

        return HandlerSpec.builder()
                .path(path)
                .command(commandSpec)
                .parameters(parameters)
                .manualEntry(new ManualEntry(
                        getUsage(commandSpec, parameters.getArguments()),
                        config.getAnnotationScanner().isHidden(method) ? null : getDescription(commandSpec, method),
                        conditions
                ))
                .method(method)
                .conditions(conditions)
                .decorators(decorators)
                .build();
    }

    private CommandSpec create(
            CommandSpec parent,
            String name,
            CommandGraph template
    ) {
        CommandSpecValidation.checkCommandName(name);

        val commandInfo = template.getInfo();
        val commandType = commandInfo.getInstanceType();

        val path = parent == null
                ? config.getHandlerPathFactory().create(name)
                : parent.getPath().resolve(name);

        val fullName = parent != null
                ? parent.getFullName() + " " + name
                : name;

        val commandSpec = CommandSpec.builder()
                .parent(parent)
                .name(name)
                .fullName(fullName)
                .path(path)
                .info(commandInfo)
                .aliases(getAliases(commandType))
                .manual(getManual(commandType))
                .build();

        val handlers = new ArrayList<HandlerSpec>();
        val subCommands = new ArrayList<CommandSpec>();

        for (val method : commandType.getMethods()) {
            val commandHandler = config.getAnnotationScanner().getCommandHandlerName(method);
            if (commandHandler != null) {
                handlers.add(createHandler(commandSpec, path.resolve(commandHandler), method));
            }

            val subCommandHandlerName = config.getAnnotationScanner().getSubCommandHandlerName(method);
            if (subCommandHandlerName != null) {
                CommandSpecValidation.checkCommandName(subCommandHandlerName);

                val subCommandPath = path.resolve(subCommandHandlerName);

                val subCommandSpec = CommandSpec.builder()
                        .parent(commandSpec)
                        .name(subCommandHandlerName)
                        .fullName(fullName + " " + subCommandHandlerName)
                        .path(subCommandPath)
                        .info(commandInfo)
                        .aliases(getAliases(method))
                        .manual(ManualSpec.empty())
                        .build();

                val subCommandHandlerSpec = createHandler(subCommandSpec, subCommandPath, method);
                subCommandSpec.setSubCommands(Collections.emptyList());
                subCommandSpec.setHandlers(Collections.singletonList(subCommandHandlerSpec));

                subCommands.add(subCommandSpec);
            }
        }

        for (val subCommand : template.getSubCommands()) {
            subCommands.add(createSubCommand(commandSpec, subCommand));
        }

        commandSpec.setHandlers(handlers);
        commandSpec.setSubCommands(subCommands);
        return commandSpec;
    }

    private CommandSpec createSubCommand(CommandSpec parent, CommandGraph template) {
        val subCommand = config.getAnnotationScanner().getSubCommandName(template.getInfo().getInstanceType());
        if (subCommand == null) {
            throw new IllegalArgumentException("Sub command must be annotated with @SubCommand");
        }
        return create(parent, subCommand, template);
    }

    private CommandSpec createCommand(CommandGraph template) {
        val command = config.getAnnotationScanner().getCommandName(template.getInfo().getInstanceType());
        if (command == null) {
            throw new IllegalArgumentException("Command must be annotated with @Command");
        }
        return create(null, command, template);
    }

    public @NotNull CommandSpec create(@NotNull CommandGraph template) {
        return createCommand(template);
    }

}
