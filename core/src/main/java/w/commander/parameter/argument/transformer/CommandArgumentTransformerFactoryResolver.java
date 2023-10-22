package w.commander.parameter.argument.transformer;

/**
 * @author whilein
 */
public interface CommandArgumentTransformerFactoryResolver {

    boolean isSupported(Class<?> type);

    CommandArgumentTransformerFactory getTransformerFactory(Class<?> type);

}
