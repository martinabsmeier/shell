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

import de.marabs.common.shell.util.ArrayHashMultiMap;
import de.marabs.common.shell.util.EmptyMultiMap;
import de.marabs.common.shell.util.MultiMap;

import java.util.ArrayList;
import java.util.List;

public final class ShellFactory {

    /**
     * One of facade methods for operating the Shell.
     * <p>
     * Run the obtained Shell with commandLoop().
     *
     * @param prompt   Prompt to be displayed
     * @param appName  The app name string
     * @param handlers Command handlers
     * @return Shell that can be either further customized or run directly by calling commandLoop().
     */
    public static Shell createConsoleShell(String prompt, String appName, Object... handlers) {
        ConsoleIO io = new ConsoleIO();

        List<String> path = new ArrayList<>(1);
        path.add(prompt);

        MultiMap<String, Object> modifAuxHandlers = new ArrayHashMultiMap<>();
        modifAuxHandlers.put("!", io);

        ShellConfig config = ShellConfig.builder().input(io).output(io).auxHandlers(modifAuxHandlers).displayTime(false).build();
        Shell theShell = new Shell(config, new CommandTable(new DashJoinedNamer(true)), path);
        theShell.setAppName(appName);

        theShell.addMainHandler(theShell, "!");
        theShell.addMainHandler(new HelpCommandHandler(), "?");
        for (Object h : handlers) {
            theShell.addMainHandler(h, "");
        }

        return theShell;
    }

    /**
     * Facade method for operating the Shell allowing specification of auxiliary
     * handlers (i.e. handlers that are to be passed to all subshells).
     * <p>
     * Run the obtained Shell with commandLoop().
     *
     * @param prompt      Prompt to be displayed
     * @param appName     The app name string
     * @param mainHandler Main command handler
     * @param auxHandlers Aux handlers to be passed to all subshells.
     * @return Shell that can be either further customized or run directly by calling commandLoop().
     */
    public static Shell createConsoleShell(String prompt, String appName, Object mainHandler,
                                           MultiMap<String, Object> auxHandlers) {
        ConsoleIO io = new ConsoleIO();

        List<String> path = new ArrayList<>(1);
        path.add(prompt);

        MultiMap<String, Object> modifAuxHandlers = new ArrayHashMultiMap<>(auxHandlers);
        modifAuxHandlers.put("!", io);

        ShellConfig config = ShellConfig.builder().input(io).output(io).auxHandlers(modifAuxHandlers).displayTime(false).build();
        Shell theShell = new Shell(config, new CommandTable(new DashJoinedNamer(true)), path);
        theShell.setAppName(appName);

        theShell.addMainHandler(theShell, "!");
        theShell.addMainHandler(new HelpCommandHandler(), "?");
        theShell.addMainHandler(mainHandler, "");

        return theShell;
    }

    /**
     * Facade method for operating the Shell.
     * <p>
     * Run the obtained Shell with commandLoop().
     *
     * @param prompt      Prompt to be displayed
     * @param appName     The app name string
     * @param mainHandler Command handler
     * @return Shell that can be either further customized or run directly by calling commandLoop().
     */
    public static Shell createConsoleShell(String prompt, String appName, Object mainHandler) {
        return createConsoleShell(prompt, appName, mainHandler, new EmptyMultiMap<>());
    }

    /**
     * Facade method facilitating the creation of subshell.
     * Subshell is created and run inside Command method and shares the same IO and naming strategy.
     * <p>
     * Run the obtained Shell with commandLoop().
     *
     * @param pathElement sub-prompt
     * @param parent      Shell to be subshell'd
     * @param appName     The app name string
     * @param mainHandler Command handler
     * @param auxHandlers Aux handlers to be passed to all subshells.
     * @return subshell
     */
    public static Shell createSubshell(String pathElement, Shell parent, String appName, Object mainHandler,
                                       MultiMap<String, Object> auxHandlers) {

        List<String> newPath = new ArrayList<>(parent.getPath());
        newPath.add(pathElement);

        Shell subshell = new Shell(parent.getShellConfig().createWithAddedAuxHandlers(auxHandlers),
            new CommandTable(parent.getCommandTable().getNamer()), newPath);

        subshell.setAppName(appName);
        subshell.addMainHandler(subshell, "!");
        subshell.addMainHandler(new HelpCommandHandler(), "?");

        subshell.addMainHandler(mainHandler, "");
        return subshell;
    }

    /**
     * Facade method facilitating the creation of subshell.
     * Subshell is created and run inside Command method and shares the same IO and naming strtategy.
     * <p>
     * Run the obtained Shell with commandLoop().
     *
     * @param pathElement sub-prompt
     * @param parent      Shell to be subshell'd
     * @param appName     The app name string
     * @param mainHandler Command handler
     * @return subshell
     */
    public static Shell createSubshell(String pathElement, Shell parent, String appName, Object mainHandler) {
        return createSubshell(pathElement, parent, appName, mainHandler, new EmptyMultiMap<>());
    }

    // #################################################################################################################
    private ShellFactory() {
        // this class has only static methods.
    }
}