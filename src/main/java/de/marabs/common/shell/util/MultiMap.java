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

import java.util.Collection;
import java.util.Set;

/**
 * This is an extension to Java Collections framework.
 * MultiMap is a map which can contain multiple values under the same key.
 *
 * @author Martin Absmeier
 */
public interface MultiMap<K, V> {

    void put(K key, V value);

    void putAll(MultiMap<K, V> map);

    Collection<V> get(K key);

    Set<K> keySet();

    void remove(K key, V value);

    void removeAll(K key);

    int size();

}