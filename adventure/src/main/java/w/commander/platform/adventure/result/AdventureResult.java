package w.commander.platform.adventure.result;

import w.commander.execution.ExecutionContext;
import w.commander.platform.adventure.execution.AdventureExecutionContext;
import w.commander.result.Result;

/**
 * @author _Novit_ (novitpw)
 */
public interface AdventureResult extends Result {

    @Override
    default void dispatch(ExecutionContext context) {
        dispatch((AdventureExecutionContext) context);
    }

    void dispatch(AdventureExecutionContext context);

}
