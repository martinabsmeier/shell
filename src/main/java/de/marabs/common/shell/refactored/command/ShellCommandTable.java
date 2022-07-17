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
package de.marabs.common.shell.refactored.command;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * {@code ShellCommandTable} is responsible for managing a lot of {@link ShellCommand} and is like a dictionary, because its main
 * function is to return a command by name.
 *
 * @author Martin Absmeier
 */
@Data
public class ShellCommandTable {

    private List<ShellCommand> commands;

    @Builder
    public ShellCommandTable(List<ShellCommand> commands) {
        this.commands = commands;
    }
}