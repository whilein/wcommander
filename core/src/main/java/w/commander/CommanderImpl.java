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
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.condition.ConditionFactory;
import w.commander.cooldown.CooldownManager;
import w.commander.decorator.DecoratorFactory;
import w.commander.decorator.type.AsyncDecoratorFactory;
import w.commander.decorator.type.CooldownDecoratorFactory;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.DefaultExecutionContextFactory;
import w.commander.execution.ExecutionContextFactory;
import w.commander.executor.DefaultHandlerPathFactory;
import w.commander.executor.HandlerPathFactory;
import w.commander.manual.DescriptionFormatter;
import w.commander.manual.ManualFactory;
import w.commander.manual.ManualFormatter;
import w.commander.manual.SimpleManualFactory;
import w.commander.manual.SimpleManualFormatter;
import w.commander.manual.SimpleUsageFormatter;
import w.commander.manual.UsageFormatter;
import w.commander.parameter.ArgumentParameterParser;
import w.commander.parameter.ParameterParser;
import w.commander.parameter.ParameterPostProcessor;
import w.commander.parameter.argument.ArgumentPostProcessor;
import w.commander.parameter.argument.parser.ArgumentParserFactory;
import w.commander.parameter.argument.parser.type.EnumArgumentParserFactory;
import w.commander.parameter.argument.parser.type.NumberArgumentParserFactory;
import w.commander.parameter.argument.validator.ArgumentValidatorFactory;
import w.commander.parameter.argument.validator.type.BetweenArgumentValidatorFactory;
import w.commander.parameter.argument.validator.type.GreaterThanArgumentValidatorFactory;
import w.commander.parameter.argument.validator.type.LowerThanArgumentValidatorFactory;
import w.commander.parameter.argument.validator.type.RegexArgumentValidatorFactory;
import w.commander.spec.CommandSpecFactory;
import w.commander.tabcomplete.NamedTabCompleter;
import w.commander.tabcomplete.TabCompleter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class CommanderImpl implements Commander {

    Map<Class<?>, Command> type2CommandMap = new ConcurrentHashMap<>();

    @Getter
    Collection<? extends Command> commands = Collections.unmodifiableCollection(type2CommandMap.values());

    CommandRegistrar commandRegistrar;
    CommandSpecFactory commandSpecFactory;
    CommandFactory commandFactory;

    @Override
    public @NotNull Command ofGraph(@NotNull CommandGraph graph) {
        return commandFactory.create(commandSpecFactory.create(graph));
    }

    @Override
    public void register(@NotNull Command command) {
        val commandType = command.getInstanceType();

        val prevCommand = type2CommandMap.put(commandType, command);
        if (prevCommand != null) {
            commandRegistrar.unregister(prevCommand);
        }
        commandRegistrar.register(command);
    }

    @Override
    public void unregister(@NotNull Class<?> type) {
        val command = type2CommandMap.remove(type);
        if (command == null) return;

        commandRegistrar.unregister(command);
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class Builder implements CommanderBuilder<Builder>, Cloneable {

        AnnotationScanner annotationScanner;

        ErrorResultFactory errorResultFactory;

        HandlerPathFactory handlerPathFactory;

        ExecutionContextFactory executionContextFactory;

        CommandRegistrar commandRegistrar;

        UsageFormatter usageFormatter;

        DescriptionFormatter descriptionFormatter;

        ManualFactory manualFactory;

        ManualFormatter manualFormatter;

        boolean includeDefaultArgumentParsers;

        boolean includeDefaultArgumentValidators;

        Executor asyncExecutor;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        Map<String, TabCompleter> tabCompleters;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        Map<String, CooldownManager> cooldownManagers;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        List<ConditionFactory<?>> conditions;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        List<DecoratorFactory<?>> decorators;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        List<ArgumentParserFactory> argumentParserFactories;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        List<ArgumentValidatorFactory<?>> argumentValidatorFactories;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        List<ParameterParser> parameterParsers;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        List<ParameterPostProcessor> parameterPostProcessors;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        List<Consumer<CommanderSetup<?>>> customizers;

        public Builder() {
            annotationScanner = new AnnotationScanner();
            errorResultFactory = ErrorResultFactory.NOOP;
            handlerPathFactory = DefaultHandlerPathFactory.LOWER_SNAKE_CASE;
            executionContextFactory = new DefaultExecutionContextFactory();
            commandRegistrar = CommandRegistrar.NOOP;
            usageFormatter = new SimpleUsageFormatter();
            descriptionFormatter = DescriptionFormatter.RAW;
            manualFactory = new SimpleManualFactory();
            manualFormatter = new SimpleManualFormatter();
            includeDefaultArgumentParsers = true;
            includeDefaultArgumentValidators = true;
            asyncExecutor = ForkJoinPool.commonPool();
            tabCompleters = new HashMap<>();
            cooldownManagers = new HashMap<>();
            conditions = new ArrayList<>();
            decorators = new ArrayList<>();
            argumentParserFactories = new ArrayList<>();
            argumentValidatorFactories = new ArrayList<>();
            parameterParsers = new ArrayList<>();
            parameterPostProcessors = new ArrayList<>();
            customizers = new ArrayList<>();
        }

        @Override
        protected Builder clone() {
            try {
                val clone = (Builder) super.clone();
                clone.tabCompleters = new HashMap<>(clone.tabCompleters);
                clone.cooldownManagers = new HashMap<>(clone.cooldownManagers);
                clone.conditions = new ArrayList<>(clone.conditions);
                clone.decorators = new ArrayList<>(clone.decorators);
                clone.argumentParserFactories = new ArrayList<>(clone.argumentParserFactories);
                clone.argumentValidatorFactories = new ArrayList<>(clone.argumentValidatorFactories);
                clone.parameterParsers = new ArrayList<>(clone.parameterParsers);
                clone.parameterPostProcessors = new ArrayList<>(clone.parameterPostProcessors);
                clone.customizers = new ArrayList<>(clone.customizers);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public @NotNull <A extends Annotation> Builder addCondition(
                @NotNull ConditionFactory<A> conditionFactory
        ) {
            this.conditions.add(conditionFactory);
            return this;
        }

        @Override
        public @NotNull Builder addTabCompleter(@NotNull NamedTabCompleter tabCompleter) {
            this.tabCompleters.put(tabCompleter.getName(), tabCompleter);
            return this;
        }

        @Override
        public @NotNull Builder addCooldownManager(@NotNull CooldownManager cooldownManager) {
            this.cooldownManagers.put(cooldownManager.getId(), cooldownManager);
            return this;
        }

        @Override
        public @NotNull <A extends Annotation> Builder addDecorator(@NotNull DecoratorFactory<A> decoratorFactory) {
            this.decorators.add(decoratorFactory);
            return this;
        }

        @Override
        public @NotNull Builder addParentCustomizer(@NotNull Consumer<CommanderSetup<?>> customizer) {
            customizers.add(customizer);
            return this;
        }

        @Override
        public @NotNull Builder addArgumentParserFactory(
                @NotNull ArgumentParserFactory argumentParserFactory
        ) {
            argumentParserFactories.add(argumentParserFactory);
            return this;
        }

        @Override
        public @NotNull Builder addArgumentValidatorFactory(
                @NotNull ArgumentValidatorFactory<?> argumentValidatorFactory
        ) {
            argumentValidatorFactories.add(argumentValidatorFactory);
            return this;
        }

        @Override
        public @NotNull Builder addParameterParser(
                @NotNull ParameterParser parameterParser
        ) {
            parameterParsers.add(parameterParser);
            return this;
        }

        @Override
        public @NotNull Builder addParameterPostProcessor(@NotNull ParameterPostProcessor parameterPostProcessor) {
            parameterPostProcessors.add(parameterPostProcessor);
            return this;
        }

        private Commander build0() {
            val errorResultFactory = this.errorResultFactory;

            val argumentParserFactories = new ArrayList<>(this.argumentParserFactories);
            if (includeDefaultArgumentParsers) {
                argumentParserFactories.add(new EnumArgumentParserFactory(errorResultFactory));
                argumentParserFactories.add(new NumberArgumentParserFactory(errorResultFactory));
            }
            val argumentValidatorFactories = new ArrayList<>(this.argumentValidatorFactories);
            if (includeDefaultArgumentValidators) {
                argumentValidatorFactories.add(new GreaterThanArgumentValidatorFactory(errorResultFactory));
                argumentValidatorFactories.add(new LowerThanArgumentValidatorFactory(errorResultFactory));
                argumentValidatorFactories.add(new BetweenArgumentValidatorFactory(errorResultFactory));
                argumentValidatorFactories.add(new RegexArgumentValidatorFactory(errorResultFactory));
            }

            val tabCompleters = new HashMap<>(this.tabCompleters);

            val parameterPostProcessors = new ArrayList<ParameterPostProcessor>(1 + this.parameterPostProcessors.size());
            parameterPostProcessors.add(new ArgumentPostProcessor(tabCompleters, argumentValidatorFactories));
            parameterPostProcessors.addAll(this.parameterPostProcessors);

            val parameterParsers = new ArrayList<>(this.parameterParsers);
            parameterParsers.add(new ArgumentParameterParser(argumentParserFactories));

            val conditions = new ArrayList<>(this.conditions);

            val decorators = new ArrayList<>(this.decorators);
            decorators.add(new AsyncDecoratorFactory(asyncExecutor));

            if (!cooldownManagers.isEmpty()) {
                val cooldownManagers = new HashMap<>(this.cooldownManagers);

                decorators.add(new CooldownDecoratorFactory(asyncExecutor, cooldownManagers, errorResultFactory));
            }

            return new CommanderImpl(
                    commandRegistrar,
                    new CommandSpecFactory(
                            annotationScanner,
                            handlerPathFactory,
                            usageFormatter,
                            descriptionFormatter,
                            parameterParsers,
                            parameterPostProcessors,
                            conditions,
                            decorators
                    ),
                    new CommandFactory(
                            executionContextFactory,
                            manualFactory,
                            manualFormatter,
                            errorResultFactory
                    )
            );
        }

        @Override
        public @NotNull Commander build() {
            val clone = clone();

            for (val customizer : customizers) {
                customizer.accept(clone);
            }

            return clone.build0();
        }

    }

}
