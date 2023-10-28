package w.commander.parameter.argument.cursor;

/**
 * @author whilein
 */
public interface ArgumentCursor {

    boolean hasNext(boolean required);

    int next();

}
