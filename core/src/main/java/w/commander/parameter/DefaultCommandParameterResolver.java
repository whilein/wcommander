package w.commander.parameter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import w.commander.CommandSender;
import w.commander.annotation.Arg;
import w.commander.annotation.Join;
import w.commander.parameter.argument.transformer.CommandArgumentTransformerFactory;
import w.commander.parameter.argument.transformer.CommandArgumentTransformerFactoryResolver;
import w.commander.parameter.argument.transformer.type.NoopCommandArgumentTransformerFactory;
import w.commander.parameter.argument.type.JoinCommandArgument;
import w.commander.parameter.argument.type.OrdinaryCommandArgument;
import w.commander.parameter.type.SenderCommandParameter;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultCommandParameterResolver extends AbstractCommandParameterResolver {

    Iterable<? extends CommandArgumentTransformerFactoryResolver> commandArgumentTransformerFactoryResolvers;

    public static CommandParameterResolver create(
            Iterable<? extends CommandArgumentTransformerFactoryResolver> resolvers
    ) {
        return new DefaultCommandParameterResolver(resolvers);
    }

    public static CommandParameterResolver create(
            CommandArgumentTransformerFactoryResolver... commandArgumentTransformerFactoryResolvers
    ) {
        return create(Arrays.asList(commandArgumentTransformerFactoryResolvers));
    }

    @Override
    public boolean isSupported(Parameter parameter) {
        return parameter.isAnnotationPresent(Arg.class)
               || parameter.isAnnotationPresent(Join.class)
               || CommandSender.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public CommandParameter getParameter(Parameter parameter) {
        val type = parameter.getType();

        val join = parameter.getDeclaredAnnotation(Join.class);

        if (join != null) {
            if (type != String.class) {
                throw new IllegalArgumentException("@Join annotation is not allowed on " + type.getName());
            }

            return JoinCommandArgument.create(
                    join.value(),
                    isRequired(parameter),
                    join.delimiter()
            );
        }

        val arg = parameter.getDeclaredAnnotation(Arg.class);

        if (arg != null) {
            CommandArgumentTransformerFactory factory = null;

            if (type == String.class) {
                factory = NoopCommandArgumentTransformerFactory.getInstance();
            } else {
                for (val resolver : commandArgumentTransformerFactoryResolvers) {
                    if (resolver.isSupported(type)) {
                        factory = resolver.getTransformerFactory(type);
                        break;
                    }
                }

                if (factory == null) {
                    throw new IllegalArgumentException("There are no available argument transformers for "
                                                       + type.getName());
                }
            }

            return OrdinaryCommandArgument.create(
                    arg.value(),
                    isRequired(parameter),
                    factory
            );
        }

        if (CommandSender.class.isAssignableFrom(type)) {
            return SenderCommandParameter.getInstance();
        }

        throw new IllegalArgumentException("Unsupported parameter: " + parameter);
    }

}
