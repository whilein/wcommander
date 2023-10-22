package w.commander;

import w.commander.execution.CommandExecutor;

import java.util.List;
import java.util.Map;

/**
 * @author whilein
 */
public interface CommandNodeFactory {

    CommandNode create(List<CommandExecutor> executors, Map<String, CommandNode> subCommands);

}
