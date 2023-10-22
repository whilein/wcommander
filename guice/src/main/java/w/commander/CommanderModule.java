package w.commander;

import com.google.inject.AbstractModule;
import w.commander.spec.AnnotationBasedCommandSpecFactoryProvider;
import w.commander.spec.CommandSpecFactory;
import w.commander.spec.template.CommandTemplateFactory;
import w.commander.template.GuiceCommandTemplateFactory;

/**
 * @author whilein
 */
public final class CommanderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CommandSpecFactory.class).toProvider(AnnotationBasedCommandSpecFactoryProvider.class);
        bind(CommandTemplateFactory.class).to(GuiceCommandTemplateFactory.class);
    }

}
