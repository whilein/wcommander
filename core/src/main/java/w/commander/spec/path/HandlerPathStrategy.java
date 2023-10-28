package w.commander.spec.path;

import java.util.Arrays;
import java.util.List;

/**
 * @author whilein
 */
public interface HandlerPathStrategy {

    String getPath(List<String> items);

    default String getPath(String... items) {
        return getPath(Arrays.asList(items));
    }

}
