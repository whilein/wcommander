package w.commander.platform.adventure.result;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import w.commander.platform.adventure.execution.AdventureExecutionContext;

/**
 * @author _Novit_ (novitpw)
 */
@UtilityClass
public class AdventureResults {

    public @NotNull AdventureSuccessResult ok(Component message) {
        return new ComponentResultSuccess(message);
    }

    public  @NotNull AdventureFailedResult error(@NotNull Component message) {
        return new ComponentResultFailed(message);
    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static abstract class ComponentResult implements AdventureResult {

        Component message;

        @Override
        public void dispatch(AdventureExecutionContext context) {
            context.dispatch(message);
        }

    }

    private static class ComponentResultSuccess extends ComponentResult implements AdventureSuccessResult {
        ComponentResultSuccess(Component message) {
            super(message);
        }
    }

    private static class ComponentResultFailed extends ComponentResult implements AdventureFailedResult {
        ComponentResultFailed(Component message) {
            super(message);
        }
    }

}
