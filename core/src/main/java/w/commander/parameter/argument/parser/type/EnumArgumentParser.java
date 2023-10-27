package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.argument.Argument;
import w.commander.parameter.argument.parser.ArgumentParser;

import java.util.Map;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumArgumentParser<E extends Enum<E>> implements ArgumentParser {

    Argument argument;
    ErrorResultFactory errorResultFactory;
    Map<String, E> enumValues;

    public static <E extends Enum<E>> @NotNull ArgumentParser create(
            @NotNull Argument argument,
            @NotNull ErrorResultFactory errorResultFactory,
            @NotNull Map<String, E> enumValues
    ) {
        return new EnumArgumentParser<>(argument, errorResultFactory, enumValues);
    }

    @Override
    public @Nullable Object parse(@NotNull String value, @NotNull ExecutionContext context) {
        val enumValue = enumValues.get(value.toLowerCase());

        if (enumValue == null) {
            return errorResultFactory.onInvalidEnum(argument, value, enumValues);
        }

        return enumValues.get(value.toLowerCase());
    }
}
