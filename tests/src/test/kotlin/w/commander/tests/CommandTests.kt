package w.commander.tests

import io.kotest.assertions.throwables.shouldThrow
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
import w.commander.kt.EmptyRawArguments
import w.commander.manual.SimpleManualFactory
import w.commander.manual.description.SimpleDescriptionFactory
import w.commander.manual.usage.SimpleUsageFactory
import w.commander.parameter.DefaultHandlerParameterResolver
import w.commander.parameter.HandlerParameter
import w.commander.parameter.HandlerParameterResolver
import w.commander.spec.AnnotationBasedCommandSpecFactory
import w.commander.spec.path.HandlerPathStrategies
import java.lang.RuntimeException
import java.lang.reflect.Parameter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.function.Supplier

/**
 * @author whilein
 */
class CommandTests : FunSpec({

    val errorResultFactory = spyk(NoopErrorResultFactory.create())
    val executionThrowableInterceptor = spyk(DefaultExecutionThrowableInterceptor.create(errorResultFactory))

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
            executionThrowableInterceptor,
            SimpleManualFactory.create(),
    )

    afterEach {
        clearAllMocks()
    }

    context("Errors") {
        test("Should catch parameter exception") {
            val exception = RuntimeException()

            val errorCommandSpecFactory = AnnotationBasedCommandSpecFactory.create(
                    HandlerPathStrategies.lowerSnakeCase(),
                    SimpleUsageFactory.create(),
                    SimpleDescriptionFactory.create(),
                    listOf(
                            object : HandlerParameterResolver {
                                override fun isSupported(parameter: Parameter): Boolean {
                                    return true
                                }

                                override fun resolve(parameter: Parameter): HandlerParameter {
                                    return HandlerParameter { _, _ ->
                                        throw exception
                                    }
                                }

                            }
                    )
            )

            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(param: Any) {
                }
            }

            val command = commandFactory.create(errorCommandSpecFactory.create(TestCommand()))

            shouldThrow<CompletionException> {
                command.execute(TestCommandActor, EmptyRawArguments)
                        .join()
            }

            verify { executionThrowableInterceptor.intercept(exception) }
        }

        test("Should catch lazy execution exception") {
            val exception = RuntimeException()

            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(): Supplier<*> {
                    return Supplier {
                        throw exception
                    }
                }
            }

            val command = commandFactory.create(commandSpecFactory.create(TestCommand()))

            shouldThrow<CompletionException> {
                command.execute(TestCommandActor, EmptyRawArguments)
                        .join()
            }

            verify { executionThrowableInterceptor.intercept(exception) }
        }

        test("Should catch future execution exception") {
            val exception = RuntimeException()

            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute(): CompletableFuture<*> {
                    val future = CompletableFuture<Any>()
                    future.completeExceptionally(exception)

                    return future
                }
            }

            val command = commandFactory.create(commandSpecFactory.create(TestCommand()))

            shouldThrow<CompletionException> {
                command.execute(TestCommandActor, EmptyRawArguments)
                        .join()
            }

            verify { executionThrowableInterceptor.intercept(exception) }
        }

        test("Should catch execution exception") {
            val exception = RuntimeException()

            @Command("test")
            class TestCommand {
                @CommandHandler
                fun execute() {
                    throw exception
                }
            }

            val command = commandFactory.create(commandSpecFactory.create(TestCommand()))
            command.execute(TestCommandActor, EmptyRawArguments)

            verify { executionThrowableInterceptor.intercept(exception) }
        }
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