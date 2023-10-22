package w.commander.path;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import w.commander.spec.path.CommandHandlerPathStrategies;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author whilein
 */
class CommandHandlerPathStrategyTest {

    @Test
    @DisplayName("UpperSnakeCase")
    public void testUpperSnakeCase() {
        val strategy = CommandHandlerPathStrategies.upperSnakeCase();
        assertEquals("A_B_C", strategy.getPath(Arrays.asList("a", "b", "c")));
        assertEquals("A_C", strategy.getPath(Arrays.asList("a", "c")));
    }

    @Test
    @DisplayName("LowerSnakeCase")
    public void testLowerSnakeCase() {
        val strategy = CommandHandlerPathStrategies.lowerSnakeCase();
        assertEquals("a_b_c", strategy.getPath(Arrays.asList("A", "B", "C")));
        assertEquals("a_c", strategy.getPath(Arrays.asList("A", "", "C")));
    }

}