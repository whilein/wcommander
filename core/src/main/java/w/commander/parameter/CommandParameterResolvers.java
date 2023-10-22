package w.commander.parameter;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
public interface CommandParameterResolvers {

    CommandParameter getParameter(Parameter parameter);

}
