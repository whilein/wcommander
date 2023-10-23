package w.commander;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.spec.CommandSpecFactory;
import w.commander.spec.template.CommandTemplate;
import w.commander.spec.template.CommandTemplateFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCommandManager implements CommandManager {

    Map<Class<?>, Command> type2CommandMap = new ConcurrentHashMap<>();

    CommandTemplateFactory commandTemplateFactory;

    CommandSpecFactory commandSpecFactory;

    CommandFactory commandFactory;

    @Override
    public void register(@NotNull Object instance) {
        val commandTemplate = commandTemplateFactory.create(instance);
        val command = createCommand(commandTemplate);

        put(instance.getClass(), command);
        register(command);
    }

    @Override
    public void register(@NotNull Class<?> type) {
        val commandTemplate = commandTemplateFactory.create(type);
        val command = createCommand(commandTemplate);

        put(type, command);
        register(command);
    }

    private void put(Class<?> type, Command command) {
        type2CommandMap.put(type, command);
    }

    protected abstract void register(@NotNull Command command);

    protected Command createCommand(CommandTemplate commandTemplate) {
        return commandFactory.create(
                commandSpecFactory.create(commandTemplate)
        );
    }

    @Override
    public void unregister(@NotNull Object instance) {
        unregister(instance.getClass());
    }

    @Override
    public void unregister(@NotNull Class<?> type) {
        val command = type2CommandMap.remove(type);

        if (command != null) {
            unregister(command);
        }
    }

    protected abstract void unregister(@NotNull Command command);

}
