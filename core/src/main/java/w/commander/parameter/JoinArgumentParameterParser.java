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

import lombok.val;
import org.jetbrains.annotations.NotNull;
import w.commander.annotation.Join;
import w.commander.parameter.argument.type.JoinArgument;

import java.lang.reflect.Parameter;

/**
 * @author whilein
 */
public class JoinArgumentParameterParser extends AnnotatedParameterParser<Join> {

    public JoinArgumentParameterParser() {
        super(Join.class);
    }

    @Override
    protected @NotNull HandlerParameter parse(@NotNull Parameter parameter, @NotNull Join annotation) {
        val type = parameter.getType();
        if (type != String.class) {
            throw new IllegalArgumentException("@Join annotation is not allowed on " + type.getName());
        }

        return new JoinArgument(
                annotation.value(),
                annotation.delimiter()
        );
    }

}
