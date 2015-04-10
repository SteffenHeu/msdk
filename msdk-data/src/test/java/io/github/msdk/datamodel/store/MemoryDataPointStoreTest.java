/* 
 * (C) Copyright 2015 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.datamodel.store;

import org.junit.Test;

/**
 * Tests for MemoryDataPointStore
 */
public class MemoryDataPointStoreTest {

    @Test
    public void testStoreReadDataPoints() {

        MemoryDataPointStore store = new MemoryDataPointStore();
        DataPointStoreTest.testStoreReadDataPoints(store);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveDataPoints() {

        MemoryDataPointStore store = new MemoryDataPointStore();
        DataPointStoreTest.testRemoveDataPoints(store);

    }

    @Test(expected = IllegalStateException.class)
    public void testDispose() {

        MemoryDataPointStore store = new MemoryDataPointStore();
        DataPointStoreTest.testDispose(store);

    }

}
