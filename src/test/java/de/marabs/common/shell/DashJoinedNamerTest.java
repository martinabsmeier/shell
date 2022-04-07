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

import de.marabs.common.shell.CommandNamer.NamingInfo;
import de.marabs.common.shell.annotation.Command;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DashJoinedNamerTest {

    @Test
    public void testNameCommand() throws NoSuchMethodException {
        System.out.println("nameCommand");
        CommandNamer namer = new DashJoinedNamer(true);
        Method[] methods = new Method[]{
            TestClass.class.getMethod("cliTestMethod1"),
            TestClass.class.getMethod("cmdTestMethod2"),
        };

        CommandNamer.NamingInfo[] expecteds = new CommandNamer.NamingInfo[]{
            new NamingInfo("test-method-1", new String[]{"tm1", "teme1"}),
            new NamingInfo("test-method-2", new String[]{"tm2", "teme2"}),
        };
        CommandNamer.NamingInfo[] results = new NamingInfo[methods.length];
        for (int i = 0; i < methods.length; i++) {
            results[i] = namer.nameCommand(methods[i]);
        }

        for (int i = 0; i < methods.length; i++) {
            System.out.println(results[i]);
            assertEquals(expecteds[i].commandName, results[i].commandName);
            assertArrayEquals(expecteds[i].possibleAbbreviations, results[i].possibleAbbreviations);
        }
    }

    private static class TestClass {
        @Command
        public void cliTestMethod1() {
        }

        @Command
        public void cmdTestMethod2() {
        }
    }
}