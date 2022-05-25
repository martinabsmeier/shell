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

import de.marabs.common.shell.Token;
import de.marabs.common.shell.exception.ShellException;
import de.marabs.common.shell.exception.TokenException;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * {@code InputConversion} responsible for converting strings to object.<br>
 * Elementary types can be handled by itself, and arbitrary types can be handled by registering InputConverter instances.
 * It also gets all converters declared in a handler object through addDeclaredConverters method.<br>
 * Used by Shell and will also be used by ShellCommand.
 *
 * @author Martin Absmeier
 */
public class InputConversion {

    private final List<InputConverter> inputConverters = new ArrayList<>();

    public void addConverter(InputConverter converter) {
        Objects.requireNonNull(converter, "Converter == null");
        inputConverters.add(converter);
    }

    public boolean removeConverter(InputConverter converter) {
        return inputConverters.remove(converter);
    }

    public Object convertInput(String string, Class<?> aClass) {
        for (InputConverter currentConverter : inputConverters) {
            Object conversionResult = currentConverter.convertInput(string, aClass);
            if (conversionResult != null) {
                if (!aClass.isAssignableFrom(conversionResult.getClass())) {
                    throw new ShellException("Registered asg.Cliche converter " + currentConverter + " returns wrong result");
                } else {
                    return conversionResult;
                }
            }
        }
        return convertArgToElementaryType(string, aClass);
    }

    public final Object[] convertToParameters(List<Token> tokens, Class<?>[] paramClasses, boolean isVarArgs) {
        assert isVarArgs || paramClasses.length == tokens.size() - 1;

        Object[] parameters = new Object[paramClasses.length];
        for (int i = 0; i < parameters.length - 1; i++) {
            try {
                parameters[i] = convertInput(tokens.get(i + 1).getString(), paramClasses[i]);
            } catch (ShellException ex) {
                throw new TokenException(tokens.get(i + 1), ex.getMessage());
            }
        }

        int lastIndex = paramClasses.length - 1;
        if (isVarArgs) {
            Class<?> varClass = paramClasses[lastIndex];
            assert varClass.isArray();
            Class<?> elemClass = varClass.getComponentType();
            Object theArray = Array.newInstance(elemClass, tokens.size() - paramClasses.length);
            for (int i = 0; i < Array.getLength(theArray); i++) {
                try {
                    Array.set(theArray, i, convertInput(tokens.get(lastIndex + 1 + i).getString(), elemClass));
                } catch (ShellException ex) {
                    throw new TokenException(tokens.get(lastIndex + 1 + i), ex.getMessage());
                }
            }
            parameters[lastIndex] = theArray;
        } else if (lastIndex >= 0) {
            try {
                parameters[lastIndex] = convertInput(tokens.get(lastIndex + 1).getString(), paramClasses[lastIndex]);
            } catch (ShellException ex) {
                throw new TokenException(tokens.get(lastIndex + 1), ex.getMessage());
            }
        }

        return parameters;
    }

    public void addDeclaredConverters(Object handler) {
        Field[] fields = handler.getClass().getFields();
        for (Field field : fields) {
            if (isPrefixAndArrayFieldAndConverter(field)) {
                try {
                    Object convertersArray = field.get(handler);
                    for (int i = 0; i < Array.getLength(convertersArray); i++) {
                        addConverter((InputConverter) Array.get(convertersArray, i));
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new ShellException("Error getting converter from field " + field.getName(), ex);
                }
            }
        }
    }

    // #################################################################################################################
    private boolean isPrefixAndArrayFieldAndConverter(Field field) {
        final String PREFIX = "CLI_INPUT_CONVERTERS";
        return field.getName().startsWith(PREFIX)
            && field.getType().isArray()
            && InputConverter.class.isAssignableFrom(field.getType().getComponentType());
    }

    private static Object convertArgToElementaryType(String string, Class<?> aClass) {
        if (aClass.equals(String.class) || aClass.isInstance(string)) {
            return string;
        } else if (aClass.equals(Integer.class) || aClass.equals(Integer.TYPE)) {
            return Integer.parseInt(string);
        } else if (aClass.equals(Long.class) || aClass.equals(Long.TYPE)) {
            return Long.parseLong(string);
        } else if (aClass.equals(Double.class) || aClass.equals(Double.TYPE)) {
            return Double.parseDouble(string);
        } else if (aClass.equals(Float.class) || aClass.equals(Float.TYPE)) {
            return Float.parseFloat(string);
        } else if (aClass.equals(Boolean.class) || aClass.equals(Boolean.TYPE)) {
            return Boolean.parseBoolean(string);
        } else {
            return createClass(string, aClass);
        }
    }

    private static Object createClass(String string, Class<?> aClass) {
        try {
            Constructor<?> constructor = aClass.getConstructor(String.class);
            return constructor.newInstance(string);
        } catch (Exception ex) {
            throw new ShellException("Can not instantiate class: " + aClass.getName(), ex);
        }
    }
}