/*
 *    Copyright 2024 Whilein
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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Класс собирающий подсказки для команды. После завершения сбора передает все
 * подсказки в {@link #completion}.
 *
 * @author whilein
 */
@ThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class Suggestions {

    private static final AtomicIntegerFieldUpdater<Suggestions> REF_COUNT_UPDATER
            = AtomicIntegerFieldUpdater.newUpdater(Suggestions.class, "refCount");

    List<String> suggestions = new ArrayList<>();
    CompletableFuture<List<String>> completion;

    @NonFinal
    volatile int refCount = 1;

    public void retain() {
        REF_COUNT_UPDATER.incrementAndGet(this);
    }

    public void release() {
        if (REF_COUNT_UPDATER.decrementAndGet(this) == 0) {
            completion.complete(getSuggestions());
        }
    }

    public void exceptionCaught(Throwable cause) {
        if (REF_COUNT_UPDATER.getAndSet(this, 0) != 0) {
            completion.completeExceptionally(cause);
        }
    }

    private synchronized List<String> getSuggestions() {
        return new ArrayList<>(suggestions);
    }

    public synchronized void next(@NotNull String suggestion) {
        this.suggestions.add(suggestion);
    }

    public synchronized void next(@NotNull Collection<String> suggestions) {
        this.suggestions.addAll(suggestions);
    }

}
