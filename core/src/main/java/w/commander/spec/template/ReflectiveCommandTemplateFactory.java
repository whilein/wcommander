package w.commander.spec.template;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author whilein
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectiveCommandTemplateFactory extends AbstractCommandTemplateFactory {

    public static CommandTemplateFactory create() {
        return new ReflectiveCommandTemplateFactory();
    }

    @Override
    @SneakyThrows
    protected Object getInstance(Class<?> type) {
        return type.newInstance();
    }

}
