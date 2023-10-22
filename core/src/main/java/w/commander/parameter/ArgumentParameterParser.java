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

package w.commander.parameter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.annotation.Arg;
import w.commander.annotation.Join;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.ArgumentParserFactory;
import w.commander.parameter.argument.parser.type.NoopArgumentParser;
import w.commander.parameter.argument.type.JoinArgument;
import w.commander.parameter.argument.type.OrdinaryArgument;
import w.commander.parameter.type.ActorHandlerParameter;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ArgumentParameterParser extends AbstractParameterParser {

    Iterable<? extends ArgumentParserFactory> argumentParserFactoryResolvers;

    @Override
    public boolean isSupported(@NotNull Parameter parameter) {
        return parameter.isAnnotationPresent(Arg.class)
               || parameter.isAnnotationPresent(Join.class)
               || CommandActor.class.isAssignableFrom(parameter.getType());
    }

    private ArgumentParser findParser(Class<?> type) {
        if (type == String.class) {
            return NoopArgumentParser.getInstance();
        }

        for (val resolver : argumentParserFactoryResolvers) {
            if (resolver.isSupported(type)) {
                return resolver.resolve(type);
            }
        }

        throw new UnsupportedOperationException("There are no available parsers for " + type);
    }

    @Override
    public @NotNull HandlerParameter parse(@NotNull Parameter parameter) {
        val type = parameter.getType();
        val join = parameter.getDeclaredAnnotation(Join.class);
        if (join != null) {
            if (type != String.class) {
                throw new IllegalArgumentException("@Join annotation is not allowed on " + type.getName());
            }

            return new JoinArgument(
                    join.value(),
                    isRequired(parameter),
                    join.delimiter()
            );
        }

        val arg = parameter.getDeclaredAnnotation(Arg.class);
        if (arg != null) {
            val parser = findParser(type);
            val argument = new OrdinaryArgument(
                    arg.value(),
                    isRequired(parameter),
                    parser
            );
            argument.setTabCompleter(parser.getDefaultTabCompleter());
            return argument;
        }

        if (CommandActor.class.isAssignableFrom(type)) {
            return ActorHandlerParameter.getInstance();
        }

        throw new IllegalArgumentException("Unsupported parameter: " + parameter);
    }

}
