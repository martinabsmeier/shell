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

public class AnnotatedClass {

    @Command
    public void method_01() {
        System.out.println("method_01");
    }

    @Command(name = "method_02", shortcut = "m02", description = "Description method_02")
    public void method_02() {
        System.out.println("method_02");
    }

    @Command(name = "method_03", shortcut = "m03", description = "Description method_03")
    public void method_03(@CommandParameter(name = "param_01") String param_01) {
        System.out.println("method_03");
    }
}