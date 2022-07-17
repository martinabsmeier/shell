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

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * JUnit test cases of {@link Command} and {@link CommandParameter} annotion.
 *
 * @author Martin Absmeier
 */
public class AnnotationTest {

    @Test
    public void commandAnnotation() {
        for (Method method : AnnotatedClass.class.getMethods()) {
            Command commandAnnotation = method.getAnnotation(Command.class);
            if (Objects.nonNull(commandAnnotation)) {
                System.out.println("Method '" + method.getName() + "' has command annotation.");
                System.out.println("    - Name       : " + commandAnnotation.name());
                System.out.println("    - Shortcut   : " + commandAnnotation.shortcut());
                System.out.println("    - Description: " + commandAnnotation.description());
                System.out.println("    - Header     : " + commandAnnotation.header());
            }

            for (Parameter parameter : method.getParameters()) {
                CommandParameter parameterAnnotation = parameter.getAnnotation(CommandParameter.class);
                if (Objects.nonNull(parameterAnnotation)) {
                    System.out.println("    --------------------------------------------------------------------------------");
                    System.out.println("    - Parameter '" + parameter.toString() + "' has param annotation.");
                    System.out.println("        - Name       : " + parameterAnnotation.name());
                    System.out.println("        - Description: " + parameterAnnotation.description());
                }
            }
        }
    }
}