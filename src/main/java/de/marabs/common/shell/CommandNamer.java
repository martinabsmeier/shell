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

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * This interface is a Strategy for auto-naming commands with no name specified
 * based on command's method. The algorithm is isolated because it's highly
 * subjective and therefore subject to changes. And, if you don't like
 * default dash-joined naming you can completely redefine the functionality.
 * <p>
 * It is also responsible for generating several suggested abbreviations,
 * one from them may later be "approved" by CommandTable.
 *
 * @author Martin Absmeier
 */
@FunctionalInterface
public interface CommandNamer {

    /**
     * Generate command name and suggested abbreviation variants.
     * Since this method is given a Method, not a string, the algorithm can
     * take parameter types into account.
     *
     * @param commandMethod Command method
     * @return com.infiniteimprobabilitydrive.analyzer.shell.CommandNamer.NamingInfo containing generated name and shortcut array.
     */
    NamingInfo nameCommand(Method commandMethod);

    /**
     * Return value grouping structure for nameCommand().
     * I decided to return name and abbreviations together because in the default
     * algorithm they are generated simultaneously, and I think this approach is
     * better than having a stateful strategy.
     */
    @Data
    class NamingInfo {
        public final String commandName;
        public final String[] possibleAbbreviations;

        public NamingInfo(String commandName, String[] possibleAbbreviations) {
            this.commandName = commandName;
            this.possibleAbbreviations = possibleAbbreviations;
        }

        @Override
        public String toString() {
            return String.format("NamingInfo(%s, %s)", commandName, Arrays.toString(possibleAbbreviations));
        }
    }
}