package w.commander;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import lombok.val;
import org.junit.jupiter.api.Test;
import w.commander.spec.CommandSpecFactory;
import w.commander.spec.path.CommandHandlerPathStrategies;
import w.commander.spec.path.CommandHandlerPathStrategy;

/**
 * @author whilein
 */
public class GuiceTest {

    @Test
    public void test() {
        val injector = Guice.createInjector(new CommanderModule(), new AbstractModule() {
            @Provides
            public CommandHandlerPathStrategy handlerPathStrategy() {
                return CommandHandlerPathStrategies.upperSnakeCase();
            }
        });

        val commandSpecFactory = injector.getInstance(CommandSpecFactory.class);
        System.out.println(commandSpecFactory);
    }

}
