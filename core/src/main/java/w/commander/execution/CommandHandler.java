package w.commander.execution;

import w.commander.manual.usage.CommandUsage;

/**
 * @author whilein
 */
public interface CommandHandler extends CommandExecutor {
    String path();

    int argumentCount();

    int requiredArgumentCount();

    CommandUsage usage();
}
