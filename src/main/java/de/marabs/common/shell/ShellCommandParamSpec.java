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

import de.marabs.common.shell.annotation.CommandParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Specification of command's parameters, such as description given with CommandParameter annotation.
 *
 * @author Martin Absmeier
 */
public class ShellCommandParamSpec {

    static ShellCommandParamSpec[] forMethod(Method theMethod) {
        Class<?>[] paramTypes = theMethod.getParameterTypes();
        ShellCommandParamSpec[] result = new ShellCommandParamSpec[theMethod.getParameterTypes().length];
        Annotation[][] annotations = theMethod.getParameterAnnotations();
        assert annotations.length == result.length;
        for (int i = 0; i < result.length; i++) {
            CommandParameter paramAnnotation = null;
            for (Annotation a : annotations[i]) {
                if (a instanceof CommandParameter) {
                    paramAnnotation = (CommandParameter) a;
                    break;
                }
            }
            if (paramAnnotation != null) {
                assert !paramAnnotation.name().isEmpty() : "@CommandParameter.name must not be empty";
                result[i] = new ShellCommandParamSpec(paramAnnotation.name(), paramTypes[i],
                    paramAnnotation.description(), i);
            } else {
                result[i] = new ShellCommandParamSpec(String.format("p%d", i + 1), paramTypes[i], "", i);
            }
        }
        return result;
    }

    private final String name;
    private final String description;
    private final int position;
    private final Class<?> valueClass;

    public Class<?> getValueClass() {
        return valueClass;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public ShellCommandParamSpec(String name, Class<?> valueClass, String description, int position) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.valueClass = valueClass;
    }
}