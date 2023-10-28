package w.commander.tests

import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.spyk
import io.mockk.verify
import w.commander.RawArguments
import w.commander.SimpleCommandFactory
import w.commander.SimpleCommandNodeFactory
import w.commander.annotation.Arg
import w.commander.annotation.Command
import w.commander.annotation.CommandHandler
import w.commander.annotation.Join
import w.commander.error.DefaultExecutionThrowableInterceptor
import w.commander.error.NoopErrorResultFactory
import w.commander.kt.EmptyRawArguments
import w.commander.manual.SimpleManualFactory
import w.commander.manual.description.SimpleDescriptionFactory
import w.commander.manual.usage.SimpleUsageFactory
import w.commander.parameter.DefaultHandlerParameterResolver
import w.commander.spec.AnnotationBasedCommandSpecFactory
import w.commander.spec.path.HandlerPathStrategies

/**
 * @author whilein
 */
class CommandTests : FunSpec({

    val errorResultFactory = spyk(NoopErrorResultFactory.create())

    val commandSpecFactory = AnnotationBasedCommandSpecFactory.create(
            HandlerPathStrategies.lowerSnakeCase(),
            SimpleUsageFactory.create(),
            SimpleDescriptionFactory.create(),
            listOf(
                    DefaultHandlerParameterResolver.create(
                            errorResultFactory
                    )
            )
    )

    val commandFactory = SimpleCommandFactory.create(
            SimpleCommandNodeFactory.create(errorResultFactory),
            TestExecutionContextFactory(),
            DefaultExecutionThrowableInterceptor.create(errorResultFactory),
            SimpleManualFactory.create(),
    )

    afterEach {
        clearAllMocks()
    }

    context("Join argument") {
        test("Non-empty arguments joins") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(@Join value: String) {
                }
            }

            val commandInstance = spyk<TestCommand>()

            val command = commandFactory.create(commandSpecFactory.create(commandInstance))
            command.execute(TestCommandActor, RawArguments.fromArray("1", "2", "3"))

            verify { commandInstance.execute("1 2 3") }
        }

        test("Empty arguments fails") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(@Join value: String) {
                }
            }

            val commandInstance = spyk<TestCommand>()

            val command = commandFactory.create(commandSpecFactory.create(commandInstance))
            command.execute(TestCommandActor, EmptyRawArguments)

            verify { errorResultFactory.onNotEnoughArguments(any()) }
        }

    }

    context("Number argument") {
        test("Valid number does not fail") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(@Arg value: Int) {
                }
            }

            val commandInstance = spyk<TestCommand>()

            val command = commandFactory.create(commandSpecFactory.create(commandInstance))
            command.execute(TestCommandActor, RawArguments.fromArray("123"))

            verify { commandInstance.execute(123) }
        }

        test("Invalid number fails") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(@Arg value: Int) {
                }
            }

            val commandInstance = spyk<TestCommand>()

            val command = commandFactory.create(commandSpecFactory.create(commandInstance))
            command.execute(TestCommandActor, RawArguments.fromArray("abc"))

            verify { errorResultFactory.onInvalidNumber(any(), any()) }
        }
    }

    context("Enum argument") {
        test("Known enum does not fail") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(@Arg shape: Shape) {
                }
            }

            val commandInstance = spyk<TestCommand>()

            val command = commandFactory.create(commandSpecFactory.create(commandInstance))
            command.execute(TestCommandActor, RawArguments.fromArray("rectangle"))

            verify { commandInstance.execute(Shape.RECTANGLE) }
        }

        test("Unknown enum fails") {
            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(@Arg shape: Shape) {
                }
            }

            val commandInstance = spyk<TestCommand>()

            val command = commandFactory.create(commandSpecFactory.create(commandInstance))
            command.execute(TestCommandActor, RawArguments.fromArray("rectt"))

            verify { errorResultFactory.onInvalidEnum(any(), any(), any<Map<String, Shape>>()) }
        }
    }

})

enum class Shape {

    RECTANGLE,
    TRIANGLE,
    CIRCLE,

}