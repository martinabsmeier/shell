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
import de.marabs.common.shell.annotation.CommandParameter;
import de.marabs.common.shell.exception.ShellException;
import de.marabs.common.shell.exception.TokenException;
import de.marabs.common.shell.input.Input;
import de.marabs.common.shell.input.InputConversion;
import de.marabs.common.shell.otput.Output;
import de.marabs.common.shell.otput.OutputConversion;
import de.marabs.common.shell.util.ArrayHashMultiMap;
import de.marabs.common.shell.util.MultiMap;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Shell is the class interacting with user.
 * Provides the command loop.
 * All logic lies here.
 *
 * @author Martin Absmeier
 */
@Data
public class Shell {

    public static final String PROJECT_HOMEPAGE_URL = "https://github.com/martinabsmeier/shell";
    private static final String HINT_FORMAT = "This is %1$s, running on Shell\nFor more information on the Shell, enter ?help";
    private static final String TIME_MS_FORMAT_STRING = "time: %d ms";

    private Output output;
    private Input input;
    private String appName;
    private CommandTable commandTable;
    private InputConversion inputConverter = new InputConversion();
    private OutputConversion outputConverter = new OutputConversion();
    private MultiMap<String, Object> auxHandlers = new ArrayHashMultiMap<>();
    private List<Object> allHandlers = new ArrayList<>();
    private Throwable lastException = null;
    private List<String> path;
    private boolean displayTime = false;

    public ShellConfig getShellConfig() {
        return ShellConfig.builder().input(input).output(output).auxHandlers(auxHandlers).displayTime(displayTime).build();
    }

    public void setShellConfig(ShellConfig config) {
        input = config.getInput();
        output = config.getOutput();
        displayTime = config.isDisplayTime();
        for (String prefix : config.getAuxHandlers().keySet()) {
            for (Object handler : config.getAuxHandlers().get(prefix)) {
                addAuxHandler(handler, prefix);
            }
        }
    }

    /**
     * Shell's constructor
     * You probably don't need this one, see methods of the ShellFactory.
     *
     * @param config       ShellConfig object for the shell instance
     * @param commandTable CommandTable to store commands
     * @param path         Shell's location: list of path elements.
     * @see ShellFactory
     */
    @Builder
    public Shell(ShellConfig config, CommandTable commandTable, List<String> path) {
        this.commandTable = commandTable;
        this.path = path;
        setShellConfig(config);
    }

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
        Objects.requireNonNull(handler, "NULL is not permitted as value for handler.");

        allHandlers.add(handler);
        addDeclaredMethods(handler, prefix);
        inputConverter.addDeclaredConverters(handler);
        outputConverter.addDeclaredConverters(handler);

        if (handler.getClass().isAssignableFrom(ShellDependent.class)) {
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
                ((ShellManageable) handler).enterLoop();
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
            } catch (ShellException clie) {
                lastException = clie;
                if (!command.trim().equals("exit")) {
                    output.outputException(clie);
                }
            }
        }
        for (Object handler : allHandlers) {
            if (handler instanceof ShellManageable) {
                ((ShellManageable) handler).leaveLoop();
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

    /**
     * You can operate Shell linewise, without entering the command loop.
     * All output is directed to shell's Output.
     *
     * @param line Full command line
     * @throws ShellException This may be TokenException
     * @see Output
     */
    public void processLine(String line) throws ShellException {
        if (line.trim().equals("?")) {
            output.output(String.format(HINT_FORMAT, appName), outputConverter);
        } else {
            List<Token> tokens = Token.tokenize(line);
            if (!tokens.isEmpty()) {
                String discriminator = tokens.get(0).getString();
                processCommand(discriminator, tokens);
            }
        }
    }

    private void processCommand(String discriminator, List<Token> tokens) throws ShellException {
        assert discriminator != null;
        assert !discriminator.equals("");

        ShellCommand commandToInvoke = commandTable.lookupCommand(discriminator, tokens);

        Class<?>[] paramClasses = commandToInvoke.getMethod().getParameterTypes();
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

    // #################################################################################################################

    /**
     * Turns command execution time display on and off
     *
     * @param displayTime true if do display, false otherwise
     */
    @Command(description = "Turns command execution time display on and off")
    public void setDisplayTime(
        @CommandParameter(name = "do-display-time", description = "true if do display, false otherwise")
        boolean displayTime) {
        this.displayTime = displayTime;
    }

    /**
     * Returns last thrown exception
     */
    @Command(description = "Returns last thrown exception")
    public Throwable getLastException() {
        return lastException;
    }
}