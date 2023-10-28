package w.commander.spec;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import w.commander.manual.description.Description;
import w.commander.manual.usage.Usage;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.HandlerParameters;

import java.lang.reflect.Method;

/**
 * @author whilein
 */
@Value.Immutable
public interface HandlerSpec {

    @NotNull String getPath();

    @NotNull HandlerParameters getParameters();

    // todo conditions

    @NotNull Description getDescription();

    @NotNull Usage getUsage();

    // todo async

    @NotNull Method getMethod();

}
