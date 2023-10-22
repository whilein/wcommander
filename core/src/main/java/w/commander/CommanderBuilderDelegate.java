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
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.condition.ConditionFactory;
import w.commander.cooldown.CooldownManager;
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
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public abstract class CommanderBuilderDelegate<S extends CommanderBuilder<S>>
        implements CommanderBuilder<S> {

    @Delegate(types = CommanderSetupRead.class)
    CommanderBuilder<?> delegate;

    @SuppressWarnings("unchecked")
    private S self() {
        return (S) this;
    }

    @Override
    @NotNull
    public S annotationScanner(@NotNull AnnotationScanner annotationScanner) {
        delegate.annotationScanner(annotationScanner);
        return self();
    }

    @Override
    public @NotNull S addTabCompleter(@NotNull NamedTabCompleter tabCompleter) {
        delegate.addTabCompleter(tabCompleter);
        return self();
    }

    @Override
    public @NotNull S addCooldownManager(@NotNull CooldownManager cooldownManager) {
        delegate.addCooldownManager(cooldownManager);
        return self();
    }

    @Override
    public @NotNull S addParentCustomizer(@NotNull Consumer<CommanderSetup<?>> customizer) {
        delegate.addParentCustomizer(customizer);
        return self();
    }

    @Override
    public <A extends Annotation> @NotNull S addDecorator(@NotNull DecoratorFactory<A> decoratorFactory) {
        delegate.addDecorator(decoratorFactory);
        return self();
    }

    @Override
    public <A extends Annotation> @NotNull S addCondition(@NotNull ConditionFactory<A> conditionFactory) {
        delegate.addCondition(conditionFactory);
        return self();
    }

    @Override
    public @NotNull S addArgumentValidatorFactory(@NotNull ArgumentValidatorFactory<?> argumentValidatorFactory) {
        delegate.addArgumentValidatorFactory(argumentValidatorFactory);
        return self();
    }

    @Override
    public @NotNull S includeDefaultArgumentValidators(boolean includeDefaultArgumentValidators) {
        delegate.includeDefaultArgumentValidators(includeDefaultArgumentValidators);
        return self();
    }

    @Override
    public @NotNull S addArgumentParserFactory(@NotNull ArgumentParserFactory argumentParserFactory) {
        delegate.addArgumentParserFactory(argumentParserFactory);
        return self();
    }

    @Override
    public @NotNull S addParameterParser(@NotNull ParameterParser parameterParser) {
        delegate.addParameterParser(parameterParser);
        return self();
    }

    @Override
    public @NotNull S addParameterPostProcessor(@NotNull ParameterPostProcessor parameterPostProcessor) {
        delegate.addParameterPostProcessor(parameterPostProcessor);
        return self();
    }

    @Override
    public @NotNull S handlerPathFactory(@NotNull HandlerPathFactory handlerPathFactory) {
        delegate.handlerPathFactory(handlerPathFactory);
        return self();
    }

    @Override
    public @NotNull S errorResultFactory(@NotNull ErrorResultFactory errorResultFactory) {
        delegate.errorResultFactory(errorResultFactory);
        return self();
    }

    @Override
    public @NotNull S executionContextFactory(@NotNull ExecutionContextFactory executionContextFactory) {
        delegate.executionContextFactory(executionContextFactory);
        return self();
    }

    @Override
    public @NotNull S manualFactory(@NotNull ManualFactory manualFactory) {
        delegate.manualFactory(manualFactory);
        return self();
    }

    @Override
    public @NotNull S manualFormatter(@NotNull ManualFormatter manualFormatter) {
        delegate.manualFormatter(manualFormatter);
        return self();
    }

    @Override
    public @NotNull S usageFormatter(@NotNull UsageFormatter usageFormatter) {
        delegate.usageFormatter(usageFormatter);
        return self();
    }

    @Override
    public @NotNull S descriptionFormatter(@NotNull DescriptionFormatter descriptionFormatter) {
        delegate.descriptionFormatter(descriptionFormatter);
        return self();
    }

    @Override
    public @NotNull S commandRegistrar(@NotNull CommandRegistrar commandRegistrar) {
        delegate.commandRegistrar(commandRegistrar);
        return self();
    }

    @Override
    public @NotNull S includeDefaultArgumentParsers(boolean includeDefaultArgumentParsers) {
        delegate.includeDefaultArgumentParsers(includeDefaultArgumentParsers);
        return self();
    }

    @Override
    public @NotNull S asyncExecutor(@NotNull Executor executor) {
        delegate.asyncExecutor(executor);
        return self();
    }

    @Override
    public @NotNull Commander build() {
        return delegate.build();
    }

}
