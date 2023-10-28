package w.commander;

import org.jetbrains.annotations.NotNull;
import w.commander.result.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author whilein
 */
public interface Command {

    @NotNull String getName();

    @NotNull List<@NotNull String> getAliases();

    @NotNull CompletableFuture<@NotNull Result> execute(@NotNull CommandActor actor, @NotNull RawArguments args);

}
