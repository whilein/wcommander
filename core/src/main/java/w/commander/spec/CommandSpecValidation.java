package w.commander.spec;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.regex.Pattern;

/**
 * @author whilein
 */
@UtilityClass
public class CommandSpecValidation {

    private final Pattern VALID_COMMAND_NAME = Pattern.compile("^[^ ]+$");

    public void checkCommandNames(String... names) {
        for (val name : names) {
            checkCommandName(name);
        }
    }

    public void checkCommandName(String name) {
        if (!VALID_COMMAND_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid command name value: \"" + name + "\"");
        }
    }

}
