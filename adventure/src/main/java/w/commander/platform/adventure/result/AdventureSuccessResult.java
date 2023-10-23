package w.commander.platform.adventure.result;

import w.commander.result.SuccessResult;

/**
 * @author _Novit_ (novitpw)
 */
public interface AdventureSuccessResult extends AdventureResult, SuccessResult {

    @Override
    default boolean isSuccess() {
        return true;
    }

}
