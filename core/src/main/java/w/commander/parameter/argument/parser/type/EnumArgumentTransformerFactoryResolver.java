package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.parser.ArgumentParserFactory;
import w.commander.parameter.argument.parser.ArgumentParserFactoryResolver;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumArgumentTransformerFactoryResolver
        implements ArgumentParserFactoryResolver {

    ErrorResultFactory errorResultFactory;

    public static @NotNull ArgumentParserFactoryResolver create(
            @NotNull ErrorResultFactory errorResultFactory
    ) {
        return new EnumArgumentTransformerFactoryResolver(errorResultFactory);
    }

    @Override
    public boolean isSupported(@NotNull Class<?> type) {
        return type.isEnum();
    }

    @Override
    public @NotNull ArgumentParserFactory resolve(@NotNull Class<?> type) {
        return EnumArgumentParserFactory.create(errorResultFactory, type.asSubclass(Enum.class));
    }

}
