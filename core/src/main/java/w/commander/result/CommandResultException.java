package w.commander.result;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandResultException extends RuntimeException implements CommandResult {

    @Delegate(types = CommandResult.class)
    CommandResult delegate;

    public static CommandResultException create(final CommandResult handle) {
        return new CommandResultException(handle);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
