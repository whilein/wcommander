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

import org.jetbrains.annotations.NotNull;
import w.commander.condition.Condition;
import w.commander.condition.ConditionFactory;
import w.commander.cooldown.CooldownManager;
import w.commander.decorator.Decorator;
import w.commander.decorator.DecoratorFactory;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContextFactory;
import w.commander.executor.HandlerPathFactory;
import w.commander.manual.DescriptionFormatter;
import w.commander.manual.ManualFactory;
import w.commander.manual.ManualFormatter;
import w.commander.manual.UsageFormatter;
import w.commander.parameter.ParameterParser;
import w.commander.parameter.ParameterPostProcessor;
import w.commander.parameter.argument.parser.ArgumentParserFactory;
import w.commander.parameter.argument.validator.ArgumentValidatorFactory;
import w.commander.tabcomplete.NamedTabCompleter;

import java.lang.annotation.Annotation;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * @author whilein
 */
public interface CommanderSetup<S extends CommanderSetup<S>> extends CommanderSetupRead {
    <A extends Annotation> @NotNull S addDecorator(@NotNull DecoratorFactory<A> decoratorFactory);

    default <A extends Annotation> @NotNull S addDecorator(@NotNull Class<A> annotation, @NotNull Decorator decorator) {
        return addDecorator(DecoratorFactory.from(annotation, decorator));
    }

    <A extends Annotation> @NotNull S addCondition(@NotNull ConditionFactory<A> conditionFactory);

    default <A extends Annotation> @NotNull S addCondition(@NotNull Class<A> annotation, @NotNull Condition condition) {
        return addCondition(ConditionFactory.from(annotation, condition));
    }

    @NotNull S annotationScanner(@NotNull AnnotationScanner annotationScanner);

    @NotNull S addTabCompleter(@NotNull NamedTabCompleter tabCompleter);

    @NotNull S addCooldownManager(@NotNull CooldownManager cooldownManager);

    @NotNull S addParentCustomizer(@NotNull Consumer<CommanderSetup<?>> customizer);

    @NotNull S addArgumentParserFactory(@NotNull ArgumentParserFactory argumentParserFactory);

    @NotNull S addArgumentValidatorFactory(@NotNull ArgumentValidatorFactory<?> argumentValidatorFactory);

    @NotNull S addParameterParser(@NotNull ParameterParser parameterParser);

    @NotNull S addParameterPostProcessor(@NotNull ParameterPostProcessor parameterPostProcessor);

    @NotNull S handlerPathFactory(@NotNull HandlerPathFactory handlerPathFactory);

    @NotNull S errorResultFactory(@NotNull ErrorResultFactory errorResultFactory);

    @NotNull S executionContextFactory(@NotNull ExecutionContextFactory executionContextFactory);

    @NotNull S manualFactory(@NotNull ManualFactory manualFactory);

    @NotNull S manualFormatter(@NotNull ManualFormatter manualFormatter);

    @NotNull S usageFormatter(@NotNull UsageFormatter usageFormatter);

    @NotNull S descriptionFormatter(@NotNull DescriptionFormatter descriptionFormatter);

    @NotNull S commandRegistrar(@NotNull CommandRegistrar commandRegistrar);

    @NotNull S includeDefaultArgumentParsers(boolean includeDefaultArgumentParsers);

    @NotNull S includeDefaultArgumentValidators(boolean includeDefaultArgumentValidators);

    @NotNull S asyncExecutor(@NotNull Executor executor);

}
