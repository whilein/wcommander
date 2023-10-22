package w.commander;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author whilein
 */
abstract class RawCommandArgumentsTest {

    public abstract RawCommandArguments create(String... arguments);

    @DisplayName("OfList")
    public static class OfList extends RawCommandArgumentsTest {

        @Override
        public RawCommandArguments create(String... arguments) {
            return RawCommandArguments.fromList(Arrays.asList(arguments));
        }

        @Test
        @DisplayName("Initiation")
        public void testInitiation() {
            val args = RawCommandArguments.fromList(Arrays.asList("1", "2"));
            assertNotNull(args);
        }

    }

    @DisplayName("OfArray")
    public static class OfArray extends RawCommandArgumentsTest {

        @Override
        public RawCommandArguments create(String... arguments) {
            return RawCommandArguments.fromArray(arguments);
        }

        @Nested
        @DisplayName("Initiation")
        class Initiation {

            @Test
            @DisplayName("Unbound")
            public void testInitiation() {
                val args = RawCommandArguments.fromArray(new String[]{"1", "2"});
                assertNotNull(args);
            }

            @Test
            @DisplayName("Slice Out Bounds")
            public void testInitiationSliceOutBounds() {
                val array = new String[]{"1"};

                assertThrows(IllegalArgumentException.class,
                        () -> RawCommandArguments.fromArray(array, 0, 2));
                assertThrows(IllegalArgumentException.class,
                        () -> RawCommandArguments.fromArray(array, 2, 0));
                assertThrows(IllegalArgumentException.class,
                        () -> RawCommandArguments.fromArray(array, -1, 1));
                assertThrows(IllegalArgumentException.class,
                        () -> RawCommandArguments.fromArray(array, 0, -1));
            }

            @Test
            @DisplayName("Slice In Bounds")
            public void testInitiationSliceInBounds() {
                val args = RawCommandArguments.fromArray(new String[]{"1", "2"}, 1, 1);
                assertNotNull(args);
            }

        }

    }

    @Nested
    @DisplayName("join()")
    public class JoinTest {

        @Test
        @DisplayName("All")
        public void testAll() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals("12345", args.join(""));
        }

        @Test
        @DisplayName("From")
        public void testFrom() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals("345", args.join("", 2));
        }

        @Test
        @DisplayName("From and To")
        public void testFromAndTo() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals("34", args.join("", 2, 4));
        }

        @Test
        @DisplayName("Out Bounds")
        public void testOutBounds() {
            val args = create("1", "2", "3", "4", "5");
            assertThrows(IllegalArgumentException.class, () -> args.join("", 6));
            assertThrows(IndexOutOfBoundsException.class, () -> args.join("", 1, 6));
            assertThrows(IndexOutOfBoundsException.class, () -> args.join("", -1, 2));
            assertThrows(IllegalArgumentException.class, () -> args.join("", 1, -2));
        }

        @Test
        @DisplayName("Sub Arguments")
        public void testSubArguments() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals("1", args.subArguments(0, 1).join(""));
            assertEquals("23", args.subArguments(1, 3).join(""));
            assertEquals("345", args.subArguments(2, 5).join(""));
        }
    }

    @Nested
    @DisplayName("withOffset()")
    public class WithOffsetTest {

        @Test
        @DisplayName("Out Bounds")
        public void testOutBounds() {
            val args = create("1");
            assertThrows(IllegalArgumentException.class, () -> args.withOffset(2));
            assertThrows(IndexOutOfBoundsException.class, () -> args.withOffset(-1));
        }

        @Test
        @DisplayName("Size")
        public void testSize() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals(5, args.withOffset(0).size());
            assertEquals(4, args.withOffset(1).size());
            assertEquals(3, args.withOffset(2).size());
            assertEquals(2, args.withOffset(3).size());
            assertEquals(1, args.withOffset(4).size());
            assertEquals(0, args.withOffset(5).size());
        }

        @Test
        @DisplayName("Value")
        public void testValue() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals("1", args.withOffset(0).value(0));
            assertEquals("2", args.withOffset(1).value(0));
            assertEquals("3", args.withOffset(2).value(0));
            assertEquals("4", args.withOffset(3).value(0));
            assertEquals("5", args.withOffset(4).value(0));
        }

    }

    @Nested
    @DisplayName("withLength()")
    public class WithLengthTest {
        @Test
        @DisplayName("Size")
        public void testSize() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals(0, args.withLength(0).size());
            assertEquals(1, args.withLength(1).size());
            assertEquals(2, args.withLength(2).size());
            assertEquals(3, args.withLength(3).size());
            assertEquals(4, args.withLength(4).size());
            assertEquals(5, args.withLength(5).size());
        }

        @Test
        @DisplayName("Out Bounds")
        public void testOutBounds() {
            val args = create("1");
            assertThrows(IndexOutOfBoundsException.class, () -> args.withLength(2));
            assertThrows(IllegalArgumentException.class, () -> args.withLength(-1));
        }
    }

    @Nested
    @DisplayName("value()")
    public class ValueTest {

        @Test
        @DisplayName("In Bounds")
        public void testInBounds() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals("1", args.value(0));
            assertEquals("2", args.value(1));
            assertEquals("3", args.value(2));
            assertEquals("4", args.value(3));
            assertEquals("5", args.value(4));
        }

        @Test
        @DisplayName("Out Bounds")
        public void testOutBounds() {
            val args = create("1", "2", "3");
            assertThrows(IndexOutOfBoundsException.class, () -> args.value(3));
            assertThrows(IndexOutOfBoundsException.class, () -> args.value(-1));
        }

    }

    @Nested
    @DisplayName("size()")
    public class SizeTest {

        @Test
        @DisplayName("All")
        public void testSize() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals(5, args.size());
        }

        @Test
        @DisplayName("Sub Arguments")
        public void testSubArguments() {
            val args = create("1", "2", "3", "4", "5");
            assertEquals(1, args.subArguments(0, 1).size());
            assertEquals(2, args.subArguments(1, 3).size());
            assertEquals(3, args.subArguments(2, 5).size());
        }

    }


}