package w.commander.execution;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import w.commander.manual.CommandManual;
import w.commander.result.CommandResult;

import java.util.function.Consumer;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ManualCommandExecutor extends AbstractCommandExecutor {

    CommandManual commandManual;

    public static CommandExecutor create(CommandManual commandManual) {
        return new ManualCommandExecutor(commandManual);
    }

    @Override
    protected void doExecute(CommandExecutionContext context, Consumer<CommandResult> callback) {
        callback.accept(commandManual.format(context));
    }

    @Override
    public boolean isOverrideable() {
        return true;
    }

}
