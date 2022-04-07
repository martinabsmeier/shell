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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Output conversion engine is responsible for converting objects after they are returned
 * by command but before they are sent to the Output.
 * As with InputConversionEngine, it can automatically retrieve all converters declared inside
 * an object.
 * <p>
 * All converters are applied to all objects, first-registered--last-applied.
 * <p>
 * Used by Shell.
 *
 * @author Martin Absmeier
 */
public class OutputConversionEngine {

    private List<OutputConverter> outputConverters = new ArrayList<>();

    public void addConverter(OutputConverter converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter == null");
        }
        outputConverters.add(converter);
    }

    public boolean removeConverter(OutputConverter converter) {
        return outputConverters.remove(converter);
    }

    public Object convertOutput(Object anObject) {
        Object convertedOutput = anObject;
        for (ListIterator<OutputConverter> it = outputConverters.listIterator(outputConverters.size()); it.hasPrevious(); ) {
            OutputConverter outputConverter = it.previous(); // last in --- first called.
            Object conversionResult = outputConverter.convertOutput(convertedOutput);
            if (conversionResult != null) {
                convertedOutput = conversionResult;
            }
        }
        return convertedOutput;
    }

    public void addDeclaredConverters(Object handler) {
        Field[] fields = handler.getClass().getFields();
        final String PREFIX = "CLI_OUTPUT_CONVERTERS";
        for (Field field : fields) {
            if (field.getName().startsWith(PREFIX)
                && field.getType().isArray()
                && OutputConverter.class.isAssignableFrom(field.getType().getComponentType())) {
                try {
                    Object convertersArray = field.get(handler);
                    for (int i = 0; i < Array.getLength(convertersArray); i++) {
                        addConverter((OutputConverter) Array.get(convertersArray, i));
                    }
                } catch (Exception ex) {
                    throw new RuntimeException("Error getting converter from field " + field.getName(), ex);
                }
            }
        }
    }
}