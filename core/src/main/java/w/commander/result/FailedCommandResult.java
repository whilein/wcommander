package w.commander.result;

/**
 * @author whilein
 */
public interface FailedCommandResult extends CommandResult {

    @Override
    default boolean isSuccess() {
        return false;
    }

}
