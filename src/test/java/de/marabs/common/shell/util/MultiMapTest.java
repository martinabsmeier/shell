/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */
package de.marabs.common.shell.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author ASG
 */
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