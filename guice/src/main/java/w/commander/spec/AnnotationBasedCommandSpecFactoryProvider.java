package w.commander.spec;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.manual.description.CommandDescriptionFactory;
import w.commander.manual.usage.CommandUsageFactory;
import w.commander.parameter.CommandParameterResolvers;
import w.commander.spec.path.CommandHandlerPathStrategy;

/**
 * @author whilein
 */
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class AnnotationBasedCommandSpecFactoryProvider implements Provider<CommandSpecFactory> {

    CommandHandlerPathStrategy commandHandlerPathStrategy;
    CommandParameterResolvers commandParameterResolvers;
    CommandUsageFactory commandUsageFactory;
    CommandDescriptionFactory commandDescriptionFactory;

    @Override
    public CommandSpecFactory get() {
        return AnnotationBasedCommandSpecFactory.create(
                commandHandlerPathStrategy,
                commandParameterResolvers,
                commandUsageFactory,
                commandDescriptionFactory
        );
    }

}
