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

package w.commander.tabcomplete;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import w.commander.execution.ExecutionContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author whilein
 */
final class TabCompleterTests {

    @AfterEach
    void cleanup() {
        Mockito.clearAllCaches();
    }

    @Test
    void suggestionsThreadSafe() {
        val scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        val ctx = mock(ExecutionContext.class);

        val cf = new CompletableFuture<List<String>>();
        val suggestions = new Suggestions(cf);

        val tc = new TabCompleter() {

            @Override
            public void getSuggestions(
                    @NotNull ExecutionContext ctx,
                    @NotNull String value,
                    @NotNull Suggestions suggestions
            ) {
                suggestions.retain();

                scheduler.schedule(() -> {
                    suggestions.next(value);
                    suggestions.release();
                }, 1, TimeUnit.SECONDS);
            }
        };

        try {
            for (int i = 0; i < 1_000_000; i++) {
                suggestions.retain();

                scheduler.execute(() -> {
                    try {
                        tc.getSuggestions(ctx, "foobarbaz", suggestions);
                    } finally {
                        suggestions.release();
                    }
                });
            }
        } finally {
            suggestions.release();
        }

        assertFalse(cf.isDone());
        val result = assertDoesNotThrow(() -> cf.get(5, TimeUnit.SECONDS));
        assertEquals(1_000_000, result.size());

        scheduler.shutdown();
    }

    @Test
    void explicit() {
        val tc = new ExplicitTabCompleter(Arrays.asList("foo", "bar", "baz"));

        CompletableFuture<List<String>> r;

        r = tabComplete(tc, "f");
        assertTrue(r.isDone());
        assertEquals(Collections.singletonList("foo"), r.join());

        r = tabComplete(tc, "ba");
        assertTrue(r.isDone());
        assertEquals(Arrays.asList("bar", "baz"), r.join());

        r = tabComplete(tc, "baz");
        assertTrue(r.isDone());
        assertEquals(Collections.singletonList("baz"), r.join());

        r = tabComplete(tc, "q");
        assertTrue(r.isDone());
        assertEquals(Collections.emptyList(), r.join());
    }

    private CompletableFuture<List<String>> tabComplete(TabCompleter tabCompleter, String input) {
        val cf = new CompletableFuture<List<String>>();
        val suggestions = new Suggestions(cf);
        try {
            tabCompleter.getSuggestions(
                    mock(),
                    input,
                    suggestions
            );
        } finally {
            suggestions.release();
        }
        return cf;
    }

}
