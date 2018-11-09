/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.tifezh.kchartlib.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Provides utility methods and decorators for {@link Collection} instances.
 *
 * @since Commons Collections 1.0
 * @version $Revision: 1713167 $ $Date: 2015-11-07 20:44:03 +0100 (Sat, 07 Nov 2015) $
 *
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Steve Downey
 * @author Herve Quiroz
 * @author Peter KoBek
 * @author Matthew Hawthorne
 * @author Janek Bogucki
 * @author Phil Steitz
 * @author Steven Melzer
 * @author Jon Schewe
 * @author Neil O'Toole
 * @author Stephen Smith
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }


    /**
     * Selects all elements from input collection which match the given predicate
     * into an output collection.
     * <p>
     * A <code>null</code> predicate matches no elements.
     *
     * @param inputCollection  the collection to get the input from, may not be null
     * @param predicate  the predicate to use, may be null
     * @return the elements matching the predicate (new list)
     * @throws NullPointerException if the input collection is null
     */
    public static <T> Collection<T> select(Collection<T> inputCollection, Predicate predicate) {
        ArrayList<T> answer = new ArrayList<>(inputCollection.size());
        select(inputCollection, predicate, answer);
        return answer;
    }

    /**
     * Selects all elements from input collection which match the given predicate
     * and adds them to outputCollection.
     * <p>
     * If the input collection or predicate is null, there is no change to the
     * output collection.
     *
     * @param inputCollection  the collection to get the input from, may be null
     * @param predicate  the predicate to use, may be null
     * @param outputCollection  the collection to output into, may not be null
     */
    public static <T> void select(Collection<T> inputCollection, Predicate predicate, Collection<T> outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (Iterator<T> iter = inputCollection.iterator(); iter.hasNext();) {
                T item = iter.next();
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
    }
}
