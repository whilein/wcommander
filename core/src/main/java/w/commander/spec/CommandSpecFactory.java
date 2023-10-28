package w.commander.spec;

import w.commander.spec.template.CommandTemplate;
import w.commander.spec.template.SimpleCommandTemplate;

/**
 * @author whilein
 */
public interface CommandSpecFactory {

    CommandSpec create(CommandTemplate template);

    default CommandSpec create(Object instance) {
        return create(SimpleCommandTemplate.create(instance));
    }

}
