package w.commander.spec;

import w.commander.spec.template.CommandTemplate;

/**
 * @author whilein
 */
public interface CommandSpecFactory {

    CommandSpec create(CommandTemplate template);

}
