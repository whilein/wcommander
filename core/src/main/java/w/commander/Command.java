package w.commander;

import java.util.List;

/**
 * @author whilein
 */
public interface Command {

    String name();

    List<String> aliases();

    void execute(CommandSender sender, RawCommandArguments args);

}
