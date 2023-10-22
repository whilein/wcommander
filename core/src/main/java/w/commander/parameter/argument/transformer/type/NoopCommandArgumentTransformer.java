package w.commander.parameter.argument.transformer.type;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import w.commander.execution.CommandExecutionContext;
import w.commander.parameter.argument.transformer.CommandArgumentTransformer;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoopCommandArgumentTransformer implements CommandArgumentTransformer {

    private static final CommandArgumentTransformer INSTANCE = new NoopCommandArgumentTransformer();

    public static CommandArgumentTransformer getInstance() {
        return INSTANCE;
    }

    @Override
    public Object transform(String value, CommandExecutionContext context) {
        return value;
    }
}
