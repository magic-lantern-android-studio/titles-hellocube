// COPYRIGHT_BEGIN
// COPYRIGHT_END

// Declare package.
package com.wizzer.mle.title.hellocube.roles;

// Import Magic Lantern classes
import com.wizzer.mle.runtime.core.IMleRole;
import com.wizzer.mle.runtime.core.MleRuntimeException;

/**
 * This class implements a carrier for propagating position properties
 * between Actors and Roles.
 */
public class PositionCarrier
{
    /**
     * Set the position from the specified floating-point array and update
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
            if ((values != null) && (values.length == 3)) {
                ((CubeRole) role).setTranslation(values);
                return true;
            }
        }

        throw new MleRuntimeException("PositionCarrier: Invalid input arguments.");
    }

    /**
     * Get the position from the specified Role.
     *
     * @param role The Role to retrieve the position from.
     * @param values The position to update.
     *
     * @return If the position is successfully retrieved from the Role, then
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
            if ((values != null) && (values.length == 3)) {
                float[] position = ((CubeRole) role).getTranslation();
                values[0] = position[0];
                values[1] = position[1];
                values[2] = position[2];
                return true;
            }
        }

        throw new MleRuntimeException("PositionCarrier: Invalid input arguments.");
    }
}
