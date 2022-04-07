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
package de.marabs.common.shell.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MultiMapTest {

    @Test
    public void test() {
        System.out.println("test");
        MultiMap<String, String> map = new ArrayHashMultiMap<>();
        map.put("k1", "v11");
        map.put("k2", "v21");
        map.put("k1", "v12");
        assertEquals(3, map.size());
        map.remove("k1", "v12");
        assertEquals(2, map.size());
        map.put("k2", "v22");
        map.removeAll("k2");
        assertEquals(1, map.size());
    }
}