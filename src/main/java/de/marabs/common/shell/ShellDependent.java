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

/**
 * Classes that want to have an instance of Shell associated with them should implement this interface.
 * Upon registration in Shell, cliSetShell() is called.
 *
 * @author Martin Absmeier
 */
public interface ShellDependent {

    /**
     * This method informs the object about the Shell operating it.
     * Is called upon object's registration in Shell.
     *
     * @param theShell Shell running the object.
     */
    void cliSetShell(Shell theShell);

}