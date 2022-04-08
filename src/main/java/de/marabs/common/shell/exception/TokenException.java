/*
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
 * Exception pointing at the token which caused it.
 * Used to report invalid parameter types.
 *
 * @author Martin Absmeier
 */
public class TokenException extends CliException {
    private static final long serialVersionUID = -4572281644389173784L;

    private final Token token;

    public Token getToken() {
        return token;
    }

    public TokenException(Token token) {
        this.token = token;
    }

    public TokenException(Token token, String message) {
        super(message);
        this.token = token;
    }

    public TokenException(Token token, Throwable cause) {
        super(cause);
        this.token = token;
    }

    public TokenException(Token token, String message, Throwable cause) {
        super(message, cause);
        this.token = token;
    }
}