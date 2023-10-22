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

package w.commander;

import org.jetbrains.annotations.NotNull;
import w.commander.error.ErrorResultFactory;
import w.commander.execution.ExecutionContextFactory;
import w.commander.executor.HandlerPathFactory;
import w.commander.manual.DescriptionFormatter;
import w.commander.manual.ManualFactory;
import w.commander.manual.ManualFormatter;
import w.commander.manual.UsageFormatter;

import java.util.concurrent.Executor;

/**
 * @author whilein
 */
public interface CommanderSetupRead {

    @NotNull AnnotationScanner annotationScanner();

    @NotNull HandlerPathFactory handlerPathFactory();

    @NotNull ErrorResultFactory errorResultFactory();

    @NotNull ExecutionContextFactory executionContextFactory();

    @NotNull ManualFactory manualFactory();

    @NotNull ManualFormatter manualFormatter();

    @NotNull UsageFormatter usageFormatter();

    @NotNull DescriptionFormatter descriptionFormatter();

    @NotNull CommandRegistrar commandRegistrar();

    @NotNull Executor asyncExecutor();

    boolean includeDefaultArgumentParsers();

    boolean includeDefaultArgumentValidators();

}
