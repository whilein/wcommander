package w.commander.tests

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import w.commander.kt.EmptyRawArguments
import w.commander.kt.RawCommandArguments.size
import w.commander.kt.RawCommandArguments.get

/**
 * @author whilein
 */
class EmptyRawArgumentsTests : FunSpec({

    test("Is empty") {
        EmptyRawArguments.isEmpty shouldBe true
    }

    test("Size is 0") {
        EmptyRawArguments.size shouldBe 0
    }

    test("Join returns empty string") {
        EmptyRawArguments.join("").isEmpty() shouldBe true
        EmptyRawArguments.join(" ").isEmpty() shouldBe true
        EmptyRawArguments.join("_").isEmpty() shouldBe true
    }

    test("Size of sub arguments starting from 0 to 0 returns 0") {
        EmptyRawArguments.subArguments(0, 0).size shouldBe 0
    }

    test("Sub arguments starting from non-zero to non-zero fails") {
        shouldThrow<IndexOutOfBoundsException> { EmptyRawArguments[-1 until 0] }
        shouldThrow<IllegalArgumentException> { EmptyRawArguments[0 until -1] }
        shouldThrow<IndexOutOfBoundsException> { EmptyRawArguments[0 until 1] }
        shouldThrow<IllegalArgumentException> { EmptyRawArguments[1 until 0] }
    }

    test("Size of arguments limited to 0 is 0") {
        EmptyRawArguments.withLength(0).size shouldBe 0
    }

    test("Limit by non-zero value fails") {
        shouldThrow<IllegalArgumentException> { EmptyRawArguments.withLength(-1) }
        shouldThrow<IndexOutOfBoundsException> { EmptyRawArguments.withLength(1) }
    }

    test("Size of arguments shifted by 0 is 0"){
        EmptyRawArguments.withOffset(0).size shouldBe 0
    }

    test("Shift by non-zero value fails") {
        shouldThrow<IndexOutOfBoundsException> { EmptyRawArguments.withOffset(-1) }
        shouldThrow<IllegalArgumentException> { EmptyRawArguments.withOffset(1) }
    }

})