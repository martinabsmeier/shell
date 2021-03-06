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

import de.marabs.common.shell.exception.TokenException;

/**
 * Output for Shell to direct its output to.
 * Something like the Builder pattern.
 *
 * @author Martin Absmeier
 */
public interface Output {

    void output(Object obj, OutputConversion oce);

    void outputException(String input, TokenException error);

    void outputException(Throwable e);

    void outputHeader(String text);

}