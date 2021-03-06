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
package de.marabs.common.shell;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Token associates index of a token in the input line with the token itself, in order to be able to provide helpful
 * error indication.
 *
 * @author Martin Absmeier
 */
@EqualsAndHashCode
public class Token implements Serializable {
    private static final long serialVersionUID = 3971371050497827764L;

    private static final int WHITESPACE = 0;
    private static final int WORD = 1;
    private static final int STRINGDQ = 2;
    private static final int STRINGSQ = 3;
    private static final int COMMENT = 4;

    @Getter
    @EqualsAndHashCode.Exclude
    private final int index;
    @Getter
    private final String string;

    public Token(int index, String string) {
        this.index = index;
        this.string = string;
    }

    /**
     * Escape given string so that tokenize(escapeString(str)).get(0).getString === str.
     *
     * @param input String to be escaped
     * @return escaped string
     */
    public static String escapeString(String input) {
        StringBuilder escaped = new StringBuilder(input.length() + 10);
        escaped.append('"');
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '"') {
                escaped.append("\"\"");
            } else {
                escaped.append(input.charAt(i));
            }
        }
        escaped.append('"');
        return escaped.toString();
    }

    @Override
    public String toString() {
        return (string != null ? string : "(null)") + ":" + index;
    }

    /**
     * State machine input string tokenizer.
     *
     * @param input String to be tokenized
     * @return List of tokens
     * @see Token
     */
    /*package-private for tests*/
    static List<Token> tokenize(final String input) {
        if (Objects.isNull(input)) {
            return Collections.emptyList();
        }

        List<Token> result = new ArrayList<>();

        int state = WHITESPACE;
        int tokenIndex = -1;

        StringBuilder token = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i); // character in hand
            switch (state) {
                case WHITESPACE:
                    // At start state is whitespace -> process all others characters
                    if (!Character.isWhitespace(ch)) {
                        tokenIndex = i;
                        if (Character.isLetterOrDigit(ch) || ch == '_') {
                            state = WORD;
                            token.append(ch);
                        } else if (ch == '"') {
                            state = STRINGDQ;
                        } else if (ch == '\'') {
                            state = STRINGSQ;
                        } else if (ch == '#') {
                            state = COMMENT;
                        } else {
                            state = WORD;
                            token.append(ch);
                        }
                    }
                    break;

                case WORD:
                    if (Character.isWhitespace(ch)) {
                        // submit token
                        result.add(new Token(tokenIndex, token.toString()));
                        token.setLength(0);
                        state = WHITESPACE;
                    } else if (Character.isLetterOrDigit(ch) || ch == '_') {
                        token.append(ch); // and keep state
                    } else if (ch == '"') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '"') {
                            // Yes, it's somewhat wrong in terms of statemachine, but it's the simplest and clearest way.
                            token.append('"');
                            i++;
                            // and keep state.
                        } else {
                            state = STRINGDQ; // but don't append; a"b"c is the same as abc.
                        }
                    } else if (ch == '\'') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '\'') {
                            // Yes, it's somewhat wrong in terms of statemachine, but it's the simplest and clearest way.
                            token.append('\'');
                            i++;
                            // and keep state.
                        } else {
                            state = STRINGSQ; // but don't append; a"b"c is the same as abc.
                        }
                    } else if (ch == '#') {
                        // submit token
                        result.add(new Token(tokenIndex, token.toString()));
                        token.setLength(0);
                        state = COMMENT;
                    } else {
                        // for now, we do allow special chars in words
                        token.append(ch);
                    }
                    break;

                case STRINGDQ:
                    if (ch == '"') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '"') {
                            token.append('"');
                            i++;
                            // and keep state
                        } else {
                            state = WORD;
                        }
                    } else {
                        token.append(ch);
                    }
                    break;

                case STRINGSQ:
                    if (ch == '\'') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '\'') {
                            token.append('\'');
                            i++;
                            // and keep state
                        } else {
                            state = WORD;
                        }
                    } else {
                        token.append(ch);
                    }
                    break;

                case COMMENT:
                    // eat ch
                    break;

                default:
                    assert false : "Unknown state in Shell.tokenize() state machine";
                    break;
            }
        }

        if (state == WORD || state == STRINGDQ || state == STRINGSQ) {
            result.add(new Token(tokenIndex, token.toString()));
        }

        return result;
    }
}