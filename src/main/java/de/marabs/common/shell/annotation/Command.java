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
package de.marabs.common.shell.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for commands. Allows to specify the name of a command, otherwise method's name is used.
 *
 * @author Martin Absmeier
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * Allows overriding default command name, which is derived from method's name
     *
     * @return "" or null if default name is used, user-specified name otherwise.
     */
    String name() default "";

    /**
     * Specify the description of the command. Default description (if this property is not set) says
     * "methodName(Arg1Type, Arg2Type,...) : ReturnType".
     *
     * @return command's description or "" if not set.
     */
    String description() default "";

    /**
     * Specify the shortcut name for the command.
     * If not set, if the name attribute is not set as well, the Shell takes
     * the first letter of each word (void selectUser() --- select-user --- su).
     *
     * @return command's abbreviation or "" if not set.
     */
    String abbrev() default "";

    /**
     * Specify the string to output before command's output, i.e. some explanations.
     *
     * @return command's header or "" if not set.
     */
    String header() default "";
}