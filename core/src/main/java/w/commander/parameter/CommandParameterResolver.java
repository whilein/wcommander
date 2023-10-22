package w.commander.parameter;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
public interface CommandParameterResolver {

    boolean isSupported(Parameter parameter);

    CommandParameter getParameter(Parameter parameter);

}
