package w.commander.error;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.execution.ExecutionThrowableInterceptor;
import w.commander.result.Result;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultExecutionThrowableInterceptor implements ExecutionThrowableInterceptor {

    ErrorResultFactory errorResultFactory;

    public static @NotNull ExecutionThrowableInterceptor create(
            @NotNull ErrorResultFactory errorResultFactory
    ) {
        return new DefaultExecutionThrowableInterceptor(errorResultFactory);
    }

    @Override
    public @NotNull Result intercept(@NotNull Throwable throwable) {
        return errorResultFactory.onInternalError(throwable);
    }
}
