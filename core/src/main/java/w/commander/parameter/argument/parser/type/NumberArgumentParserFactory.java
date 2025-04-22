/*
 *    Copyright 2025 Whilein
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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.ArgumentParserFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class NumberArgumentParserFactory
        implements ArgumentParserFactory {

    Map<Class<?>, ArgumentParser> parsers;

    public NumberArgumentParserFactory(CommanderConfig config) {
        val initializer = new ParsersInitializer(config);
        initializer.add(byte.class, Byte.class, Byte::valueOf);
        initializer.add(short.class, Short.class, Short::valueOf);
        initializer.add(int.class, Integer.class, Integer::valueOf);
        initializer.add(long.class, Long.class, Long::valueOf);
        initializer.add(float.class, Float.class, Float::valueOf);
        initializer.add(double.class, Double.class, Double::valueOf);

        this.parsers = initializer.parsers;
    }

    @Override
    public boolean isSupported(@NotNull Class<?> type) {
        return parsers.containsKey(type);
    }

    @Override
    public @NotNull ArgumentParser resolve(@NotNull Class<?> type) {
        val parser = parsers.get(type);
        if (parser == null) {
            throw new IllegalArgumentException(type.getName() + " is not supported");
        }
        return parser;
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static final class ParsersInitializer {
        CommanderConfig config;

        Map<Class<?>, ArgumentParser> parsers = new HashMap<>();

        private <N extends Number> void add(
                Class<?> primitive,
                Class<? extends N> wrapper,
                Function<? super String, ? extends N> fn
        ) {
            val factory = new NumberArgumentParser(config, fn);
            parsers.put(primitive, factory);
            parsers.put(wrapper, factory);
        }

    }

}
