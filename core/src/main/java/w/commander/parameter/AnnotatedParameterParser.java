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

package w.commander.parameter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public abstract class AnnotatedParameterParser<A extends Annotation> implements ParameterParser {

    Class<? extends A> annotationType;

    @Override
    public final boolean matches(@NotNull Parameter parameter) {
        return parameter.isAnnotationPresent(annotationType);
    }

    @Override
    public final @NotNull HandlerParameter parse(@NotNull Parameter parameter) {
        return parse(parameter, parameter.getAnnotation(annotationType));
    }

    protected abstract @NotNull HandlerParameter parse(@NotNull Parameter parameter, @NotNull A annotation);

}
