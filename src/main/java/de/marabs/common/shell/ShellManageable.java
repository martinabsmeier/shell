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
 * This interface is for classes that want to be aware of entering and leaving each command loop.
 * <p>
 * You might want some special resources to be allocated before CLI starts; after conversation you want to free those
 * resources. By implementing this interface you get the ability to handle these events.
 * <p>
 * Note that since Shell can possibly have other means of operation instead of commandLoop(), these methods may be not
 * called.
 *
 * @author Martin Absmeier
 */
public interface ShellManageable {

    /**
     * This method is called when it is about to enter the command loop.
     */
    void cliEnterLoop();

    /**
     * This method is called when Shell is leaving the command loop.
     */
    void cliLeaveLoop();
}