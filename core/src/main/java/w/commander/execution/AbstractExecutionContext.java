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

package w.commander.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.CommandActor;
import w.commander.RawArguments;
import w.commander.attribute.AttributeStore;
import w.commander.attribute.SimpleAttributeStore;

/**
 * @author whilein
 */
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractExecutionContext<T extends CommandActor> implements ExecutionContext {

    @NotNull T actor;
    @NotNull RawArguments rawArguments;

    @NonFinal
    @Nullable
    volatile AttributeStore attributeStore;

    @Override
    public @NotNull AttributeStore getAttributeStore() {
        AttributeStore attributeStore = this.attributeStore;

        if (attributeStore == null) {
            synchronized (this) {
                attributeStore = this.attributeStore;

                if (attributeStore == null) {
                    return this.attributeStore = new SimpleAttributeStore();
                }
            }
        }

        return attributeStore;
    }

    @Override
    public void dispatch(@NotNull String text) {
        actor.sendMessage(text);
    }
}
