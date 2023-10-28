package w.commander.tests

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import w.commander.kt.EmptyRawCommandArguments
import w.commander.kt.RawCommandArguments.size
import w.commander.kt.RawCommandArguments.get

/**
 * @author whilein
 */
class EmptyRawCommandArgumentsTests : FunSpec({

    test("Is empty") {
        EmptyRawCommandArguments.isEmpty shouldBe true
    }

    test("Size is 0") {
        EmptyRawCommandArguments.size shouldBe 0
    }

    test("Join returns empty string") {
        EmptyRawCommandArguments.join("").isEmpty() shouldBe true
        EmptyRawCommandArguments.join(" ").isEmpty() shouldBe true
        EmptyRawCommandArguments.join("_").isEmpty() shouldBe true
    }

    test("Size of sub arguments starting from 0 to 0 returns 0") {
        EmptyRawCommandArguments.subArguments(0, 0).size shouldBe 0
    }

    test("Sub arguments starting from non-zero to non-zero fails") {
        shouldThrow<IndexOutOfBoundsException> { EmptyRawCommandArguments[-1 until 0] }
        shouldThrow<IllegalArgumentException> { EmptyRawCommandArguments[0 until -1] }
        shouldThrow<IndexOutOfBoundsException> { EmptyRawCommandArguments[0 until 1] }
        shouldThrow<IllegalArgumentException> { EmptyRawCommandArguments[1 until 0] }
    }

    test("Size of arguments limited to 0 is 0") {
        EmptyRawCommandArguments.withLength(0).size shouldBe 0
    }

    test("Limit by non-zero value fails") {
        shouldThrow<IllegalArgumentException> { EmptyRawCommandArguments.withLength(-1) }
        shouldThrow<IndexOutOfBoundsException> { EmptyRawCommandArguments.withLength(1) }
    }

    test("Size of arguments shifted by 0 is 0"){
        EmptyRawCommandArguments.withOffset(0).size shouldBe 0
    }

    test("Shift by non-zero value fails") {
        shouldThrow<IndexOutOfBoundsException> { EmptyRawCommandArguments.withOffset(-1) }
        shouldThrow<IllegalArgumentException> { EmptyRawCommandArguments.withOffset(1) }
    }

})