package w.commander.parameter.argument.parser.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.parser.ArgumentParserFactory;
import w.commander.parameter.argument.parser.ArgumentParserFactoryResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberArgumentParserFactoryResolver
        implements ArgumentParserFactoryResolver {

    Map<Class<?>, ArgumentParserFactory> factories;

    public static @NotNull ArgumentParserFactoryResolver create(
            @NotNull ErrorResultFactory errorResultFactory
    ) {
        return new Initializer(errorResultFactory)
                .add(byte.class, Byte.class, Byte::valueOf)
                .add(short.class, Short.class, Short::valueOf)
                .add(int.class, Integer.class, Integer::valueOf)
                .add(long.class, Long.class, Long::valueOf)
                .add(float.class, Float.class, Float::valueOf)
                .add(double.class, Double.class, Double::valueOf)
                .create();
    }

    @Override
    public boolean isSupported(@NotNull Class<?> type) {
        return factories.containsKey(type);
    }

    @Override
    public @NotNull ArgumentParserFactory resolve(@NotNull Class<?> type) {
        val factory = factories.get(type);

        if (factory == null) {
            throw new IllegalArgumentException(type.getName() + " is not supported");
        }

        return factory;
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static final class Initializer {
        ErrorResultFactory errorResultFactory;
        Map<Class<?>, ArgumentParserFactory> factories = new HashMap<>();

        private <N extends Number> Initializer add(
                Class<?> primitive,
                Class<? extends N> wrapper,
                Function<? super String, ? extends N> fn
        ) {
            val factory = NumberArgumentParserFactory.create(errorResultFactory, fn);
            factories.put(primitive, factory);
            factories.put(wrapper, factory);
            return this;
        }

        public ArgumentParserFactoryResolver create() {
            return new NumberArgumentParserFactoryResolver(factories);
        }
    }

}
