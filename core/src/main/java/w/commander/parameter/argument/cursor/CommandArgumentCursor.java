package w.commander.parameter.argument.cursor;

/**
 * @author whilein
 */
public interface CommandArgumentCursor {

    boolean hasNext(boolean required);

    int next();

}
