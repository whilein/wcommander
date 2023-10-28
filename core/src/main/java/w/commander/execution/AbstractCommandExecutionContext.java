package w.commander.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.CommandSender;
import w.commander.RawCommandArguments;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommandExecutionContext implements CommandExecutionContext {

    @NotNull CommandSender sender;
    @NotNull CommandExecutor executor;
    @NotNull RawCommandArguments rawArguments;

    @Override
    public void answer(String text) {
        sender.sendMessage(text);
    }
}
