/*
 * Copyright 2013 Cliche project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.marabs.common.shell.util;

import java.util.*;

import static java.util.Objects.nonNull;

public final class ArrayHashMultiMap<K, V> implements MultiMap<K, V> {

    private final Map<K, List<V>> listMap;

    public ArrayHashMultiMap() {
        listMap = new HashMap<>();
    }

    public ArrayHashMultiMap(MultiMap<K, V> map) {
        this();
        putAll(map);
    }

    public void put(K key, V value) {
        List<V> values = listMap.computeIfAbsent(key, k -> new ArrayList<>());
        values.add(value);
    }

    public Collection<V> get(K key) {
        return listMap.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public Set<K> keySet() {
        return listMap.keySet();
    }

    public void remove(K key, V value) {
        List<V> values = listMap.get(key);
        if (nonNull(values)) {
            values.remove(value);
            if (values.isEmpty()) {
                listMap.remove(key);
            }
        }
    }

    public void removeAll(K key) {
        listMap.remove(key);
    }

    public int size() {
        int sum = 0;
        for (Map.Entry<K, List<V>> entry : listMap.entrySet()) {
            sum += listMap.get(entry.getKey()).size();
        }
        return sum;
    }

    public void putAll(MultiMap<K, V> map) {
        for (K key : map.keySet()) {
            for (V val : map.get(key)) {
                put(key, val);
            }
        }
    }

    @Override
    public String toString() {
        return listMap.toString();
    }
}