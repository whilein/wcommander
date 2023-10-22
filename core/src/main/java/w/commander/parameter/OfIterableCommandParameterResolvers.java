package w.commander.parameter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class OfIterableCommandParameterResolvers implements CommandParameterResolvers {

    Iterable<? extends CommandParameterResolver> resolvers;

    public static CommandParameterResolvers from(CommandParameterResolver... resolvers) {
        return from(Arrays.asList(resolvers));
    }

    public static CommandParameterResolvers from(Iterable<? extends CommandParameterResolver> resolvers) {
        return new OfIterableCommandParameterResolvers(resolvers);
    }

    @Override
    public CommandParameter getParameter(Parameter parameter) {
        for (val resolver : resolvers) {
            if (resolver.isSupported(parameter)) {
                return resolver.getParameter(parameter);
            }
        }

        throw new UnsupportedOperationException("There are no available parameter resolvers for " + parameter);
    }
}
