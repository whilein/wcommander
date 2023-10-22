package w.commander;

import w.commander.spec.CommandSpec;

/**
 * @author whilein
 */
public interface CommandFactory {

    Command create(CommandSpec spec);

}
