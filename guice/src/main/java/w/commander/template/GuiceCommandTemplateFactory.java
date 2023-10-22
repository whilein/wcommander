package w.commander.template;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.spec.template.AbstractCommandTemplateFactory;

/**
 * @author whilein
 */
@Singleton
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class GuiceCommandTemplateFactory extends AbstractCommandTemplateFactory {

    Injector injector;

    @Override
    protected Object getInstance(Class<?> type) {
        return injector.getInstance(type);
    }

}
