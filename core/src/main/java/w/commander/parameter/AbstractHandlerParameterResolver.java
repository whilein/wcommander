package w.commander.parameter;

import org.jetbrains.annotations.NotNull;
import w.commander.annotation.NonRequired;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
public abstract class AbstractHandlerParameterResolver implements HandlerParameterResolver {

    protected static boolean isRequired(@NotNull Parameter parameter) {
        return !parameter.isAnnotationPresent(NonRequired.class);
    }


}
