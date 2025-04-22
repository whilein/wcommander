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

package w.commander.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Вспомогательный класс для сбора нескольких упорядоченных по индексу {@link Callback}'ов в
 * единый {@link Callback}.
 *
 * @author whilein
 */
@ThreadSafe
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CallbackArrayCollector<T> {

    Callback<List<T>> completion;
    AtomicReferenceArray<T> store;
    AtomicInteger remaining;

    public CallbackArrayCollector(Callback<List<T>> completion, int count) {
        this.completion = completion;
        this.store = new AtomicReferenceArray<>(count);
        this.remaining = new AtomicInteger(count);
    }

    public Callback<T> element(int index) {
        return Callback.of((result, cause) -> {
            if (cause != null && remaining.getAndSet(0) > 0) {
                completion.completeExceptionally(cause);
                return;
            }

            val remaining = this.remaining.decrementAndGet();
            if (remaining < 0) return;

            store.set(index, result);

            if (remaining == 0) {
                val length = store.length();
                val list = new ArrayList<T>(length);
                for (int i = 0; i < length; i++) {
                    list.add(store.get(i));
                }
                completion.complete(list);
            }
        });
    }

}
