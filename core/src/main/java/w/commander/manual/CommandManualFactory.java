package w.commander.manual;

import w.commander.spec.CommandSpec;

/**
 * @author whilein
 */
public interface CommandManualFactory {

    CommandManual create(CommandSpec spec);

}
