/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.marabs.common.shell;

import de.marabs.common.shell.CommandNamer.NamingInfo;
import de.marabs.common.shell.annotation.Command;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author ASG
 */
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