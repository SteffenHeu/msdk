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

package io.github.msdk.datamodel.rawdata;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

/**
 * This interface provides a convenient data structure for storing large amount
 * of MS data points in memory. Internally, it is implemented by two arrays, one
 * for m/z values (double[]) and one for intensity values (float[]). The arrays
 * are resized dynamically, as in ArrayList.
 * 
 * DataPointList provides all List methods, therefore it supports iteration.
 * Furthermore, it provides direct access to the underlying buffer arrays, for
 * more efficient data handling operations. The access through these arrays is
 * always preferred, as iteration via the List interface has to create a new
 * DataPoint instance for each visited data point.
 * 
 * DataPointList methods always keep the data points sorted in the m/z order,
 * and this requirement must be maintained when the internal m/z and intensity
 * arrays are modified directly.
 * 
 * The equals() method compares the contents of the two data point lists, and
 * ignores their internal array sizes (capacities).
 * 
 * This data structure is not thread-safe.
 */
public interface DataPointList extends List<DataPoint> {

    /**
     * Returns the range of m/z values in this DataPointList.
     * 
     * @return Range of m/z values in this DataPointList
     */
    @Nonnull
    Range<Double> getMzRange();

    /**
     * Returns the current m/z buffer array. The size of the array might be
     * larger than the actual size of this DataPointList, therefore data
     * operations should always use the size returned by the size() method and
     * not the length of the returned array. The returned array reflects only
     * the current state of this list - if more data points are added, the
     * internal buffer might be replaced with a larger array.
     * 
     * @return Current m/z buffer
     */
    @Nonnull
    double[] getMzBuffer();

    /**
     * Returns the current intensity buffer array. The size of the array might
     * be larger than the actual size of this DataPointList, therefore data
     * operations should always use the size returned by the size() method and
     * not the length of the returned array. The returned array reflects only
     * the current state of this list - if more data points are added, the
     * internal buffer might be replaced with a larger array.
     * 
     * @return Current intensity buffer
     */
    @Nonnull
    float[] getIntensityBuffer();

    /**
     * Sets the internal buffers to given arrays. The arrays will be referenced
     * directly without cloning. The m/z buffer contents must be sorted in
     * ascending order.
     * 
     * @param mzBuffer
     *            New m/z buffer
     * @param intensityBuffer
     *            New intensity buffer
     * @param newSize
     *            Number of data point items in the buffers. This might be
     *            smaller than the actual length of the buffer arrays.
     * @throws IllegalArgumentException
     *             If the size is larger than the length of the m/z array.
     * @throws IllegalStateException
     *             if the m/z array is not sorted in ascending order.
     */
    void setBuffers(@Nonnull double[] mzBuffer,
            @Nonnull float[] intensityBuffer, int newSize);

    /**
     * Updates the size of the list, assuming the m/z and intensity arrays have
     * already been updated accordingly. This method also checks whether the m/z
     * array is sorted in ascending order.
     * 
     * @param newSize
     *            New size of the list. Must be <= length of the m/z array.
     * @throws IllegalArgumentException
     *             If the size is larger than the length of the m/z array.
     * @throws IllegalStateException
     *             if the m/z array is not sorted in ascending order.
     */
    void setSize(int newSize);

    /**
     * Copies the contents of another data point list into this list. The
     * capacity of this list might stay the same or it might change, depending
     * on needs.
     * 
     * @param list
     *            Source list to copy from.
     */
    void copyFrom(@Nonnull DataPointList list);

    /**
     * Copies the contents of this data point list into another list. The
     * capacity of the target list might stay the same or it might change,
     * depending on needs.
     * 
     * @param list
     *            Target list to copy to.
     */
    void copyTo(@Nonnull DataPointList list);

}
