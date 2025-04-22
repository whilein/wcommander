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

package w.commander.spec;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import w.commander.CommandInfo;
import w.commander.executor.HandlerPath;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * @author whilein
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandSpec implements NameAwareSpec, PathAwareSpec {
    String fullName;

    // NameAwareSpec
    String name;
    List<String> aliases;

    // PathAwareSpec
    HandlerPath path;

    CommandSpec parent;
    CommandInfo info;

    ManualSpec manual;

    @Setter
    @NonFinal
    List<SetupHandlerSpec> setupHandlers;

    @Setter
    @NonFinal
    List<HandlerSpec> handlers;

    @Setter
    @NonFinal
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<CommandSpec> subCommands;

    public MethodHandles.Lookup getLookup() {
        return info.getLookup();
    }

    public Object getInstance() {
        return info.getInstance();
    }

    public Class<?> getType() {
        return info.getInstanceType();
    }

}
