package w.commander.tests

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import w.commander.spec.path.HandlerPathStrategies

/**
 * @author whilein
 */
class HandlerPathStrategyTests : FunSpec({

    test("Upper snake case strategy") {
        val strategy = HandlerPathStrategies.upperSnakeCase()

        strategy.getPath(listOf("a", "b", "c")) shouldBe "A_B_C"
        strategy.getPath(listOf("a", "c")) shouldBe "A_C"
    }

    test("Lower snake case strategy") {
        val strategy = HandlerPathStrategies.lowerSnakeCase()

        strategy.getPath(listOf("a", "b", "c")) shouldBe "a_b_c"
        strategy.getPath(listOf("a", "c")) shouldBe "a_c"
    }

})