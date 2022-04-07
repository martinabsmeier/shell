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

import de.marabs.common.shell.util.Strings;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Default "dash-joined" implementation of the CommandNamer.
 *
 * @author Martin Absmeier
 */
public class DashJoinedNamer implements CommandNamer {

    private final boolean doRemoveCommonPrefix;

    public DashJoinedNamer(boolean doRemoveCommonPrefix) {
        this.doRemoveCommonPrefix = doRemoveCommonPrefix;
    }

    public NamingInfo nameCommand(Method method) {
        List<String> words = Strings.splitJavaIdentifier(method.getName());

        if (doRemoveCommonPrefix) {
            final String COMMON_PREFIX_1 = "cmd";
            final String COMMON_PREFIX_2 = "cli";

            if (words.size() > 1 && (words.get(0).equals(COMMON_PREFIX_1)
                || words.get(0).equals(COMMON_PREFIX_2))) {
                words.remove(0);
            }
        }

        String name = Strings.joinStrings(words, true, '-');
        String[] abbrevs = proposeAbbrevs(words);
        return new NamingInfo(name, abbrevs);
    }

    private String[] proposeAbbrevs(List<String> words) {
        // exit has reserved meaning; sorry for this ugly hack.
        if (words.size() == 1 && words.get(0).equals("exit")) {
            return new String[]{};
        }
        StringBuilder abbrev1 = new StringBuilder();
        for (String word : words) {
            assert !word.isEmpty();
            abbrev1.append(Character.toLowerCase(word.charAt(0)));
        }

        StringBuilder abbrev2 = new StringBuilder();
        for (String word : words) {
            abbrev2.append(Character.toLowerCase(word.charAt(0)));
            if (word.length() > 1) {
                abbrev2.append(Character.toLowerCase(word.charAt(1)));
            }
        }

        if (abbrev2.length() > 0) {
            return new String[]{abbrev1.toString(), abbrev2.toString()};
        } else {
            return new String[]{abbrev1.toString()};
        }
    }
}