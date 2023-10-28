package w.commander.result;

/**
 * @author whilein
 */
public interface FailedResult extends Result {

    @Override
    default boolean isSuccess() {
        return false;
    }

}
