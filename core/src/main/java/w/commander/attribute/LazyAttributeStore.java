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

package w.commander.attribute;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * @author _Novit_ (novitpw)
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LazyAttributeStore implements AttributeStore {

    AttributeStoreFactory factory;

    @NonFinal
    volatile AttributeStore delegate;

    @Delegate(types = AttributeStore.class)
    private AttributeStore delegate() {
        AttributeStore as;
        if ((as = this.delegate) == null) {
            synchronized (this) {
                if ((as = this.delegate) == null) {
                    as = this.delegate = factory.create();
                }
            }
        }

        return as;
    }

}
