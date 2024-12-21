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

package w.commander.parameter.argument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.annotation.NonRequired;
import w.commander.annotation.TabComplete;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.ParameterPostProcessor;
import w.commander.parameter.argument.validator.ArgumentValidator;
import w.commander.parameter.argument.validator.ArgumentValidatorFactory;
import w.commander.tabcomplete.ExplicitTabCompleter;
import w.commander.tabcomplete.TabCompleter;
import w.commander.util.Immutables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class ArgumentPostProcessor implements ParameterPostProcessor {

    CommanderConfig config;

    private static <A extends Annotation> ArgumentValidator tryCreateValidator(
            ArgumentValidatorFactory<A> factory,
            Parameter parameter
    ) {
        val annotation = parameter.getDeclaredAnnotation(factory.getAnnotation());
        if (annotation != null) {
            return factory.create(annotation);
        }

        return null;
    }

    private List<ArgumentValidator> findValidators(Parameter parameter) {
        val result = new ArrayList<ArgumentValidator>();

        for (val argumentValidatorFactory : config.getArgumentValidatorFactories()) {
            val validator = tryCreateValidator(argumentValidatorFactory, parameter);
            if (validator != null) {
                val type = parameter.getType();
                if (!argumentValidatorFactory.isSupported(type)) {
                    throw new IllegalArgumentException(validator + " cannot be used for " + type.getName());
                }
                result.add(validator);
            }
        }

        return Immutables.immutableList(result);
    }

    private TabCompleter findTabCompleter(Parameter parameter) {
        val tabComplete = parameter.getDeclaredAnnotation(TabComplete.class);
        if (tabComplete == null) return null;

        val variants = tabComplete.value();
        if (variants.length != 0) {
            return new ExplicitTabCompleter(Arrays.asList(variants));
        }

        return config.getTabCompleter(tabComplete.use());
    }

    @Override
    public @NotNull HandlerParameter process(
            @NotNull Parameter parameter,
            @NotNull HandlerParameter handlerParameter
    ) {
        if (handlerParameter instanceof Argument) {
            val argument = (Argument) handlerParameter;
            val argumentInfo = argument.getInfo();

            val validators = findValidators(parameter);
            if (!validators.isEmpty()) {
                argumentInfo.setValidators(validators);
            }

            val tabCompleter = findTabCompleter(parameter);
            if (tabCompleter != null) {
                argumentInfo.setTabCompleter(tabCompleter);
            }

            argumentInfo.setRequired(!parameter.isAnnotationPresent(NonRequired.class));
        }

        return handlerParameter;
    }
}
