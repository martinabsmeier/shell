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

import de.marabs.common.shell.otput.OutputConversionEngine;
import de.marabs.common.shell.otput.OutputConverter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OutputConversionEngineTest {

    @Before
    public void setUp() {
        converter = new OutputConversionEngine();
        converter.addDeclaredConverters(this);
    }

    OutputConversionEngine converter;

    public static final OutputConverter[] CLI_OUTPUT_CONVERTERS = {
        toBeFormatted -> {
            if (toBeFormatted instanceof String) {
                return String.format("(%s)", toBeFormatted);
            } else {
                return null;
            }
        },
        toBeFormatted -> {
            if (toBeFormatted instanceof String) {
                return String.format("[%s]", toBeFormatted);
            } else {
                return null;
            }
        },
    };

    /**
     * This method tests the order of applying converters, as other methods of
     * OutputConversionEngine are rather trivial.
     */
    @Test
    public void testConvertOutput() {
        System.out.println("convertOutput");
        String toBeConverted = "a";
        String expected = "([a])";
        assertEquals(expected, converter.convertOutput(toBeConverted));
    }
}