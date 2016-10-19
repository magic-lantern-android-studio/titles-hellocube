// COPYRIGHT_BEGIN
// COPYRIGHT_END

// Declare package.
package com.wizzer.mle.title.hellocube.props;

// Import Magic Lantern classes
import com.wizzer.mle.runtime.core.IMleObject;
import com.wizzer.mle.runtime.core.MleActor;
import com.wizzer.mle.runtime.core.MleRuntimeException;

import com.wizzer.mle.parts.IMlePropPart;

// Import title classes.
import com.wizzer.mle.title.hellocube.roles.RotationCarrier;

import java.util.Arrays;

/**
 * This Property is used to manage parameter values for performing a rotation
 * about a given axis (x, y, z).
 */
public class RotationProperty implements IMlePropPart
{
    // The array managed by the property.
    private float[] m_values = null;

    /**
     * Default constructor.
     */
    public RotationProperty()
    {
        super();
    }

    /**
     * Get the property value.
     * <p>
     * The property is comprised of four floating-point values:
     * <br>
     * float[0] = angle in degrees
     * <br>
     * float[1] = x axis
     * <br>
     * float[2] = y axis
     * <br>
     * float[3] = z axis
     * </p>
     *
     * @return An array of 4 <code>float</code> is returned.
     */
    public float[] getProperty()
    {
        return m_values;
    }

    /**
     * Set the property value.
     * <p>
     * The property is comprised of four floating-point values:
     * <br>
     * float[0] = angle in degrees
     * <br>
     * float[1] = x axis
     * <br>
     * float[2] = y axis
     * <br>
     * float[3] = z axis
     * </p>
     *
     * @param values This argument should be an array of 4 <code>float</code>.
     */
    public void setProperty(float[] values)
    {
        m_values = new float[values.length];
        for (int i = 0; i < values.length; i++)
            m_values[i] = values[i];
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.parts.props.IMlePropPart#push(com.wizzer.mle.runtime.core.IMleObject)
     */
    public boolean push(IMleObject obj)
            throws MleRuntimeException
    {
        boolean retValue = false;

        if (obj instanceof MleActor) {
            MleActor actor = (MleActor) obj;
            retValue = RotationCarrier.set(actor.getRole(), m_values);
        }

        return retValue;
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.parts.props.IMlePropPart#pull(com.wizzer.mle.runtime.core.IMleObject)
     */
    public boolean pull(IMleObject obj)
            throws MleRuntimeException
    {
        boolean retValue = false;

        if (obj instanceof MleActor) {
            MleActor actor = (MleActor) obj;
            retValue = RotationCarrier.get(actor.getRole(), m_values);
        }

        return retValue;
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.parts.props.IMlePropPart#equals(com.wizzer.mle.parts.props.IMlePropPart)
     */
    public boolean equals(IMlePropPart property)
    {
        boolean retValue = false;

        if (property instanceof RotationProperty)
        {
            RotationProperty tmp = (RotationProperty) property;

            if (Arrays.equals(tmp.m_values, this.m_values))
                retValue = true;
        }

        return retValue;
    }
}
