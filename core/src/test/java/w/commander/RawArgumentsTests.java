/*
 *    Copyright 2025 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package w.commander;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author whilein
 */
class RawArgumentsTests {

    @Test
    void trustedModifications() {
        val list = new ArrayList<String>();

        val args = RawArguments.fromTrustedList(list);
        assertEquals(0, args.size());

        list.add("");

        // expected behavior due to docs
        assertEquals(1, args.size());
    }

    @Test
    void copiedModifications() {
        val list = new ArrayList<String>();

        val args = RawArguments.fromList(list);
        assertEquals(0, args.size());

        list.add("");

        // expected behavior due to docs
        assertEquals(0, args.size());
    }

    @Test
    void rangedTrustedArray() {
        rangedArray(RawArguments::fromTrustedArray);
    }

    @Test
    void rangedArray() {
        rangedArray(RawArguments::fromArray);
    }

    @Test
    void equalsAndHashCodeAndToString() {
        RawArguments[] comparisons = {
                RawArguments.fromArray("1"), RawArguments.fromArray("1"),
                RawArguments.fromArray("1"), RawArguments.fromList(Collections.singletonList("1")),
                RawArguments.fromList(Collections.emptyList()), RawArguments.empty(),
                RawArguments.fromArray(), RawArguments.empty()
        };

        for (int i = 0; i < comparisons.length; i += 2) {
            val a = comparisons[i];
            val b = comparisons[i + 1];
            assertEquals(a, b);
            assertEquals(a.toString(), b.toString());
            assertEquals(a.hashCode(), b.hashCode());
        }
    }

    private void rangedArray(RangedFactory factory) {
        val fiveNumbers = new String[]{"1", "2", "3", "4", "5"};

        RawArguments args;

        // all
        args = factory.create(fiveNumbers, 0, 5);
        assertEquals(5, args.size());
        assertEquals("1 2 3 4 5", args.join(" "));

        // empty
        args = factory.create(fiveNumbers, 0, 0);
        assertEquals(0, args.size());
        assertEquals("", args.join(" "));

        // without first and last
        args = factory.create(fiveNumbers, 1, 3);
        assertEquals(3, args.size());
        assertEquals("2 3 4", args.join(" "));

        // without first
        args = factory.create(fiveNumbers, 1, 4);
        assertEquals(4, args.size());
        assertEquals("2 3 4 5", args.join(" "));

        // without last
        args = factory.create(fiveNumbers, 0, 4);
        assertEquals(4, args.size());
        assertEquals("1 2 3 4", args.join(" "));

        // illegal
        assertThrows(IndexOutOfBoundsException.class, () -> factory.create(fiveNumbers, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> factory.create(fiveNumbers, 0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> factory.create(fiveNumbers, 6, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> factory.create(fiveNumbers, 0, 6));
    }

    @FunctionalInterface
    private interface RangedFactory {
        RawArguments create(String[] args, int off, int len);
    }

}
