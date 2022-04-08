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
package de.marabs.common.shell.exception;

import de.marabs.common.shell.Token;

/**
 * Root exception for Cliche.
 *
 * @author Martin Absmeier
 */
public class CliException extends Exception {
    private static final long serialVersionUID = -4432160786685693109L;

    public CliException() {}

    public CliException(String message) {
        super(message);
    }

    public CliException(Throwable cause) {
        super(cause);
    }

    public CliException(String message, Throwable cause) {
        super(message, cause);
    }

    public static CliException createCommandNotFound(String commandName) {
        return new CliException("Unknown command: " + Token.escapeString(commandName));
    }

    public static CliException createCommandNotFoundForArgNum(String commandName, int argCount) {
        return new CliException("There's no command " + Token.escapeString(commandName)
            + " taking " + argCount + " arguments");
    }

    public static CliException createAmbiguousCommandExc(String commandName, int argCount) {
        return new CliException("Ambiguous command " + Token.escapeString(commandName)
            + " taking " + argCount + " arguments");
    }
}