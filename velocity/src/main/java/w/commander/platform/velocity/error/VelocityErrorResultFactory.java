package w.commander.platform.velocity.error;

import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.result.FailedResult;
import w.commander.result.Results;

/**
 * @author _Novit_ (novitpw)
 */
public interface VelocityErrorResultFactory extends ErrorResultFactory {

    default @NotNull FailedResult onOfflineUser(@NotNull String value) {
        return Results.error();
    }

}
