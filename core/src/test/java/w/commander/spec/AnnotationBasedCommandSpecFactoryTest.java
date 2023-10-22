package w.commander.spec;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import w.commander.CommandSender;
import w.commander.annotation.*;
import w.commander.error.NoopCommandErrorFactory;
import w.commander.manual.description.SimpleCommandDescriptionFactory;
import w.commander.manual.usage.SimpleCommandUsageFactory;
import w.commander.parameter.CommandParameter;
import w.commander.parameter.DefaultCommandParameterResolver;
import w.commander.parameter.OfIterableCommandParameterResolvers;
import w.commander.parameter.argument.CommandArgument;
import w.commander.parameter.argument.transformer.CommandArgumentTransformer;
import w.commander.parameter.argument.transformer.type.NoopCommandArgumentTransformer;
import w.commander.parameter.argument.transformer.type.NumberCommandArgumentTransformer;
import w.commander.parameter.argument.transformer.type.NumberCommandArgumentTransformerFactoryResolver;
import w.commander.parameter.argument.type.JoinCommandArgument;
import w.commander.parameter.argument.type.OrdinaryCommandArgument;
import w.commander.parameter.type.SenderCommandParameter;
import w.commander.spec.path.CommandHandlerPathStrategies;
import w.commander.spec.template.CommandTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author whilein
 */
class AnnotationBasedCommandSpecFactoryTest {

    private static CommandSpecFactory commandSpecFactory;

    @BeforeAll
    public static void setup() {
        commandSpecFactory = AnnotationBasedCommandSpecFactory.create(CommandHandlerPathStrategies.lowerSnakeCase(),
                OfIterableCommandParameterResolvers.from(
                        DefaultCommandParameterResolver.create(
                                NumberCommandArgumentTransformerFactoryResolver.create(
                                        NoopCommandErrorFactory.create()
                                )
                        )
                ),
                SimpleCommandUsageFactory.create(),
                SimpleCommandDescriptionFactory.create()
        );
    }

    private static CommandSpec createSimple(Object instance) {
        return commandSpecFactory.create(CommandTemplate.from(instance));
    }

    private static CommandSpec create(CommandTemplate template) {
        return commandSpecFactory.create(template);
    }

    @Nested
    @DisplayName("Path Generation")
    class PathGenerationTest {
        @Test
        @DisplayName("Nested Sub Commands")
        public void testNestedSubCommands() {
            @Command("test")
            class TestCommand {
            }

            @SubCommand("L1")
            class TestL1Command {
                @CommandHandler
                public void exec() {
                }
            }

            @SubCommand("L2")
            class TestL2Command {
                @CommandHandler
                public void exec() {
                }
            }

            @SubCommand("L3")
            class TestL3Command {
                @CommandHandler
                public void exec() {
                }
            }

            val command = create(CommandTemplate.builder(new TestCommand())
                    .subCommand(new TestL1Command(), l1 -> l1
                            .subCommand(new TestL2Command(), l2 -> l2
                                    .subCommand(new TestL3Command())))
                    .build());

            assertThat(command.path())
                    .isEqualTo("test");

            assertThat(command.subCommands()).singleElement()
                    .returns("test_l1", CommandSpec::path)
                    .satisfies(l1 -> assertThat(l1.handlers()).singleElement()
                            .returns("test_l1", HandlerSpec::path))
                    .satisfies(l1 -> assertThat(l1.subCommands()).singleElement()
                            .returns("test_l1_l2", CommandSpec::path)
                            .satisfies(l2 -> assertThat(l2.handlers()).singleElement()
                                    .returns("test_l1_l2", HandlerSpec::path))
                            .satisfies(l2 -> assertThat(l2.subCommands()).singleElement()
                                    .returns("test_l1_l2_l3", CommandSpec::path)
                                    .satisfies(l3 -> assertThat(l3.handlers()).singleElement()
                                            .returns("test_l1_l2_l3", HandlerSpec::path))));
        }

        @Test
        @DisplayName("Sub Command")
        public void testSubCommand() {
            @Command("test")
            class TestCommand {
            }

            @SubCommand("exec")
            class TestSubCommand {
                @CommandHandler
                public void exec() {
                }
            }

            val command = create(CommandTemplate.builder(new TestCommand())
                    .subCommand(new TestSubCommand())
                    .build());

            assertThat(command.path())
                    .isEqualTo("test");

            assertThat(command.subCommands()).singleElement()
                    .returns("test_exec", CommandSpec::path)
                    .satisfies(subCommand -> assertThat(subCommand.handlers()).singleElement()
                            .returns("test_exec", HandlerSpec::path));
        }

        @Test
        @DisplayName("Sub Command Handler")
        public void testSubCommandHandler() {
            @Command("test")
            class TestCommand {
                @SubCommandHandler("exec")
                public void execute() {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.path())
                    .isEqualTo("test");

            assertThat(command.subCommands()).singleElement()
                    .returns("test_exec", CommandSpec::path)
                    .satisfies(subCommand -> assertThat(subCommand.handlers()).singleElement()
                            .returns("test_exec", HandlerSpec::path));
        }

        @Test
        @DisplayName("Handler")
        public void testHandler() {
            @Command("test")
            class TestCommand {
                @CommandHandler("exec")
                public void execute() {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.path())
                    .isEqualTo("test");

            assertThat(command.handlers()).singleElement()
                    .returns("test_exec", HandlerSpec::path);
        }

        @Test
        @DisplayName("Command")
        public void testCommand() {
            @Command("test")
            class TestCommand {
                @CommandHandler
                public void execute() {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.path())
                    .isEqualTo("test");

            assertThat(command.handlers()).singleElement()
                    .returns("test", HandlerSpec::path);
        }
    }

    @Nested
    @DisplayName("@SubCommandHandler")
    class SubCommandHandlerTest {
        @Test
        @DisplayName("Valid")
        public void testValid() {
            @Command("test")
            class TestCommand {
                @SubCommandHandler("sub")
                public void sub() {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.subCommands())
                    .singleElement()
                    .returns("sub", CommandSpec::name);
        }

        @Test
        @DisplayName("Invalid")
        public void testInvalid() {
            @Command("test")
            class TestCommand {
                @SubCommandHandler("sub ")
                public void sub() {
                }
            }

            assertThrows(IllegalArgumentException.class, () -> createSimple(new TestCommand()));
        }

    }

    @Nested
    @DisplayName("@SubCommand")
    class SubCommandTest {
        @Test
        @DisplayName("Absent")
        public void testAbsent() {
            @Command("test")
            class TestCommand {
            }

            class TestSubCommand {
            }

            assertThrows(IllegalArgumentException.class, () -> create(CommandTemplate.builder(new TestCommand())
                    .subCommand(new TestSubCommand())
                    .build()));
        }

        @Test
        @DisplayName("Invalid")
        public void testInvalid() {
            @SubCommand("sub ")
            class TestSubCommand {
            }

            @Command("test")
            class TestCommand {

            }

            assertThrows(IllegalArgumentException.class, () -> create(CommandTemplate.builder(new TestCommand())
                    .subCommand(new TestSubCommand())
                    .build()));
        }

        @Test
        @DisplayName("Valid")
        public void testValid() {
            @SubCommand("sub")
            class TestSubCommand {
            }

            @Command("test")
            class TestCommand {

            }

            val command = create(CommandTemplate.builder(new TestCommand())
                    .subCommand(new TestSubCommand())
                    .build());

            assertThat(command.subCommands())
                    .singleElement()
                    .returns("sub", CommandSpec::name);
        }
    }

    @Nested
    @DisplayName("@WithManual @WithManualSubCommand")
    class ManualTest {
        @Test
        @DisplayName("Absent")
        public void testAbsent() {
            @Command("test")
            class TestCommand {
            }


            val command = createSimple(new TestCommand());

            assertThat(command.manual())
                    .returns(false, ManualSpec::hasHandler)
                    .extracting(ManualSpec::getSubCommand, optional(String.class))
                    .isEmpty();
        }

        @Test
        @DisplayName("Present Handler and Sub Command")
        public void testHandlerAndSubCommandPresent() {
            @Command("test")
            @WithManual
            @WithManualSubCommand("help")
            class TestCommand {
            }


            val command = createSimple(new TestCommand());

            assertThat(command.manual())
                    .returns(true, ManualSpec::hasHandler)
                    .extracting(ManualSpec::getSubCommand, optional(String.class))
                    .contains("help");
        }

        @Test
        @DisplayName("Present Sub Command")
        public void testSubCommandPresent() {
            @Command("test")
            @WithManualSubCommand("help")
            class TestCommand {
            }


            val command = createSimple(new TestCommand());

            assertThat(command.manual())
                    .returns(false, ManualSpec::hasHandler)
                    .extracting(ManualSpec::getSubCommand, optional(String.class))
                    .contains("help");
        }

        @Test
        @DisplayName("Present Handler")
        public void testHandlerPresent() {
            @Command("test")
            @WithManual
            class TestCommand {
            }


            val command = createSimple(new TestCommand());

            assertThat(command.manual())
                    .returns(true, ManualSpec::hasHandler)
                    .extracting(ManualSpec::getSubCommand, optional(String.class))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("@Command")
    public class CommandTest {
        @Test
        @DisplayName("Absent")
        public void testAbsent() {
            class TestCommand {
            }

            assertThrows(IllegalArgumentException.class, () -> createSimple(new TestCommand()));
        }

        @Test
        @DisplayName("Invalid")
        public void testInvalid() {
            @Command("test ")
            class NameWithSpace {
            }

            @Command("")
            class EmptyName {
            }

            assertThrows(IllegalArgumentException.class, () -> createSimple(new NameWithSpace()));
            assertThrows(IllegalArgumentException.class, () -> createSimple(new EmptyName()));
        }

        @Test
        @DisplayName("Valid")
        public void testValid() {
            @Command("test")
            class TestCommand {
            }

            val command = createSimple(new TestCommand());

            assertThat(command.name())
                    .isEqualTo("test");
        }
    }

    @Nested
    @DisplayName("Parameter")
    public class ParameterTest {

        @Test
        @DisplayName("Command Sender")
        public void testCommandSender() {
            @Command("test")
            class TestCommand {
                @CommandHandler
                public void exec(CommandSender sender) {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.handlers()).singleElement()
                    .extracting(HandlerSpec::parameters, array(CommandParameter[].class))
                    .singleElement()
                    .isInstanceOf(SenderCommandParameter.class);
        }

        @Test
        @DisplayName("@Arg int")
        public void testInt() {
            @Command("test")
            class TestCommand {
                @CommandHandler
                public void exec(@Arg int value) {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.handlers()).singleElement()
                    .extracting(HandlerSpec::parameters, array(CommandParameter[].class))
                    .singleElement()
                    .asInstanceOf(type(OrdinaryCommandArgument.class))
                    .returns(true, CommandArgument::required)
                    .returns("", CommandArgument::name)
                    .extracting(OrdinaryCommandArgument::argumentTransformer,
                            type(CommandArgumentTransformer.class))
                    .isInstanceOf(NumberCommandArgumentTransformer.class);
        }

        @Test
        @DisplayName("@Join")
        public void testJoin() {
            @Command("test")
            class TestCommand {
                @CommandHandler
                public void exec(@Join String value) {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.handlers()).singleElement()
                    .extracting(HandlerSpec::parameters, array(CommandParameter[].class))
                    .singleElement()
                    .isInstanceOf(JoinCommandArgument.class);
        }

        @Test
        @DisplayName("@NonRequired @Arg String")
        public void testOptionalString() {
            @Command("test")
            class TestCommand {
                @CommandHandler
                public void exec(@NonRequired @Arg String value) {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.handlers()).singleElement()
                    .extracting(HandlerSpec::parameters, array(CommandParameter[].class))
                    .singleElement()
                    .asInstanceOf(type(OrdinaryCommandArgument.class))
                    .returns(false, CommandArgument::required)
                    .extracting(OrdinaryCommandArgument::argumentTransformer,
                            type(CommandArgumentTransformer.class))
                    .isInstanceOf(NoopCommandArgumentTransformer.class);
        }

        @Test
        @DisplayName("@Arg String")
        public void testString() {
            @Command("test")
            class TestCommand {
                @CommandHandler
                public void exec(@Arg String value) {
                }
            }

            val command = createSimple(new TestCommand());

            assertThat(command.handlers()).singleElement()
                    .extracting(HandlerSpec::parameters, array(CommandParameter[].class))
                    .singleElement()
                    .asInstanceOf(type(OrdinaryCommandArgument.class))
                    .returns(true, CommandArgument::required);
        }
    }

    @Nested
    @DisplayName("@WithAlias @WithAliases")
    public class WithAliasTest {
        @Test
        @DisplayName("Invalid")
        public void testInvalid() {
            @Command("test")
            @WithAlias("t1 ")
            class AliasWithSpace {
            }

            @Command("test")
            @WithAlias("")
            class EmptyAlias {
            }

            assertThrows(IllegalArgumentException.class, () -> createSimple(new AliasWithSpace()));
            assertThrows(IllegalArgumentException.class, () -> createSimple(new EmptyAlias()));
        }

        @Test
        @DisplayName("Absent")
        public void testAbsent() {
            @Command("test")
            class TestCommand {
            }

            val command = createSimple(new TestCommand());

            assertThat(command.aliases())
                    .isNotNull()
                    .isEmpty();
        }


        @Test
        @DisplayName("Valid Multiple")
        public void testMultiple() {
            @Command("test")
            @WithAlias("t1")
            @WithAlias("t2")
            @WithAlias("t3")
            class TestCommand {
            }

            val command = createSimple(new TestCommand());

            assertThat(command.aliases())
                    .containsExactly("t1", "t2", "t3");
        }

        @Test
        @DisplayName("Valid Single")
        public void testSingle() {
            @Command("test")
            @WithAlias("t1")
            class TestCommand {
            }

            val command = createSimple(new TestCommand());

            assertThat(command.aliases())
                    .containsExactly("t1");
        }
    }


}