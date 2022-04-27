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
package de.marabs.common.shell.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test cases of class {@link Strings}.
 *
 * @author Martin Absmeier
 */
public class StringsTest {

    /**
     * Test of fixCase method, of class Strings.
     */
    @Test
    public void testFixCase() {
        String[] cases = {"", "a", "A", "Abc", "ABC", "ABc"};
        String[] results = {"", "a", "a", "abc", "ABC", "ABc"};

        for (int i = 0; i < cases.length; i++) {
            String expected = results[i];
            String actual = Strings.fixCase(cases[i]);
            assertEquals(expected, actual);
        }
    }

    /**
     * Test of joinStrings method, of class Strings.
     */
    @Test
    public void testJoinStrings() {
        String[] cases = {"a", "a|b|c", ""};
        String[] results = {"a", "a-b-c", ""};

        for (int i = 0; i < cases.length; i++) {
            String expected = results[i];
            String actual = Strings.joinStrings(Arrays.asList(cases[i].split("\\|")), false, '-');
            assertEquals(expected, actual);
        }
    }

    /**
     * Test of splitJavaIdentifier method, of class Strings.
     */
    @Test
    public void testSplitJavaIdentifier() {
        String[] cases = {"", "void", "splitJavaIdentifier", "NUnit", "feedURL", "ConvertURLClass"};
        String[] dashJoined = {"", "void", "split-java-identifier", "n-unit", "feed-URL", "convert-URL-class"};

        for (int i = 0; i < cases.length; i++) {
            String expected = dashJoined[i];
            String actual = Strings.joinStrings(Strings.splitJavaIdentifier(cases[i]), true, '-');
            assertEquals(expected, actual);
        }
    }
}