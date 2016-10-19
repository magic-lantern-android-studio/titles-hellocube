// COPYRIGHT_BEGIN
// COPYRIGHT_END

// Declare package.
package com.wizzer.mle.title.hellocube.props;

// Import Magic Lantern classes.
import com.wizzer.mle.parts.IMlePropPart;
import com.wizzer.mle.runtime.core.IMleObject;
import com.wizzer.mle.runtime.core.MleActor;
import com.wizzer.mle.runtime.core.MleRuntimeException;
import com.wizzer.mle.title.hellocube.roles.ScaleCarrier;

import java.util.Arrays;

/**
 * This Property is used to perform a scaling transformation.
 */
public class ScaleProperty implements IMlePropPart
{
    // The array managed by the property.
    private float[] m_values = null;

    /**
     * Default constructor.
     */
    public ScaleProperty()
    {
        super();
    }

    /**
     * Get the property value.
     * <p>
     * The property is comprised of three floating-point values:
     * <br>
     * float[0] = sx
     * <br>
     * float[1] = sy
     * <br>
     * float[2] = sz
     * </p>
     *
     * @return An array of <code>float</code> is returned.
     */
    public float[] getProperty()
    {
        return m_values;
    }

    /**
     * Set the property value.
     * <p>
     * The property is comprised of three floating-point values:
     * <br>
     * float[0] = sx
     * <br>
     * float[1] = sy
     * <br>
     * float[2] = sz
     * </p>
     *
     * @param values This argument should be an array of <code>float</code>.
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
            retValue = ScaleCarrier.set(actor.getRole(), m_values);
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
            retValue = ScaleCarrier.get(actor.getRole(), m_values);
        }

        return retValue;
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.parts.props.IMlePropPart#equals(com.wizzer.mle.parts.props.IMlePropPart)
     */
    public boolean equals(IMlePropPart property)
    {
        boolean retValue = false;

        if (property instanceof ScaleProperty)
        {
            ScaleProperty tmp = (ScaleProperty) property;

            if (Arrays.equals(tmp.m_values, this.m_values))
                retValue = true;
        }

        return retValue;
    }
}
