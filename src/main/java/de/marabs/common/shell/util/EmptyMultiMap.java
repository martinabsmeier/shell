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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EmptyMultiMap<K, V> implements MultiMap<K, V> {

    public void put(K key, V value) {
        throw new UnsupportedOperationException("You can't modify EmptyMultyMap: it's always empty!");
    }

    public Collection<V> get(K key) {
        return new ArrayList<V>();
    }

    public Set<K> keySet() {
        return new HashSet<K>();
    }

    public void remove(K key, V value) {
        throw new UnsupportedOperationException("You can't modify EmptyMultyMap: it's always empty!");
    }

    public void removeAll(K key) {
        throw new UnsupportedOperationException("You can't modify EmptyMultyMap: it's always empty!");
    }

    public int size() {
        return 0;
    }

    public void putAll(MultiMap<K, V> map) {
        throw new UnsupportedOperationException("You can't modify EmptyMultyMap: it's always empty!");
    }

    @Override
    public String toString() {
        return "{}";
    }

}