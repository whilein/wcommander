package w.commander.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.manual.usage.Usage;
import w.commander.parameter.HandlerParameters;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommandHandler extends AbstractCommandExecutor implements CommandHandler {

    @NotNull String path;

    @NotNull HandlerParameters parameters;

    @NotNull Usage usage;

}
