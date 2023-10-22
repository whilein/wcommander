package w.commander;

import w.commander.execution.CommandExecutor;

/**
 * @author whilein
 */
public interface CommandNode {

    CommandNode subCommand(String name);

    CommandExecutor executor(int arguments);

}
