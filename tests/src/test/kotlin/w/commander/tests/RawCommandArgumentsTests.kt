package w.commander.tests

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import w.commander.RawCommandArguments
import w.commander.kt.RawCommandArguments.get
import w.commander.kt.RawCommandArguments.size

/**
 * @author whilein
 */
object RawCommandArgumentsTests {
    fun implementationTests(argumentFactory: (Int) -> RawCommandArguments) = funSpec {

        context("Join") {
            test("By empty delimiter returns joined string") {
                val arguments = argumentFactory(5)
                arguments.join("") shouldBe "12345"
            }

            test("By space delimiter returns joined string") {
                val arguments = argumentFactory(5)
                arguments.join(" ") shouldBe "1 2 3 4 5"
            }

            test("With starting index returns joined string") {
                val arguments = argumentFactory(5)
                arguments.join("", 2) shouldBe "345"
            }

            test("With ending index returns joined string") {
                val arguments = argumentFactory(5)
                arguments.join("", 0, 2) shouldBe "12"
            }

            test("With starting and ending indices returns joined string") {
                val arguments = argumentFactory(5)
                arguments.join("", 2, 4) shouldBe "34"
            }

            test("Of sub arguments returns joined string") {
                val arguments = argumentFactory(5)
                arguments[0 until 0].join("") shouldBe ""
                arguments[0 until 1].join("") shouldBe "1"
                arguments[1 until 3].join("") shouldBe "23"
                arguments[2 until 5].join("") shouldBe "345"
            }

            test("Out of bound indices fails") {
                val arguments = argumentFactory(5)

                shouldThrow<IllegalArgumentException> { arguments.join("", 6) }
                shouldThrow<IndexOutOfBoundsException> { arguments.join("", 1, 6) }
                shouldThrow<IndexOutOfBoundsException> { arguments.join("", -1, 2) }
                shouldThrow<IllegalArgumentException> { arguments.join("", 1, -2) }
            }

            test("Of sub arguments out of bound indices fails") {
                val arguments = argumentFactory(5)

                shouldThrow<IllegalArgumentException> { arguments[2 until 5].join("", 4) }
                shouldThrow<IndexOutOfBoundsException> { arguments[2 until 5].join("", 1, 4) }
                shouldThrow<IndexOutOfBoundsException> { arguments[2 until 5].join("", -1, 2) }
                shouldThrow<IllegalArgumentException> { arguments[2 until 5].join("", 1, -2) }
            }
        }

        context("Value") {
            listOf(
                    0,
                    5,
                    10
            ).forEach {
                test("Of $it arguments does not fail") {
                    val arguments = argumentFactory(it)

                    for (i in 0 until it) {
                        arguments[i] shouldBe (i + 1).toString()
                    }
                }
            }

            test("Of shifted arguments does not fail") {
                val arguments = argumentFactory(10)

                for (i in 0 until 10) {
                    val shifted = arguments.withOffset(i)

                    for (j in 0 until (10 - i)) {
                        shifted[j] shouldBe (j + i + 1).toString()
                    }
                }
            }

            test("Of limited arguments does not fail") {
                val arguments = argumentFactory(10)

                for (i in 0 until 10) {
                    val limited = arguments.withLength(i)

                    for (j in 0 until i) {
                        limited[j] shouldBe (j + 1).toString()
                    }
                }
            }

            test("Of sub arguments does not fail") {
                val arguments = argumentFactory(10)

                for (i in 0..10) {
                    for (j in i..10) {
                        for (k in 0 until (j - i)) {
                            arguments.subArguments(i, j)[k] shouldBe (i + k + 1).toString()
                        }
                    }
                }
            }
        }

        context("Size") {
            listOf(
                    0,
                    5,
                    10
            ).forEach {
                test("Of $it arguments does not fail") {
                    val arguments = argumentFactory(it)
                    arguments.size shouldBe it
                }
            }

            test("Of shifted arguments does not fail") {
                val arguments = argumentFactory(10)

                for (i in 0..10) {
                    arguments.withOffset(i).size shouldBe 10 - i
                }
            }

            test("Of limited arguments does not fail") {
                val arguments = argumentFactory(10)

                for (i in 0..10) {
                    arguments.withLength(i).size shouldBe i
                }
            }

            test("Of sub arguments does not fail") {
                val arguments = argumentFactory(10)

                for (i in 0..10) {
                    for (j in i..10) {
                        arguments[i until j].size shouldBe j - i
                    }
                }
            }
        }

        test("Value at index out of bound fails") {
            val arguments = argumentFactory(10)

            shouldThrow<IndexOutOfBoundsException> { arguments.withLength(0)[0] }
            shouldThrow<IndexOutOfBoundsException> { arguments.withOffset(10)[0] }
            shouldThrow<IndexOutOfBoundsException> { arguments[-1] }
            shouldThrow<IndexOutOfBoundsException> { arguments[10] }
        }

        test("Shift by incorrect value fails") {
            val arguments = argumentFactory(10)

            shouldThrow<IndexOutOfBoundsException> { arguments.withOffset(-1) }
            shouldThrow<IllegalArgumentException> { arguments.withOffset(11) }
        }

        test("Limit by incorrect value fails") {
            val arguments = argumentFactory(10)

            shouldThrow<IllegalArgumentException> { arguments.withLength(-1) }
            shouldThrow<IndexOutOfBoundsException> { arguments.withLength(11) }
        }

        test("Sub arguments for incorrect range fails") {
            val arguments = argumentFactory(10)

            shouldThrow<IndexOutOfBoundsException> { arguments[-1 until 0] }
            shouldThrow<IllegalArgumentException> { arguments[0 until -1] }
            shouldThrow<IndexOutOfBoundsException> { arguments[0 until 11] }
            shouldThrow<IllegalArgumentException> { arguments[11 until 0] }
        }

    }

    class OfArray : FunSpec({
        include(implementationTests {
            RawCommandArguments.fromList((1..it).map(Int::toString).toList())
        })
    })

    class OfList : FunSpec({
        include(implementationTests {
            RawCommandArguments.fromArray(*(1..it).map(Int::toString).toTypedArray())
        })

        test("Factory with incorrect ranges fails") {
            val array = arrayOf("1")

            shouldThrow<IllegalArgumentException> { RawCommandArguments.fromArray(array, 0, 2) }
            shouldThrow<IllegalArgumentException> { RawCommandArguments.fromArray(array, 2, 0) }
            shouldThrow<IllegalArgumentException> { RawCommandArguments.fromArray(array, -1, 1) }
            shouldThrow<IllegalArgumentException> { RawCommandArguments.fromArray(array, 0, -1) }
        }

        test("Factory with correct range doesn't fail") {
            val arguments = RawCommandArguments.fromArray(arrayOf("1", "2"), 1, 1)

            arguments[0] shouldBe "2"
            arguments.size shouldBe 1
        }
    })
}