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

package w.commander.parameter.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import w.commander.annotation.Attr;
import w.commander.execution.ExecutionContext;
import w.commander.parameter.AnnotatedParameterParser;
import w.commander.parameter.HandlerParameter;
import w.commander.parameter.argument.cursor.ArgumentCursor;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
public class AttributeParameterParser extends AnnotatedParameterParser<Attr> {
    public AttributeParameterParser() {
        super(Attr.class);
    }

    @Override
    protected @NotNull HandlerParameter parse(@NotNull Parameter parameter, @NotNull Attr annotation) {
        return new ParameterImpl(parameter.getType());
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static class ParameterImpl implements HandlerParameter {

        Class<?> type;

        @Override
        public @Nullable Object extract(@NotNull ExecutionContext context, @NotNull ArgumentCursor cursor) {
            return context.getAttributeStore().getAttribute(type);
        }
    }

}
