package w.commander.manual.description;

import org.jetbrains.annotations.NotNull;

/**
 * @author whilein
 */
public interface DescriptionFactory {

    @NotNull Description create(@NotNull String description);

}
