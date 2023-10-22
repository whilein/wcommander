package w.commander.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import w.commander.CommandSender;
import w.commander.RawCommandArguments;

/**
 * @author whilein
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommandExecutionContext implements CommandExecutionContext {

    CommandSender sender;
    CommandExecutor executor;
    RawCommandArguments rawArguments;

    @Override
    public void answer(String text) {
        sender.sendMessage(text);
    }
}
