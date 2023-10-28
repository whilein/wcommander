package w.commander.spec.template;

import lombok.val;
import w.commander.annotation.SubCommandTypes;

/**
 * @author whilein
 */
public abstract class AbstractCommandTemplateFactory implements CommandTemplateFactory {

    protected abstract Object getInstance(Class<?> type);

    @Override
    public final CommandTemplate create(Class<?> type) {
        return createTemplate(getInstance(type));
    }

    @Override
    public final CommandTemplate create(Object instance) {
        return createTemplate(instance);
    }

    private CommandTemplate createTemplate(Object instance) {
        val subCommands = instance.getClass().getDeclaredAnnotation(SubCommandTypes.class);
        val builder = SimpleCommandTemplate.builder(instance);

        for (val subCommand : subCommands.value()) {
            builder.subCommand(create(subCommand));
        }

        return builder.build();
    }

}
