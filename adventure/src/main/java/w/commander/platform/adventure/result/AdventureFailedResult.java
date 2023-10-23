package w.commander.platform.adventure.result;

import w.commander.result.FailedResult;

/**
 * @author _Novit_ (novitpw)
 */
public interface AdventureFailedResult extends AdventureResult, FailedResult {

    @Override
    default boolean isSuccess() {
        return false;
    }

}
