package w.commander.spec;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author whilein
 */
public interface NameAwareSpec {
    @NotNull String getName();

    @Unmodifiable @NotNull List<@NotNull String> getAliases();

    default @Unmodifiable @NotNull List<@NotNull String> getNames() {
        val result = new ArrayList<String>();
        result.add(getName());
        result.addAll(getAliases());

        return Collections.unmodifiableList(result);
    }
}
