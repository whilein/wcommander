package w.commander.annotation;

import java.lang.annotation.*;

/**
 * @author whilein
 */
@Repeatable(WithAliases.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithAlias {

    String value();

}
