package w.commander.parameter.argument;

import w.commander.parameter.CommandParameter;

public interface CommandArgument extends CommandParameter {

    String name();

    default int minLength() {
        return 1;
    }

    default int maxLength() {
        return 1;
    }

    boolean required();

}
