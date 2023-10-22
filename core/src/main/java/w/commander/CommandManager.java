package w.commander;

/**
 * @author whilein
 */
public interface CommandManager {

    void register(Object instance);

    void register(Class<?> instance);

}
