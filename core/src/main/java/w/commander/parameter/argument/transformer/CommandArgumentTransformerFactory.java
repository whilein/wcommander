package w.commander.parameter.argument.transformer;

import w.commander.parameter.argument.CommandArgument;

/**
 * @author whilein
 */
public interface CommandArgumentTransformerFactory {

    CommandArgumentTransformer create(CommandArgument argument);

}
