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
import de.marabs.common.shell.exception.ShellException;
import de.marabs.common.shell.exception.TokenException;
import de.marabs.common.shell.input.Input;
import de.marabs.common.shell.otput.Output;
import de.marabs.common.shell.otput.OutputConversion;
import de.marabs.common.shell.util.Strings;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Console IO subsystem.
 * This is also one of special command handlers and is responsible
 * for logging (duplicating output) and execution of scripts.
 *
 * @author Martin Absmeier
 */
public class ConsoleIO implements Input, Output, ShellManageable {

    private static final String USER_PROMPT_SUFFIX = "> ";
    private static final String FILE_PROMPT_SUFFIX = "$ ";
    private final BufferedReader in;
    private final PrintStream out;
    private final PrintStream err;
    private int lastCommandOffset = 0;
    private PrintStream log = null;
    private int loopCounter = 0;
    private InputState inputState = InputState.USER;
    private BufferedReader scriptReader = null;

    private enum InputState {USER, SCRIPT}

    public ConsoleIO(BufferedReader in, PrintStream out, PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    public ConsoleIO() {
        this(new BufferedReader(new InputStreamReader(System.in)), System.out, System.err);
    }

    public String readCommand(List<String> path) {
        try {
            String prompt = Strings.joinStrings(path, false, '/');

            if (InputState.USER.equals(inputState)) {
                return readUsersCommand(prompt);
            }

            if (InputState.SCRIPT.equals(inputState)) {
                String command = readCommandFromScript(prompt);
                if (command != null) {
                    return command;
                } else {
                    closeScript();
                    return readUsersCommand(prompt);
                }
            }

            return readUsersCommand(prompt);
        } catch (IOException ex) {
            throw new ShellException(ex);
        }
    }

    @Command(description = "Reads commands from file")
    public void runScript(
        @Param(name = "filename", description = "Full file name of the script") String filename) throws FileNotFoundException {
        scriptReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        inputState = InputState.SCRIPT;
    }

    public void outputHeader(String text) {
        if (text != null) {
            println(text);
        }
    }

    public void output(Object obj, OutputConversion oce) {
        if (Objects.isNull(obj)) {
            return;
        }

        obj = oce.convertOutput(obj);
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                output(Array.get(obj, i), 0, oce);
            }
        } else if (obj instanceof Collection) {
            for (Object elem : (Collection<?>) obj) {
                output(elem, 0, oce);
            }
        } else {
            output(obj, 0, oce);
        }
    }

    public void outputException(String input, TokenException error) {
        int errIndex = error.getToken().getIndex() + lastCommandOffset;
        while (errIndex-- > 0) {
            printErr("-");
        }
        for (int i = 0; i < error.getToken().getString().length(); i++) {
            printErr("^");
        }
        printlnErr("");
        printlnErr(error);
    }

    public void outputException(Throwable e) {
        printlnErr(e);
        if (e.getCause() != null) {
            printlnErr(e.getCause());
        }
    }

    public void cliEnterLoop() {
        if (isLoggingEnabled()) {
            loopCounter++;
        }
    }

    public void cliLeaveLoop() {
        if (isLoggingEnabled()) {
            loopCounter--;
        }
        if (loopCounter < 0) {
            disableLogging();
        }
    }

    @Command(description = "Sets up logging, which duplicates all subsequent output in a file")
    public void enableLogging(
        @Param(name = "fileName", description = "Name of the logfile") String filename) throws FileNotFoundException {
        log = new PrintStream(filename);
        loopCounter = 0;
    }

    @Command(description = "Turns off logging")
    public String disableLogging() {
        if (log != null) {
            log.close();
            log = null;
            return "Logging disabled";
        } else {
            return "Logging is already disabled";
        }
    }

    // #################################################################################################################
    private String readUsersCommand(String prompt) throws IOException {
        String completePrompt = prompt + USER_PROMPT_SUFFIX;
        print(completePrompt);
        lastCommandOffset = completePrompt.length();

        String command = in.readLine();
        if (log != null) {
            log.println(command);
        }
        return command;
    }


    private String readCommandFromScript(String prompt) throws IOException {
        String command = scriptReader.readLine();
        if (command != null) {
            String completePrompt = prompt + FILE_PROMPT_SUFFIX;
            print(completePrompt);
            lastCommandOffset = completePrompt.length();
        }
        return command;
    }

    private void closeScript() throws IOException {
        if (scriptReader != null) {
            scriptReader.close();
            scriptReader = null;
        }
        inputState = InputState.USER;
    }

    private void print(Object x) {
        out.print(x);
        if (log != null) {
            log.print(x);
        }
    }

    private void println(Object x) {
        out.println(x);
        if (log != null) {
            log.println(x);
        }
    }

    private void printErr(Object x) {
        err.print(x);
        if (log != null) {
            log.print(x);
        }
    }

    private void printlnErr(Object x) {
        err.println(x);
        if (log != null) {
            log.println(x);
        }
    }

    private void output(Object obj, int indent, OutputConversion oce) {
        if (Objects.isNull(obj)) {
            return;
        }

        obj = oce.convertOutput(obj);
        for (int i = 0; i < indent; i++) {
            print("\t");
        }

        if (Objects.isNull(obj)) {
            println("(null)");
        } else if (obj.getClass().isPrimitive() || obj instanceof String) {
            println(obj);
        } else if (obj.getClass().isArray()) {
            println("Array");
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                output(Array.get(obj, i), indent + 1, oce);
            }
        } else if (obj instanceof Collection) {
            println("Collection");
            for (Object elem : (Collection<?>) obj) {
                output(elem, indent + 1, oce);
            }
        } else if (obj instanceof Throwable) {
            println(obj); // class and its message
            ((Throwable) obj).printStackTrace(out);
        } else {
            println(obj);
        }
    }

    private boolean isLoggingEnabled() {
        return log != null;
    }
}