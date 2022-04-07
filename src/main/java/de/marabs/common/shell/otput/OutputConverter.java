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
package de.marabs.common.shell.otput;

/**
 * This interface is used by the Shell to support new return types.
 * It converts objects to other objects (usually strings) that will be displayed.
 *
 * @author Martin Absmeier
 */
public interface OutputConverter {
    /**
     * Object-to--user-friendly-object (usually string) conversion method.
     * The method must check argument's class, since it will be fed virtually all
     * returned objects. Simply return null when not sure.
     *
     * @param toBeFormatted Object to be displayed to the user
     * @return Object representing the object or Null if don't know how to make it.
     * Do not return default toString() !!
     */
    Object convertOutput(Object toBeFormatted);
}