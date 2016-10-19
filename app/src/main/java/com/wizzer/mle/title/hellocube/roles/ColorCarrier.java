/*
 * // COPYRIGHT_BEGIN
 * //
 * // The MIT License (MIT)
 * //
 * // Copyright (c) 2000 - 2016 Wizzer Works
 * //
 * // Permission is hereby granted, free of charge, to any person obtaining a copy
 * // of this software and associated documentation files (the "Software"), to deal
 * // in the Software without restriction, including without limitation the rights
 * // to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * // copies of the Software, and to permit persons to whom the Software is
 * // furnished to do so, subject to the following conditions:
 * //
 * // The above copyright notice and this permission notice shall be included in all
 * // copies or substantial portions of the Software.
 * //
 * // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * // IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * // FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * // AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * // LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * // OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * // SOFTWARE.
 * //
 * //  For information concerning this header file, contact Mark S. Millard,
 * //  of Wizzer Works at msm@wizzerworks.com.
 * //
 * //  More information concerning Wizzer Works may be found at
 * //
 * //      http://www.wizzerworks.com
 * //
 * // COPYRIGHT_END
 *
 */

// Declare package.
package com.wizzer.mle.title.hellocube.roles;

// Import Magic Lantern classes.
import com.wizzer.mle.runtime.core.IMleRole;
import com.wizzer.mle.runtime.core.MleRuntimeException;

/**
 * This class implements a carrier for propagating color properties
 * between Actors and Roles.
 */
public class ColorCarrier
{
    /**
     * Set the color from the specified floating-point array and update
     * the specified Role.
     *
     * @param role The Role to update.
     * @param values The floating-point array to obtain the values from.
     *
     * @return If the array is successfully set on the Role, then
     * <b>true</b> will be returned. Otherwise, <b>false</b> will
     * be returned.
     *
     * @throws MleRuntimeException This exception is thrown if
     * the specified parameters are <b>null</b>. It will also be
     * thrown if an error occurs while setting the property.
     */
    public static final boolean set(IMleRole role, float[] values)
            throws MleRuntimeException

    {
        if ((role != null) && (role instanceof CubeRole)) {
            if ((values != null) && (values.length == 4)) {
                ((CubeRole) role).setColor(values);
                return true;
            }
        }

        throw new MleRuntimeException("ColorCarrier: Invalid input arguments.");
    }

    /**
     * Get the color from the specified Role.
     *
     * @param role The Role to retrieve the color from.
     * @param values The scale to update.
     *
     * @return If the color is successfully retrieved from the Role, then
     * <b>true</b> will be returned. Otherwise, <b>false</b> will
     * be returned.
     *
     * @throws MleRuntimeException This exception is thrown if
     * the specified parameters are <b>null</b>. It will also be
     * thrown if an error occurs while setting the property.
     */
    public static final boolean get(IMleRole role, float[] values)
            throws MleRuntimeException
    {
        if ((role != null) && (role instanceof CubeRole)) {
            if ((values != null) && (values.length == 4)) {
                float[] color = ((CubeRole) role).getColor();
                values[0] = color[0];
                values[1] = color[1];
                values[2] = color[2];
                values[3] = color[3];
                return true;
            }
        }

        throw new MleRuntimeException("ColorCarrier: Invalid input arguments.");
    }
}
