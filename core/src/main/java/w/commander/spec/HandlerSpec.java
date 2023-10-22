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

package w.commander.spec;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import w.commander.condition.Conditions;
import w.commander.decorator.Decorators;
import w.commander.executor.HandlerPath;
import w.commander.manual.ManualEntry;
import w.commander.parameter.HandlerParameters;

import java.lang.reflect.Method;

/**
 * @author whilein
 */
@Value
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HandlerSpec implements PathAwareSpec {

    // PathAwareSpec
    HandlerPath path;

    @ToString.Exclude
    CommandSpec command;

    HandlerParameters parameters;
    Method method;

    ManualEntry manualEntry;

    Conditions conditions;
    Decorators decorators;

}
