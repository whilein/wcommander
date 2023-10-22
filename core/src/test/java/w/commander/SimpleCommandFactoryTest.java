package w.commander;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.annotation.WithManual;
import w.commander.error.NoopCommandErrorFactory;
import w.commander.execution.TestCommandExecutionContextFactory;
import w.commander.manual.SimpleCommandManualFactory;
import w.commander.manual.description.SimpleCommandDescriptionFactory;
import w.commander.manual.usage.SimpleCommandUsageFactory;
import w.commander.parameter.DefaultCommandParameterResolver;
import w.commander.parameter.OfIterableCommandParameterResolvers;
import w.commander.parameter.argument.transformer.type.NumberCommandArgumentTransformerFactoryResolver;
import w.commander.spec.AnnotationBasedCommandSpecFactory;
import w.commander.spec.CommandSpecFactory;
import w.commander.spec.path.CommandHandlerPathStrategies;
import w.commander.spec.template.CommandTemplate;

/**
 * @author whilein
 */
class SimpleCommandFactoryTest {

    static CommandSpecFactory commandSpecFactory;
    static CommandFactory commandFactory;

    static TestCommandSender testCommandSender;

    @BeforeAll
    static void setup() {
        val errorFactory = NoopCommandErrorFactory.create();

        commandFactory = SimpleCommandFactory.create(
                SimpleCommandNodeFactory.create(
                        errorFactory
                ),
                new TestCommandExecutionContextFactory(),
                SimpleCommandManualFactory.create()
        );

        commandSpecFactory = AnnotationBasedCommandSpecFactory.create(
                CommandHandlerPathStrategies.lowerSnakeCase(),
                OfIterableCommandParameterResolvers.from(
                        DefaultCommandParameterResolver.create(
                                NumberCommandArgumentTransformerFactoryResolver.create(
                                        errorFactory
                                )
                        )
                ),
                SimpleCommandUsageFactory.create(),
                SimpleCommandDescriptionFactory.create()
        );

        testCommandSender = new TestCommandSender();
    }

    @Test
    public void testCommandHandler() {
        @Command("test")
        @WithManual
        class TestCommand {

            @CommandHandler
            public void test(CommandSender sender) {
                System.out.println(sender);
            }

        }

        val commandSpec = commandSpecFactory.create(CommandTemplate.builder(new TestCommand())
                .build());

        val command = commandFactory.create(commandSpec);
        command.execute(new TestCommandSender(), RawCommandArguments.fromArray("help"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}