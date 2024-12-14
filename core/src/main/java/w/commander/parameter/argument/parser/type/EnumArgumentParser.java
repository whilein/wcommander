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

package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.CommanderConfig;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.tabcomplete.ExplicitTabCompleter;
import w.commander.tabcomplete.TabCompleter;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author _Novit_ (novitpw), whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class EnumArgumentParser<E extends Enum<E>> implements ArgumentParser {

    CommanderConfig config;
    Map<String, E> enumValues;

    @Getter
    TabCompleter defaultTabCompleter;

    public EnumArgumentParser(CommanderConfig config, Map<String, E> enumValues) {
        this.config = config;
        this.enumValues = enumValues;

        this.defaultTabCompleter = new ExplicitTabCompleter(new ArrayList<>(enumValues.keySet()));
    }

    @Override
    public @Nullable Object parse(
            @NotNull String value,
            @NotNull Argument argument,
            @NotNull ExecutionContext context
    ) {
        E enumValue;
        if ((enumValue = enumValues.get(value.toLowerCase())) == null) {
            return config.getErrorResultFactory().onInvalidEnum(argument, value, enumValues);
        }

        return enumValue;
    }
}
