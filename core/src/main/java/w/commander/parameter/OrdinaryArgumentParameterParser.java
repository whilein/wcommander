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
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.annotation.Arg;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.type.NoopArgumentParser;
import w.commander.parameter.argument.type.OrdinaryArgument;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdinaryArgumentParameterParser extends AnnotatedParameterParser<Arg> {

    CommanderConfig config;

    public OrdinaryArgumentParameterParser(CommanderConfig config) {
        super(Arg.class);

        this.config = config;
    }

    private ArgumentParser findParser(Class<?> type) {
        if (type == String.class) {
            return NoopArgumentParser.getInstance();
        }

        for (val resolver : config.getArgumentParserFactories()) {
            if (resolver.isSupported(type)) {
                return resolver.resolve(type);
            }
        }

        throw new UnsupportedOperationException("There are no available argument parsers for " + type);
    }

    @Override
    protected @NotNull HandlerParameter parse(@NotNull Parameter parameter, @NotNull Arg annotation) {
        val parser = findParser(parameter.getType());
        val argument = new OrdinaryArgument(
                annotation.value(),
                parser
        );
        argument.getInfo().setTabCompleter(parser.getDefaultTabCompleter());
        return argument;
    }

}
