package w.commander.result;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionContext;

/**
 * @author whilein
 */
@UtilityClass
public class Results {

    public @NotNull SuccessResult ok() {
        return EmptySuccess.INSTANCE;
    }

    public @NotNull FailedResult error() {
        return EmptyFailed.INSTANCE;
    }

    public @NotNull SuccessResult ok(@NotNull String text) {
        return new TextSuccess(text);
    }

    public @NotNull FailedResult error(@NotNull String text) {
        return new TextFailed(text);
    }

    private static abstract class Empty implements Result {
        @Override
        public void dispatch(@NotNull ExecutionContext context) {
        }
    }

    private static final class EmptyFailed extends Empty implements FailedResult {

        private static final FailedResult INSTANCE
                = new EmptyFailed();

        private static final ResultException EXCEPTION
                = ResultException.create(INSTANCE);

        @Override
        public @NotNull ResultException asException() {
            return EXCEPTION;
        }

    }

    private static final class EmptySuccess extends Empty implements SuccessResult {

        private static final SuccessResult INSTANCE
                = new EmptySuccess();

        private static final ResultException EXCEPTION
                = ResultException.create(INSTANCE);

        @Override
        public @NotNull ResultException asException() {
            return EXCEPTION;
        }

    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static abstract class Text implements Result {
        String text;

        @Override
        public void dispatch(@NotNull ExecutionContext context) {
            context.dispatch(text);
        }
    }

    private static final class TextFailed extends Text implements FailedResult {
        public TextFailed(String text) {
            super(text);
        }
    }

    private static final class TextSuccess extends Text implements SuccessResult {
        public TextSuccess(String text) {
            super(text);
        }
    }

}
