package w.commander.execution;

import w.commander.CommandSender;
import w.commander.RawCommandArguments;

/**
 * @author whilein
 */
public class TestCommandExecutionContext extends AbstractCommandExecutionContext {

    public TestCommandExecutionContext(
            CommandSender sender,
            CommandExecutor executor,
            RawCommandArguments rawArguments
    ) {
        super(sender, executor, rawArguments);
    }

}
