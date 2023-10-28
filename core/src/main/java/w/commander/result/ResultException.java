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
public final class ResultException extends RuntimeException implements Result {

    @Delegate(types = Result.class)
    Result delegate;

    public static ResultException create(final Result handle) {
        return new ResultException(handle);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
