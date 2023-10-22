package w.commander.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import w.commander.manual.usage.CommandUsage;

/**
 * @author whilein
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommandHandler extends AbstractCommandExecutor implements CommandHandler {

    String path;

    int argumentCount;
    int requiredArgumentCount;

    CommandUsage usage;

}
