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

package w.commander.decorator;

import java.lang.reflect.AnnotatedElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author _Novit_ (novitpw)
 */
public abstract class TypeDecoratorFactory implements DecoratorFactory {

    @Override
    public boolean isSupported(@NotNull AnnotatedElement annotatedElement) {
        return annotatedElement instanceof Class<?> && isSupported((Class<?>) annotatedElement);
    }

    @Override
    public @NotNull Decorator create(@NotNull AnnotatedElement annotatedElement) {
        return create((Class<?>) annotatedElement);
    }

    protected abstract boolean isSupported(@NotNull Class<?> type);

    protected abstract @NotNull Decorator create(@NotNull Class<?> type);

}
