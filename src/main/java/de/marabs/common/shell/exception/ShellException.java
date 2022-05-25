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
public class ShellException extends RuntimeException {
    private static final long serialVersionUID = -4432160786685693109L;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ShellException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.  <p>Note that the detail message
     * associated with {@code cause} is <i>not</i> automatically incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A {@code null}
     *                value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ShellException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified cause and a detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).  This constructor is useful for runtime
     * exceptions that are little more than wrappers for other throwable.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null}
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ShellException(Throwable cause) {
        super(cause);
    }

    public static ShellException createCommandNotFound(String commandName) {
        return new ShellException("Unknown command: ".concat(Token.escapeString(commandName)));
    }

    public static ShellException createCommandNotFoundForArgNum(String commandName, int argCount) {
        return new ShellException("There's no command ".concat(Token.escapeString(commandName))
                                    .concat(" taking " + argCount).concat(" arguments"));
    }

    public static ShellException createAmbiguousCommandExc(String commandName, int argCount) {
        return new ShellException("Ambiguous command ".concat(Token.escapeString(commandName))
                                    .concat(" taking " + argCount).concat(" arguments"));
    }
}