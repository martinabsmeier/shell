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
package de.marabs.common.shell.refactored;

import de.marabs.common.shell.refactored.command.ShellCommandTable;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Shell is the class interacting with user and provides the command loop.
 * All logic lies here.
 *
 * @author Martin Absmeier
 */
@Data
public class Shell {

    private ShellConfig config;
    private ShellCommandTable commands;
    private List<Object> handlers;

    @Builder
    public Shell(ShellConfig config, ShellCommandTable commands, List<Object> handlers) {
        this.config = config;
        this.commands = commands;
        this.handlers = isNull(handlers) ? new ArrayList<>() : handlers;
    }
}