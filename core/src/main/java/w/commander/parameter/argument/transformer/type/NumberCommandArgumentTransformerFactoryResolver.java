package w.commander.parameter.argument.transformer.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.error.CommandErrorFactory;
import w.commander.parameter.argument.transformer.CommandArgumentTransformerFactory;
import w.commander.parameter.argument.transformer.CommandArgumentTransformerFactoryResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberCommandArgumentTransformerFactoryResolver
        implements CommandArgumentTransformerFactoryResolver {

    Map<Class<?>, CommandArgumentTransformerFactory> transformers;

    public static CommandArgumentTransformerFactoryResolver create(CommandErrorFactory commandErrorFactory) {
        return new Initializer(commandErrorFactory)
                .add(byte.class, Byte.class, Byte::valueOf)
                .add(short.class, Short.class, Short::valueOf)
                .add(int.class, Integer.class, Integer::valueOf)
                .add(long.class, Long.class, Long::valueOf)
                .add(float.class, Float.class, Float::valueOf)
                .add(double.class, Double.class, Double::valueOf)
                .create();
    }

    @Override
    public boolean isSupported(Class<?> type) {
        return transformers.containsKey(type);
    }

    @Override
    public CommandArgumentTransformerFactory getTransformerFactory(Class<?> type) {
        return transformers.get(type);
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static final class Initializer {
        CommandErrorFactory commandErrorFactory;
        Map<Class<?>, CommandArgumentTransformerFactory> transformers = new HashMap<>();

        private <N extends Number> Initializer add(
                Class<?> primitive,
                Class<? extends N> wrapper,
                Function<? super String, ? extends N> fn
        ) {
            val factory = NumberCommandArgumentTransformerFactory.create(commandErrorFactory, fn);
            transformers.put(primitive, factory);
            transformers.put(wrapper, factory);
            return this;
        }

        public CommandArgumentTransformerFactoryResolver create() {
            return new NumberCommandArgumentTransformerFactoryResolver(transformers);
        }
    }

}
