package w.commander.spec.template;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleCommandTemplateFactory implements CommandTemplateFactory {

    public static CommandTemplateFactory create() {
        return new SimpleCommandTemplateFactory();
    }

    @Override
    public CommandTemplate create(Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandTemplate create(Object instance) {
        return CommandTemplate.from(instance);
    }
}
