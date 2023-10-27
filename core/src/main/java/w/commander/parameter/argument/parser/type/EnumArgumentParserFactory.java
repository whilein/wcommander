package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.parser.ArgumentParser;
import w.commander.parameter.argument.parser.ArgumentParserFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumArgumentParserFactory<E extends Enum<E>> implements ArgumentParserFactory {

    ErrorResultFactory errorResultFactory;
    Map<String, E> enumValues;

    public static <E extends Enum<E>> @NotNull ArgumentParserFactory create(
            @NotNull ErrorResultFactory errorResultFactory,
            @NotNull Class<E> enumType
    ) {
        val enumValues = Arrays.stream(enumType.getEnumConstants())
                .collect(Collectors.toMap(
                        e -> e.name().toLowerCase(),
                        Function.identity()
                ));

        return new EnumArgumentParserFactory<>(errorResultFactory, enumValues);
    }

    @Override
    public ArgumentParser create(Argument argument) {
        return EnumArgumentParser.create(argument, errorResultFactory, enumValues);
    }
}