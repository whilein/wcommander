package w.commander.parameter.argument.transformer.type;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import w.commander.parameter.argument.CommandArgument;
import w.commander.parameter.argument.transformer.CommandArgumentTransformer;
import w.commander.parameter.argument.transformer.CommandArgumentTransformerFactory;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoopCommandArgumentTransformerFactory implements CommandArgumentTransformerFactory {

    private static final CommandArgumentTransformerFactory INSTANCE = new NoopCommandArgumentTransformerFactory();

    public static CommandArgumentTransformerFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public CommandArgumentTransformer create(CommandArgument argument) {
        return NoopCommandArgumentTransformer.getInstance();
    }
}
