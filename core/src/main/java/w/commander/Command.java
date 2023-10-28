package w.commander;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author whilein
 */
public interface Command {

    @NotNull String getName();

    @NotNull List<@NotNull String> getAliases();

    void execute(@NotNull CommandActor actor, @NotNull RawArguments args);

}
