package w.commander.spec;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.manual.description.DescriptionFactory;
import w.commander.manual.usage.UsageFactory;
import w.commander.parameter.HandlerParameterResolver;
import w.commander.spec.path.HandlerPathStrategy;

import java.util.Set;

/**
 * @author whilein
 */
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class AnnotationBasedCommandSpecFactoryProvider implements Provider<CommandSpecFactory> {

    HandlerPathStrategy handlerPathStrategy;
    Set<? extends HandlerParameterResolver> handlerResolverSet;
    UsageFactory usageFactory;
    DescriptionFactory descriptionFactory;

    @Override
    public CommandSpecFactory get() {
        return AnnotationBasedCommandSpecFactory.create(
                handlerPathStrategy,
                handlerResolverSet,
                usageFactory,
                descriptionFactory
        );
    }

}
