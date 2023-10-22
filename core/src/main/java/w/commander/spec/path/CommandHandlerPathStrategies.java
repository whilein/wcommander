package w.commander.spec.path;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author whilein
 */
@UtilityClass
public class CommandHandlerPathStrategies {

    public CommandHandlerPathStrategy lowerSnakeCase() {
        return new LowerSnakeCase();
    }

    public CommandHandlerPathStrategy upperSnakeCase() {
        return new UpperSnakeCase();
    }

    private static abstract class SnakeCase implements CommandHandlerPathStrategy {

        public abstract String processItem(String item);

        @Override
        public String getPath(List<String> items) {
            return items.stream()
                    .filter(item -> !item.isEmpty())
                    .map(this::processItem)
                    .collect(Collectors.joining("_"));
        }

    }

    private static final class LowerSnakeCase extends SnakeCase {

        @Override
        public String processItem(String item) {
            return item.toLowerCase();
        }
    }

    private static final class UpperSnakeCase extends SnakeCase {

        @Override
        public String processItem(String item) {
            return item.toUpperCase();
        }
    }

}
