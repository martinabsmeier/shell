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

import de.marabs.common.shell.annotation.Command;
import de.marabs.common.shell.annotation.Param;
import de.marabs.common.shell.exception.CliException;
import de.marabs.common.shell.exception.TokenException;
import de.marabs.common.shell.input.Input;
import de.marabs.common.shell.input.InputConversionEngine;
import de.marabs.common.shell.otput.Output;
import de.marabs.common.shell.otput.OutputConversionEngine;
import de.marabs.common.shell.util.ArrayHashMultiMap;
import de.marabs.common.shell.util.MultiMap;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Shell is the class interacting with user.
 * Provides the command loop.
 * All logic lies here.
 *
 * @author Martin Absmeier
 */
public class Shell {

    public static String PROJECT_HOMEPAGE_URL = "http://cliche.sourceforge.net";

    private Output output;
    private Input input;
    private String appName;

    public static class Settings {
        private final Input input;
        private final Output output;
        private final MultiMap<String, Object> auxHandlers;
        private final boolean displayTime;

        public Settings(Input input, Output output, MultiMap auxHandlers, boolean displayTime) {
            this.input = input;
            this.output = output;
            this.auxHandlers = auxHandlers;
            this.displayTime = displayTime;
        }

        public Settings createWithAddedAuxHandlers(MultiMap<String, Object> addAuxHandlers) {
            MultiMap<String, Object> allAuxHandlers = new ArrayHashMultiMap<String, Object>(auxHandlers);
            allAuxHandlers.putAll(addAuxHandlers);
            return new Settings(input, output, allAuxHandlers, displayTime);
        }

    }

    public Settings getSettings() {
        return new Settings(input, output, auxHandlers, displayTime);
    }

    public void setSettings(Settings s) {
        input = s.input;
        output = s.output;
        displayTime = s.displayTime;
        for (String prefix : s.auxHandlers.keySet()) {
            for (Object handler : s.auxHandlers.get(prefix)) {
                addAuxHandler(handler, prefix);
            }
        }
    }

    /**
     * Shell's constructor
     * You probably don't need this one, see methods of the ShellFactory.
     *
     * @param s            Settings object for the shell instance
     * @param commandTable CommandTable to store commands
     * @param path         Shell's location: list of path elements.
     * @see ShellFactory
     */
    public Shell(Settings s, CommandTable commandTable, List<String> path) {
        this.commandTable = commandTable;
        this.path = path;
        setSettings(s);
    }

    private CommandTable commandTable;

    /**
     * @return the CommandTable for this shell.
     */
    public CommandTable getCommandTable() {
        return commandTable;
    }

    private OutputConversionEngine outputConverter = new OutputConversionEngine();

    /**
     * Call this method to get OutputConversionEngine used by the Shell.
     *
     * @return a conversion engine.
     */
    public OutputConversionEngine getOutputConverter() {
        return outputConverter;
    }

    private InputConversionEngine inputConverter = new InputConversionEngine();

    /**
     * Call this method to get InputConversionEngine used by the Shell.
     *
     * @return a conversion engine.
     */
    public InputConversionEngine getInputConverter() {
        return inputConverter;
    }

    private MultiMap<String, Object> auxHandlers = new ArrayHashMultiMap<>();
    private List<Object> allHandlers = new ArrayList<>();


    /**
     * Method for registering command hanlers (or providers?)
     * You call it, and from then the Shell has all commands declare in
     * the handler object.
     * <p>
     * This method recognizes if it is passed ShellDependent or ShellManageable
     * and calls corresponding methods, as described in those interfaces.
     *
     * @param handler Object which should be registered as handler.
     * @param prefix  Prefix that should be prepended to all handler's command names.
     * @see ShellDependent
     * @see ShellManageable
     */
    public void addMainHandler(Object handler, String prefix) {
        if (handler == null) {
            throw new NullPointerException();
        }
        allHandlers.add(handler);

        addDeclaredMethods(handler, prefix);
        inputConverter.addDeclaredConverters(handler);
        outputConverter.addDeclaredConverters(handler);

        if (handler instanceof ShellDependent) {
            ((ShellDependent) handler).cliSetShell(this);
        }
    }

    /**
     * This method is very similar to addMainHandler, except ShellFactory
     * will pass all handlers registered with this method to all this shell's subshells.
     *
     * @param handler Object which should be registered as handler.
     * @param prefix  Prefix that should be prepended to all handler's command names.
     * @see Shell#addMainHandler(java.lang.Object, java.lang.String)
     */
    public void addAuxHandler(Object handler, String prefix) {
        if (handler == null) {
            throw new NullPointerException();
        }
        auxHandlers.put(prefix, handler);
        allHandlers.add(handler);

        addDeclaredMethods(handler, prefix);
        inputConverter.addDeclaredConverters(handler);
        outputConverter.addDeclaredConverters(handler);

        if (handler instanceof ShellDependent) {
            ((ShellDependent) handler).cliSetShell(this);
        }
    }

    private void addDeclaredMethods(Object handler, String prefix) throws SecurityException {
        for (Method m : handler.getClass().getMethods()) {
            Command annotation = m.getAnnotation(Command.class);
            if (annotation != null) {
                commandTable.addMethod(m, handler, prefix);
            }
        }
    }

    private Throwable lastException = null;

    /**
     * Returns last thrown exception
     */
    @Command(description = "Returns last thrown exception") // Shell is self-manageable, isn't it?
    public Throwable getLastException() {
        return lastException;
    }

    private List<String> path;

    /**
     * @return list of path elements, as it was passed in constructor
     */
    public List<String> getPath() {
        return path;
    }

    /**
     * Function to allow changing path at runtime; use with care to not break
     * the semantics of sub-shells (if you're using them) or use to emulate
     * tree navigation without subshells
     *
     * @param path New path
     */
    public void setPath(List<String> path) {
        this.path = path;
    }

    /**
     * Runs the command session.
     * Create the Shell, then run this method to listen to the user,
     * and the Shell will invoke Handler's methods.
     *
     * @throws java.io.IOException when can't readLine() from input.
     */
    public void commandLoop() throws IOException {
        for (Object handler : allHandlers) {
            if (handler instanceof ShellManageable) {
                ((ShellManageable) handler).cliEnterLoop();
            }
        }
        output.output(appName, outputConverter);
        String command = "";
        while (!command.trim().equals("exit")) {
            try {
                command = input.readCommand(path);
                processLine(command);
            } catch (TokenException te) {
                lastException = te;
                output.outputException(command, te);
            } catch (CliException clie) {
                lastException = clie;
                if (!command.trim().equals("exit")) {
                    output.outputException(clie);
                }
            }
        }
        for (Object handler : allHandlers) {
            if (handler instanceof ShellManageable) {
                ((ShellManageable) handler).cliLeaveLoop();
            }
        }
    }

    private void outputHeader(String header, Object[] parameters) {
        if (header == null || header.isEmpty()) {
            output.outputHeader(null);
        } else {
            output.outputHeader(String.format(header, parameters));
        }
    }

    private static final String HINT_FORMAT = "This is %1$s, running on Cliche Shell\n" +
        "For more information on the Shell, enter ?help";

    /**
     * You can operate Shell linewise, without entering the command loop.
     * All output is directed to shell's Output.
     *
     * @param line Full command line
     * @throws CliException This may be TokenException
     * @see Output
     */
    public void processLine(String line) throws CliException {
        if (line.trim().equals("?")) {
            output.output(String.format(HINT_FORMAT, appName), outputConverter);
        } else {
            List<Token> tokens = Token.tokenize(line);
            if (tokens.size() > 0) {
                String discriminator = tokens.get(0).getString();
                processCommand(discriminator, tokens);
            }
        }
    }

    private void processCommand(String discriminator, List<Token> tokens) throws CliException {
        assert discriminator != null;
        assert !discriminator.equals("");

        ShellCommand commandToInvoke = commandTable.lookupCommand(discriminator, tokens);

        Class[] paramClasses = commandToInvoke.getMethod().getParameterTypes();
        Object[] parameters = inputConverter.convertToParameters(tokens, paramClasses,
            commandToInvoke.getMethod().isVarArgs());

        outputHeader(commandToInvoke.getHeader(), parameters);

        long timeBefore = Calendar.getInstance().getTimeInMillis();
        Object invocationResult = commandToInvoke.invoke(parameters);
        long timeAfter = Calendar.getInstance().getTimeInMillis();

        if (invocationResult != null) {
            output.output(invocationResult, outputConverter);
        }
        if (displayTime) {
            final long time = timeAfter - timeBefore;
            if (time != 0L) {
                output.output(String.format(TIME_MS_FORMAT_STRING, time), outputConverter);
            }
        }
    }

    private static final String TIME_MS_FORMAT_STRING = "time: %d ms";

    private boolean displayTime = false;

    /**
     * Turns command execution time display on and off
     *
     * @param displayTime true if do display, false otherwise
     */
    @Command(description = "Turns command execution time display on and off")
    public void setDisplayTime(
        @Param(name = "do-display-time", description = "true if do display, false otherwise")
            boolean displayTime) {
        this.displayTime = displayTime;
    }

    /**
     * Hint is some text displayed before the command loop and every time user enters "?".
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

}