package w.commander.spec;

import org.immutables.value.Value;
import w.commander.manual.description.CommandDescription;
import w.commander.manual.usage.CommandUsage;
import w.commander.parameter.CommandParameter;

import java.lang.reflect.Method;

/**
 * @author whilein
 */
@Value.Immutable
public interface HandlerSpec {

    String getPath();

    CommandParameter[] getParameters();

    // todo conditions

    CommandDescription getDescription();

    CommandUsage getUsage();

    // todo async

    Method getMethod();

}
