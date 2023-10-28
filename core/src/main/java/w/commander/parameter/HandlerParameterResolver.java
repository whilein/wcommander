package w.commander.parameter;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
public interface HandlerParameterResolver {

    boolean isSupported(@NotNull Parameter parameter);

    @NotNull HandlerParameter resolve(@NotNull Parameter parameter);

}
