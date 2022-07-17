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

import lombok.Builder;
import lombok.Data;

@Data
public class ShellConfig {

    private boolean isLoggingEnabled;
    private boolean displayTime;

    @Builder
    public ShellConfig(boolean isLoggingEnabled, boolean displayTime) {
        this.isLoggingEnabled = isLoggingEnabled;
        this.displayTime = displayTime;
    }
}