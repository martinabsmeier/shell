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

public class StringsTest {

    /**
     * Test of fixCase method, of class Strings.
     */
    @Test
    public void testFixCase() {
        System.out.println("fixCase");

        String[] cases = {"", "a", "A", "Abc", "ABC", "ABc"};
        String[] results = {"", "a", "a", "abc", "ABC", "ABc"};

        for (int i = 0; i < cases.length; i++) {
            assertEquals(results[i], Strings.fixCase(cases[i]));
        }
    }

    /**
     * Test of joinStrings method, of class Strings.
     */
    @Test
    public void testJoinStrings() {
        System.out.println("JoinStrings");

        String[] cases = {"a", "a|b|c", ""};
        String[] results = {"a", "a-b-c", ""};

        for (int i = 0; i < cases.length; i++) {
            assertEquals(results[i], Strings.joinStrings(Arrays.asList(results[i].split("\\|")), false, '-'));
        }
    }

    /**
     * Test of splitJavaIdentifier method, of class Strings.
     */
    @Test
    public void testSplitJavaIdentifier() {
        System.out.println("splitJavaIdentifier");

        String[] cases = {"", "void", "splitJavaIdentifier", "NUnit", "feedURL", "ConvertURLClass"};
        String[] dashJoined = {"", "void", "split-java-identifier", "n-unit", "feed-URL", "convert-URL-class"};

        for (int i = 0; i < cases.length; i++) {
            assertEquals(dashJoined[i], Strings.joinStrings(Strings.splitJavaIdentifier(cases[i]), true, '-'));
        }
    }
}