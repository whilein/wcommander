package w.commander.parameter;

import w.commander.annotation.NonRequired;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
public abstract class AbstractCommandParameterResolver implements CommandParameterResolver {

    protected static boolean isRequired(Parameter parameter) {
        return !parameter.isAnnotationPresent(NonRequired.class);
    }


}
