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

package w.commander.decorator.type;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import w.commander.CommanderConfig;
import w.commander.annotation.Async;
import w.commander.decorator.AnnotationDecoratorFactory;
import w.commander.decorator.Decorator;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncDecoratorFactory extends AnnotationDecoratorFactory<Async> {

    CommanderConfig config;

    public AsyncDecoratorFactory(CommanderConfig config) {
        super(Async.class);

        this.config = config;
    }

    @Override
    protected @NotNull Decorator create(@NotNull Async annotation) {
        return new AsyncDecorator(config);
    }

}
