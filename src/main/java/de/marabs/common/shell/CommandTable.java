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
import de.marabs.common.shell.exception.ShellException;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Command table is responsible for managing a lot of ShellCommands and is like a dictionary,
 * because its main function is to return a command by name.
 *
 * @author Martin Absmeier
 */
public class CommandTable {

    @Getter
    private final List<ShellCommand> commandTable;
    @Getter
    private final CommandNamer namer;

    public CommandTable(CommandNamer namer) {
        commandTable = new ArrayList<>();
        this.namer = namer;
    }

    public void addMethod(Method method, Object handler, String prefix) {
        requireNonNull(method, "NULL is not permitted as value for 'method' parameter.");

        Command annotation = method.getAnnotation(Command.class);
        String name;
        String autoAbbrev = null;

        if (annotation != null && annotation.name() != null && !annotation.name().equals("")) {
            name = annotation.name();
        } else {
            CommandNamer.NamingInfo autoNames = namer.nameCommand(method);
            name = autoNames.commandName;
            for (String abbr : autoNames.possibleAbbreviations) {
                if (!doesCommandExist(prefix + abbr, method.getParameterTypes().length)) {
                    autoAbbrev = abbr;
                    break;
                }
            }
        }

        ShellCommand command = new ShellCommand(handler, method, prefix, name);

        if (annotation != null && annotation.abbrev() != null && !annotation.abbrev().equals("")) {
            command.setAbbreviation(annotation.abbrev());
        } else {
            command.setAbbreviation(autoAbbrev);
        }
        if (annotation != null && annotation.description() != null && !annotation.description().equals("")) {
            command.setDescription(annotation.description());
        }
        if (annotation != null && annotation.header() != null && !annotation.header().equals("")) {
            command.setHeader(annotation.header());
        }

        commandTable.add(command);
    }

    private boolean doesCommandExist(String commandName, int arity) {
        for (ShellCommand cmd : commandTable) {
            if (cmd.canBeDenotedBy(commandName) && cmd.getArity() == arity) {
                return true;
            }
        }
        return false;
    }

    public List<ShellCommand> commandsByName(String discriminator) {
        List<ShellCommand> collectedTable = new ArrayList<>();
        // collection
        for (ShellCommand cs : commandTable) {
            if (cs.canBeDenotedBy(discriminator)) {
                collectedTable.add(cs);
            }
        }
        return collectedTable;
    }

    public ShellCommand lookupCommand(String discriminator, List<Token> tokens) throws ShellException {
        List<ShellCommand> collectedTable = commandsByName(discriminator);
        // reduction
        List<ShellCommand> reducedTable = new ArrayList<>();
        for (ShellCommand cs : collectedTable) {
            if (cs.getMethod().getParameterTypes().length == tokens.size() - 1
                || (cs.getMethod().isVarArgs()
                && (cs.getMethod().getParameterTypes().length <= tokens.size() - 1))) {
                reducedTable.add(cs);
            }
        }
        // selection
        if (collectedTable.isEmpty()) {
            throw ShellException.createCommandNotFound(discriminator);
        } else if (reducedTable.isEmpty()) {
            throw ShellException.createCommandNotFoundForArgNum(discriminator, tokens.size() - 1);
        } else if (reducedTable.size() > 1) {
            throw ShellException.createAmbiguousCommandExc(discriminator, tokens.size() - 1);
        } else {
            return reducedTable.get(0);
        }
    }
}