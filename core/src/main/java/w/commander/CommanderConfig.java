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
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.condition.Condition;
import w.commander.condition.ConditionFactory;
import w.commander.cooldown.CooldownManager;
import w.commander.decorator.Decorator;
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
import w.commander.tabcomplete.NamedTabCompleter;
import w.commander.tabcomplete.TabCompleter;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * @author whilein
 */
@Setter
@Getter
@NotThreadSafe
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CommanderConfig {

    @NonFinal
    AnnotationScanner annotationScanner;
    @NonFinal
    ErrorResultFactory errorResultFactory;
    @NonFinal
    HandlerPathFactory handlerPathFactory;
    @NonFinal
    ExecutionContextFactory executionContextFactory;
    @NonFinal
    CommandRegistrar commandRegistrar;
    @NonFinal
    UsageFormatter usageFormatter;
    @NonFinal
    DescriptionFormatter descriptionFormatter;
    @NonFinal
    ManualFactory manualFactory;
    @NonFinal
    ManualFormatter manualFormatter;
    @NonFinal
    Executor asyncExecutor;

    Map<String, TabCompleter> tabCompleters = new HashMap<>();
    Map<String, CooldownManager> cooldownManagers = new HashMap<>();
    List<ConditionFactory<?>> conditionFactories = new ArrayList<>();
    List<DecoratorFactory<?>> decoratorFactories = new ArrayList<>();
    List<ArgumentParserFactory> argumentParserFactories = new ArrayList<>();
    List<ArgumentValidatorFactory<?>> argumentValidatorFactories = new ArrayList<>();
    List<ParameterParser> parameterParsers = new ArrayList<>();
    List<ParameterPostProcessor> parameterPostProcessors = new ArrayList<>();

    public static CommanderConfig createDefaults() {
        val config = new CommanderConfig();
        config.initDefaults();

        return config;
    }

    protected void initDefaults() {
        annotationScanner = new AnnotationScanner();
        errorResultFactory = ErrorResultFactory.NOOP;
        handlerPathFactory = DefaultHandlerPathFactory.LOWER_SNAKE_CASE;
        executionContextFactory = new DefaultExecutionContextFactory();
        commandRegistrar = CommandRegistrar.NOOP;
        usageFormatter = new SimpleUsageFormatter();
        descriptionFormatter = DescriptionFormatter.RAW;
        manualFactory = new SimpleManualFactory();
        manualFormatter = new SimpleManualFormatter();
        asyncExecutor = ForkJoinPool.commonPool();

        argumentParserFactories.add(new EnumArgumentParserFactory(this));
        argumentParserFactories.add(new NumberArgumentParserFactory(this));

        argumentValidatorFactories.add(new GreaterThanArgumentValidatorFactory(this));
        argumentValidatorFactories.add(new LowerThanArgumentValidatorFactory(this));
        argumentValidatorFactories.add(new BetweenArgumentValidatorFactory(this));
        argumentValidatorFactories.add(new RegexArgumentValidatorFactory(this));

        parameterPostProcessors.add(new ArgumentPostProcessor(this));
        parameterParsers.add(new ArgumentParameterParser(this));
        decoratorFactories.add(new AsyncDecoratorFactory(this));
        decoratorFactories.add(new CooldownDecoratorFactory(this));
    }

    public <A extends Annotation> void addDecorator(@NotNull DecoratorFactory<A> decoratorFactory) {
        decoratorFactories.add(decoratorFactory);
    }

    public <A extends Annotation> void addDecorator(@NotNull Class<A> annotation, @NotNull Decorator decorator) {
        addDecorator(DecoratorFactory.from(annotation, decorator));
    }

    public <A extends Annotation> void addCondition(@NotNull ConditionFactory<A> conditionFactory) {
        conditionFactories.add(conditionFactory);
    }

    public <A extends Annotation> void addCondition(@NotNull Class<A> annotation, @NotNull Condition condition) {
        addCondition(ConditionFactory.from(annotation, condition));
    }

    public void addTabCompleter(@NotNull NamedTabCompleter tabCompleter) {
        tabCompleters.put(tabCompleter.getName(), tabCompleter);
    }

    public @Nullable TabCompleter getTabCompleter(@NotNull String name) {
        return tabCompleters.get(name);
    }

    public void addCooldownManager(@NotNull CooldownManager cooldownManager) {
        cooldownManagers.put(cooldownManager.getId(), cooldownManager);
    }

    public @Nullable CooldownManager getCooldownManager(@NotNull String id) {
        return cooldownManagers.get(id);
    }

    public void addArgumentParserFactory(@NotNull ArgumentParserFactory argumentParserFactory) {
        argumentParserFactories.add(argumentParserFactory);
    }

    public void addArgumentValidatorFactory(@NotNull ArgumentValidatorFactory<?> argumentValidatorFactory) {
        argumentValidatorFactories.add(argumentValidatorFactory);
    }

    public void addParameterParser(@NotNull ParameterParser parameterParser) {
        parameterParsers.add(parameterParser);
    }

    public void addParameterPostProcessor(@NotNull ParameterPostProcessor parameterPostProcessor) {
        parameterPostProcessors.add(parameterPostProcessor);
    }

}
