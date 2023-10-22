package w.commander.result;

/**
 * @author whilein
 */
public interface SuccessCommandResult extends CommandResult {

    @Override
    default boolean isSuccess() {
        return true;
    }

}
