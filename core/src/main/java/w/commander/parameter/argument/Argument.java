package w.commander.parameter.argument;

import w.commander.parameter.HandlerParameter;

public interface Argument extends HandlerParameter {

    String getName();

    default int getMinLength() {
        return 1;
    }

    default int getMaxLength() {
        return 1;
    }

    boolean isRequired();

}
