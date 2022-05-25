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
package de.marabs.common.shell.input;

import de.marabs.common.shell.Shell;

/**
 * This interface is used by the Shell to support new argument types.
 * It converts string to an object of given class.
 *
 * @author Martin Absmeier
 */
@FunctionalInterface
public interface InputConverter {

    /**
     * String-to-someClass conversion method
     * May throw any exception if string is considered invalid for given class;
     * must do nothing but return null if doesn't recognize the toClass.
     *
     * @param original String to be converted
     * @param toClass  Class to be converted to
     * @return Object of the class toClass or <strong>null</strong>, if don't know how to convert to given class
     * @see Shell
     */
    Object convertInput(String original, Class<?> toClass) throws Exception;
}