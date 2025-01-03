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

package plugin;

import w.commander.annotation.Command;
import w.commander.annotation.CommandHandler;
import w.commander.result.Result;
import w.commander.result.Results;

/**
 * @author whilein
 */
@Command("test_protected")
final class TestProtectedCommand {

    @CommandHandler
    public Result run() {
        return Results.ok("foo");
    }

}