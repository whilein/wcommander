package w.commander.parameter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandActor;
import w.commander.annotation.Arg;
import w.commander.annotation.Join;
import w.commander.error.ErrorResultFactory;
import w.commander.parameter.argument.parser.ArgumentParserFactory;
import w.commander.parameter.argument.parser.ArgumentParserFactoryResolver;
import w.commander.parameter.argument.parser.type.NoopArgumentParserFactory;
import w.commander.parameter.argument.type.JoinArgument;
import w.commander.parameter.argument.type.OrdinaryArgument;
import w.commander.parameter.type.ActorHandlerParameter;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultHandlerParameterResolver extends AbstractHandlerParameterResolver {

    Iterable<? extends ArgumentParserFactoryResolver> argumentParserFactoryResolvers;

    public static @NotNull HandlerParameterResolver create(@NotNull ErrorResultFactory errorResultFactory) {
        return create(ArgumentParserFactoryResolver.listDefaults(errorResultFactory));
    }

    public static @NotNull HandlerParameterResolver create(
            @NotNull Iterable<? extends @NotNull ArgumentParserFactoryResolver> argumentParserFactoryResolvers
    ) {
        return new DefaultHandlerParameterResolver(argumentParserFactoryResolvers);
    }

    @Override
    public boolean isSupported(@NotNull Parameter parameter) {
        return parameter.isAnnotationPresent(Arg.class)
               || parameter.isAnnotationPresent(Join.class)
               || CommandActor.class.isAssignableFrom(parameter.getType());
    }

    private ArgumentParserFactory resolveParserFactory(Class<?> type) {
        if (type == String.class) {
            return NoopArgumentParserFactory.getInstance();
        }

        for (val resolver : argumentParserFactoryResolvers) {
            if (resolver.isSupported(type)) {
                return resolver.resolve(type);
            }
        }

        throw new UnsupportedOperationException("There are no available parser factory resolvers for " + type);

    }

    @Override
    public @NotNull HandlerParameter resolve(@NotNull Parameter parameter) {
        val type = parameter.getType();

        val join = parameter.getDeclaredAnnotation(Join.class);

        if (join != null) {
            if (type != String.class) {
                throw new IllegalArgumentException("@Join annotation is not allowed on " + type.getName());
            }

            return JoinArgument.create(
                    join.value(),
                    isRequired(parameter),
                    join.delimiter()
            );
        }

        val arg = parameter.getDeclaredAnnotation(Arg.class);

        if (arg != null) {
            return OrdinaryArgument.create(
                    arg.value(),
                    isRequired(parameter),
                    resolveParserFactory(type)
            );
        }

        if (CommandActor.class.isAssignableFrom(type)) {
            return ActorHandlerParameter.getInstance();
        }

        throw new IllegalArgumentException("Unsupported parameter: " + parameter);
    }

}