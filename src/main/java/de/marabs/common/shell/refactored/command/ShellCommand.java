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

import java.lang.reflect.Method;

@Data
public class ShellCommand {

    private String name;
    private String description;
    private String shortcut;
    private String header;
    private Method method;
    private Object handler;
    private ShellCommandParameter[] parameters;

    @Builder
    public ShellCommand(String name, String description, String shortcut, String header, Method method, Object handler, ShellCommandParameter[] parameters) {
        this.name = name;
        this.description = description;
        this.shortcut = shortcut;
        this.header = header;
        this.method = method;
        this.handler = handler;
        this.parameters = parameters;
    }
}