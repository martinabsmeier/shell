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

import de.marabs.common.shell.input.Input;
import de.marabs.common.shell.otput.Output;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Absmeier
 */
@Data
public class ShellConfig {

    private final Input input;
    private final Output output;
    private final Map<String, Object> auxHandlers;
    private final boolean displayTime;

    @Builder
    public ShellConfig(Input input, Output output, Map<String, Object> auxHandlers, boolean displayTime) {
        this.input = input;
        this.output = output;
        this.auxHandlers = auxHandlers;
        this.displayTime = displayTime;
    }

    public ShellConfig createWithAddedAuxHandlers(Map<String, Object> addAuxHandlers) {
        Map<String, Object> allAuxHandlers = new HashMap<>(auxHandlers);
        allAuxHandlers.putAll(addAuxHandlers);
        return ShellConfig.builder()
            .input(input)
            .output(output)
            .auxHandlers(addAuxHandlers)
            .displayTime(displayTime)
            .build();
    }
}