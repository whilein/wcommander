package w.commander.spec.template;

/**
 * @author whilein
 */
public interface CommandTemplateFactory {

    CommandTemplate create(Class<?> type);

    CommandTemplate create(Object instance);

}
