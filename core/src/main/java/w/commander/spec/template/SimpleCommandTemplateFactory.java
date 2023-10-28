package w.commander.spec.template;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/***
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleCommandTemplateFactory implements CommandTemplateFactory {

    public static CommandTemplateFactory create() {
        return new SimpleCommandTemplateFactory();
    }

    @Override
    public @NotNull CommandTemplate create(@NotNull Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull CommandTemplate create(@NotNull Object instance) {
        return SimpleCommandTemplate.create(instance);
    }
}
