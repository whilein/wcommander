package w.commander.tests

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import w.commander.CommandActor
import w.commander.annotation.*
import w.commander.error.NoopErrorResultFactory
import w.commander.kt.CommandTemplate.withSubCommand
import w.commander.manual.description.SimpleDescriptionFactory
import w.commander.manual.usage.SimpleUsageFactory
import w.commander.parameter.DefaultHandlerParameterResolver
import w.commander.parameter.argument.parser.type.NoopArgumentParser
import w.commander.parameter.argument.parser.type.NumberArgumentParser
import w.commander.parameter.argument.type.JoinArgument
import w.commander.parameter.argument.type.OrdinaryArgument
import w.commander.parameter.type.ActorHandlerParameter
import w.commander.spec.AnnotationBasedCommandSpecFactory
import w.commander.spec.path.HandlerPathStrategies
import w.commander.spec.template.SimpleCommandTemplate

/**
 * @author whilein
 */
class AnnotationBasedCommandSpecFactoryTests : FunSpec({

    val commandSpecFactory = AnnotationBasedCommandSpecFactory.create(
            HandlerPathStrategies.lowerSnakeCase(),
            SimpleUsageFactory.create(),
            SimpleDescriptionFactory.create(),
            listOf(
                    DefaultHandlerParameterResolver.create(
                            NoopErrorResultFactory.create()
                    )
            )
    )

    context("Path") {
        test("Nested sub command path") {
            @Command("test")
            class TestCommand

            @SubCommand("L1")
            class TestL1Command {
                @CommandHandler
                fun exec() {
                }
            }

            @SubCommand("L2")
            class TestL2Command {
                @CommandHandler
                fun exec() {
                }
            }

            @SubCommand("L3")
            class TestL3Command {
                @CommandHandler
                fun exec() {
                }
            }


            val command = commandSpecFactory.create(TestCommand()
                    .withSubCommand(TestL1Command()
                            .withSubCommand(TestL2Command()
                                    .withSubCommand(TestL3Command()))))

            command.path shouldBe "test"

            val l1 = command.subCommands.single()
            l1.path shouldBe "test_l1"
            l1.handlers.single().path shouldBe "test_l1"

            val l2 = l1.subCommands.single()
            l2.path shouldBe "test_l1_l2"
            l2.handlers.single().path shouldBe "test_l1_l2"

            val l3 = l2.getSubCommands().single()
            l3.path shouldBe "test_l1_l2_l3"
            l3.handlers.single().path shouldBe "test_l1_l2_l3"
        }

        test("Sub command path") {
            @Command("test")
            class TestCommand

            @SubCommand("exec")
            class TestSubCommand {
                @CommandHandler
                fun exec() {
                }
            }

            val command = commandSpecFactory.create(SimpleCommandTemplate.builder(TestCommand())
                    .subCommand(TestSubCommand())
                    .build())

            command.path shouldBe "test"

            val subCommand = command.subCommands.single()
            subCommand.path shouldBe "test_exec"
            subCommand.handlers.single().path shouldBe "test_exec"
        }

        test("Sub command handler path") {
            @Command("test")
            class TestCommand {
                @SubCommandHandler("exec")
                fun exec() {

                }
            }

            val command = commandSpecFactory.create(TestCommand())

            command.path shouldBe "test"

            val subCommand = command.subCommands.single()
            subCommand.path shouldBe "test_exec"
            subCommand.handlers.single().path shouldBe "test_exec"
        }

        test("Named command handler path") {
            @Command("test")
            class TestCommand {
                @CommandHandler("exec")
                fun execute() {
                }
            }

            val command = commandSpecFactory.create(TestCommand())

            command.path shouldBe "test"
            command.handlers.single().path shouldBe "test_exec"
        }


        test("Command handler path") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute() {
                }
            }

            val command = commandSpecFactory.create(TestCommand())

            command.path shouldBe "test"
            command.handlers.single().path shouldBe "test"
        }
    }

    context("Sub command handler") {
        test("Correct name does not fails") {
            @Command("test")
            class TestCommand {
                @SubCommandHandler("sub")
                fun sub() {
                }
            }

            val command = commandSpecFactory.create(TestCommand())
            command.subCommands.single().name shouldBe "sub"
        }

        test("Empty name fails") {
            @Command("test")
            class TestCommand {
                @SubCommandHandler("")
                fun sub() {
                }
            }

            shouldThrow<IllegalArgumentException> { commandSpecFactory.create(TestCommand()) }
        }

        test("Name with space fails") {
            @Command("test")
            class TestCommand {
                @SubCommandHandler("sub ")
                fun sub() {
                }
            }

            shouldThrow<IllegalArgumentException> { commandSpecFactory.create(TestCommand()) }
        }
    }

    context("Sub command") {
        test("Absent declaration fails") {
            @Command("test")
            class TestCommand
            class TestSubCommand

            shouldThrow<IllegalArgumentException> {
                commandSpecFactory.create(TestCommand()
                        .withSubCommand(TestSubCommand()))
            }
        }

        test("Correct name does not fails") {
            @Command("test")
            class TestCommand

            @SubCommand("sub")
            class TestSubCommand

            val command = commandSpecFactory.create(TestCommand()
                    .withSubCommand(TestSubCommand()))

            command.subCommands.single().name shouldBe "sub"
        }

        test("Empty name fails") {
            @Command("test")
            class TestCommand

            @SubCommand("")
            class TestSubCommand

            shouldThrow<IllegalArgumentException> {
                commandSpecFactory.create(TestCommand()
                        .withSubCommand(TestSubCommand()))
            }
        }

        test("Name with space fails") {
            @Command("test")
            class TestCommand

            @SubCommand("sub ")
            class TestSubCommand

            shouldThrow<IllegalArgumentException> {
                commandSpecFactory.create(TestCommand()
                        .withSubCommand(TestSubCommand()))
            }
        }
    }

    context("Manual") {
        test("Absent declaration") {
            @Command("test")
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())

            val manual = command.manual
            manual.hasHandler() shouldBe false
            manual.subCommand shouldBe null
        }

        test("With handler and sub command") {
            @Command("test")
            @WithManual
            @WithManualSubCommand("help")
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())

            val manual = command.manual
            manual.hasHandler() shouldBe true
            manual.subCommand shouldBe "help"
        }

        test("With handler") {
            @Command("test")
            @WithManual
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())

            val manual = command.manual
            manual.hasHandler() shouldBe true
            manual.subCommand shouldBe null
        }

        test("With sub command") {
            @Command("test")
            @WithManualSubCommand("help")
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())

            val manual = command.manual
            manual.hasHandler() shouldBe false
            manual.subCommand shouldBe "help"
        }
    }

    context("Command") {
        test("Correct name does not fails") {
            @Command("test")
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())
            command.name shouldBe "test"
        }

        test("Absent declaration fails") {
            class TestCommand

            shouldThrow<IllegalArgumentException> { commandSpecFactory.create(TestCommand()) }
        }

        test("Name with space fails") {
            @Command("test ")
            class TestCommand

            shouldThrow<IllegalArgumentException> { commandSpecFactory.create(TestCommand()) }
        }

        test("Empty name fails") {
            @Command("")
            class TestCommand

            shouldThrow<IllegalArgumentException> { commandSpecFactory.create(TestCommand()) }
        }
    }

    context("Parameters") {
        test("CommandActor") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun exec(actor: CommandActor) {
                }
            }

            val command = commandSpecFactory.create(TestCommand())

            command
                    .handlers.single()
                    .parameters.single()
                    .shouldBeTypeOf<ActorHandlerParameter>()
        }

        test("Int") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun exec(@Arg value: Int) {
                }
            }

            val command = commandSpecFactory.create(TestCommand())

            val argument = command
                    .handlers.single()
                    .parameters.single()
                    as OrdinaryArgument

            argument.isRequired shouldBe true
            argument.parser.shouldBeTypeOf<NumberArgumentParser>()
        }

        test("Optional string") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun exec(@NonRequired @Arg value: String) {
                }
            }

            val command = commandSpecFactory.create(TestCommand())

            val argument = command
                    .handlers.single()
                    .parameters.single()
                    as OrdinaryArgument

            argument.isRequired shouldBe false
            argument.parser.shouldBeTypeOf<NoopArgumentParser>()
        }

        test("Joined string") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun exec(@Join value: String) {
                }
            }

            val command = commandSpecFactory.create(TestCommand())

            val argument = command
                    .handlers.single()
                    .parameters.single()
                    as JoinArgument

            argument.isRequired shouldBe true
        }

        test("String") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun exec(@Arg value: String) {
                }
            }

            val command = commandSpecFactory.create(TestCommand())

            val argument = command
                    .handlers.single()
                    .parameters.single()
                    as OrdinaryArgument

            argument.isRequired shouldBe true
            argument.parser.shouldBeTypeOf<NoopArgumentParser>()
        }
    }

    context("Aliases") {
        test("With space fails") {
            @Command("test")
            @WithAlias("t1 ")
            class TestCommand

            shouldThrow<IllegalArgumentException> { commandSpecFactory.create(TestCommand()) }
        }

        test("Empty fails") {
            @Command("test")
            @WithAlias("")
            class TestCommand

            shouldThrow<IllegalArgumentException> { commandSpecFactory.create(TestCommand()) }
        }

        test("Absent is empty") {
            @Command("test")
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())
            command.aliases.isEmpty() shouldBe true
        }

        test("Present is not empty") {
            @Command("test")
            @WithAlias("t1")
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())
            command.aliases shouldBe arrayOf("t1")
        }

        test("Declaration is repeatable") {
            @Command("test")
            @WithAlias("t1")
            @WithAlias("t2")
            @WithAlias("t3")
            class TestCommand

            val command = commandSpecFactory.create(TestCommand())
            command.aliases shouldBe arrayOf("t1", "t2", "t3")
        }
    }
})