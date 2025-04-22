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

import org.jetbrains.annotations.NotNull;
import w.commander.executor.MethodExecutor;
import w.commander.spec.HandlerSpec;
import w.commander.util.Ordered;

/**
 * @author whilein
 */
public interface Decorator extends Ordered {

    @NotNull
    MethodExecutor wrap(@NotNull HandlerSpec handler, @NotNull MethodExecutor delegate);

}
