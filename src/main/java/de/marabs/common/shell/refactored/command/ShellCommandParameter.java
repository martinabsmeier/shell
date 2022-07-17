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

/**
 * {@code ShellCommandParameter} specify command' parameters, such as description given with CommandParameter annotation.
 *
 * @author Martin Absmeier
 */
@Data
public class ShellCommandParameter {

    private String name;
    private String description;
    private int position;
    private Class<?> valueClass;

    @Builder
    public ShellCommandParameter(String name, String description, int position, Class<?> valueClass) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.valueClass = valueClass;
    }
}