/*
 * Copyright 2022 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.marabs.common.shell.refactored;

import de.marabs.common.shell.refactored.command.ShellCommandTable;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

/**
 * {@code ShellFactory} .....
 *
 * @author Martin Absmeier
 */
public class ShellFactory {

    public static Shell createShell(String prompt, String appName, Object... handlers) {
        ShellConfig config = ShellConfig.builder().isLoggingEnabled(false).displayTime(false).build();
        ShellCommandTable commands = ShellCommandTable.builder().build();

        Shell.ShellBuilder builder = Shell.builder();
        if (isNull(handlers)) {
            builder.config(config).commands(commands);
        } else {
            builder.config(config).commands(commands).handlers(asList(handlers));
        }
        return builder.build();
    }
}