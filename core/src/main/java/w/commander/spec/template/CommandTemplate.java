package w.commander.spec.template;

import java.util.List;

/**
 * @author whilein
 */
public interface CommandTemplate {

    Object getInstance();

    List<CommandTemplate> getSubCommands();

}
