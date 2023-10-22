package w.commander.result;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import w.commander.execution.CommandExecutionContext;

/**
 * @author whilein
 */
@UtilityClass
public class CommandResults {

    public SuccessCommandResult ok() {
        return EmptySuccess.INSTANCE;
    }

    public FailedCommandResult error() {
        return EmptyFailed.INSTANCE;
    }

    public SuccessCommandResult ok(String text) {
        return new TextSuccess(text);
    }

    public FailedCommandResult error(String text) {
        return new TextFailed(text);
    }

    private static abstract class Empty implements CommandResult {
        @Override
        public void answer(CommandExecutionContext context) {
        }
    }

    private static final class EmptyFailed extends Empty implements FailedCommandResult {

        private static final FailedCommandResult INSTANCE
                = new EmptyFailed();

        private static final CommandResultException EXCEPTION
                = CommandResultException.create(INSTANCE);

        @Override
        public CommandResultException asException() {
            return EXCEPTION;
        }

    }

    private static final class EmptySuccess extends Empty implements SuccessCommandResult {

        private static final SuccessCommandResult INSTANCE
                = new EmptySuccess();

        private static final CommandResultException EXCEPTION
                = CommandResultException.create(INSTANCE);

        @Override
        public CommandResultException asException() {
            return EXCEPTION;
        }

    }

    @FieldDefaults(makeFinal = true)
    @RequiredArgsConstructor
    private static abstract class Text implements CommandResult {
        String text;

        @Override
        public void answer(CommandExecutionContext context) {
            context.answer(text);
        }
    }

    private static final class TextFailed extends Text implements FailedCommandResult {
        public TextFailed(String text) {
            super(text);
        }
    }

    private static final class TextSuccess extends Text implements SuccessCommandResult {
        public TextSuccess(String text) {
            super(text);
        }
    }

}
