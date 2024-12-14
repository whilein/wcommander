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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.ArgumentParserFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author _Novit_ (novitpw), whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class EnumArgumentParserFactory
        implements ArgumentParserFactory {

    CommanderConfig config;

    Map<Class<?>, ArgumentParser> enumCache = new ConcurrentHashMap<>();

    @Override
    public boolean isSupported(@NotNull Class<?> type) {
        return type.isEnum();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ArgumentParser create(Class<?> type) {
        val enumValues = Arrays.stream(type.asSubclass(Enum.class).getEnumConstants())
                .collect(Collectors.toMap(
                        e -> e.name().toLowerCase(),
                        Function.identity()
                ));

        return new EnumArgumentParser<>(config, enumValues);
    }

    @Override
    public @NotNull ArgumentParser resolve(@NotNull Class<?> type) {
        return enumCache.computeIfAbsent(type, this::create);
    }

}
