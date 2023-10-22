package w.commander.annotation;

import java.lang.annotation.*;

/**
 * @author whilein
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithAliases {

    WithAlias[] value();

}
