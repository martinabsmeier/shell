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

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenTest {

    /**
     * Test of tokenize method, of class Token.
     */
    @Test
    public void testTokenize() {
        System.out.println("tokenize");
        String[] cases = {
            "",
            "aSingleToken",
            "a b c",
            "an's g'ri # quotation test",
            "Shell instance = new Shell(new ShellTest(), System.out",
            "dir \"E:\\ASG\\!dynamic\\projects\" \t-l 3492.9  ",
            "a b c ''",
            " \"\" "
        };

        int[] sizes = {
            0, 1, 3, 1, 7, 4, 4, 1
        };

        for (int i = 0; i < cases.length; i++) {
            List<Token> result = Token.tokenize(cases[i]);
            System.out.println("case: " + cases[i]);
            for (Token t : result) {
                System.out.print(t + " ");
            }
            System.out.println();

            assertEquals(sizes[i], result.size());
        }
    }

    /**
     * Test of escapeString method, of class Token.
     */
    @Test
    public void testEscapeString() {
        System.out.println("escapeString");
        String[] cases = {
            "aSingleToken",
            "a b c",
            "an's g'ri # quotation test",
            "Shell instance = new Shell(new ShellTest(), System.out",
            "dir \"E:\\ASG\\!dynamic\\projects\" \t-l 3492.9  "
        };

        for (String aCase : cases) {
            String escaped = Token.escapeString(aCase);
            List<Token> result = Token.tokenize(escaped);
            System.out.println("case: " + aCase);
            System.out.println("escaped: " + escaped);

            assertEquals(1, result.size());
            assertEquals(aCase, result.get(0).getString());
        }
    }
}