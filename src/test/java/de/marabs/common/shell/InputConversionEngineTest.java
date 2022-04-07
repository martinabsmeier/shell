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

import de.marabs.common.shell.input.InputConversionEngine;
import de.marabs.common.shell.input.InputConverter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputConversionEngineTest {

    private InputConversionEngine converter;

    @Before
    public void setUp() {
        converter = new InputConversionEngine();
    }


    @Test
    public void testElementaryTypes() throws Exception {
        System.out.println("testElementaryTypes");

        String[] strings = {"aString", "some-object", "1243", "12432141231256", "12.6", "12.6", "true",};
        Class[] classes = {String.class, Object.class, Integer.class, Long.class, Double.class, Float.class, Boolean.class,};
        Object[] results = {"aString", "some-object", 1243, 12432141231256L, 12.6, 12.6f, Boolean.TRUE,};

        for (int i = 0; i < strings.length; i++) {
            assertEquals(results[i], converter.convertInput(strings[i], classes[i]));
        }

    }

    private static final int MAGIC_INT = 234;

    private static final InputConverter testInputConverter = (original, toClass) -> {
        if (toClass.equals(Integer.class)) {
            return MAGIC_INT; // no matter what was the text, for test purposes
        } else {
            return null;
        }
    };

    @Test
    public void testConverterRegistration() throws Exception {
        System.out.println("testConverterRegistration");

        InputConversionEngine otherConverter = new InputConversionEngine();
        otherConverter.addConverter(testInputConverter);
        assertEquals(MAGIC_INT, otherConverter.convertInput("10", Integer.class));
        otherConverter.removeConverter(testInputConverter);
        assertEquals(MAGIC_INT, otherConverter.convertInput(Integer.toString(MAGIC_INT), Integer.class));
    }

    public static InputConverter[] CLI_INPUT_CONVERTERS = {
        testInputConverter,
    };

    @Test
    public void testDeclaredConverterRegistration() throws Exception {
        System.out.println("testDeclaredConverterRegistration");

        InputConversionEngine otherConverter = new InputConversionEngine();
        otherConverter.addDeclaredConverters(this);
        assertEquals(MAGIC_INT, otherConverter.convertInput("10", Integer.class));
    }
}