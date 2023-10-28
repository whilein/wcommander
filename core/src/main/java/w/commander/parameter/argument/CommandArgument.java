package w.commander.parameter.argument;

import w.commander.parameter.CommandParameter;

public interface CommandArgument extends CommandParameter {

    String getName();

    default int getMinLength() {
        return 1;
    }

    default int getMaxLength() {
        return 1;
    }

    boolean isRequired();

}
