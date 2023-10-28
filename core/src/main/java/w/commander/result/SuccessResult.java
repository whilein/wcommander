package w.commander.result;

/**
 * @author whilein
 */
public interface SuccessResult extends Result {

    @Override
    default boolean isSuccess() {
        return true;
    }

}
