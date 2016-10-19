// COPYRIGHT_BEGIN
// COPYRIGHT_END

// Declare package.
package com.wizzer.mle.title.hellocube.props;

// Import Magic Lantern classes.
import com.wizzer.mle.runtime.core.IMleObject;
import com.wizzer.mle.runtime.core.MleActor;
import com.wizzer.mle.runtime.core.MleRuntimeException;

import com.wizzer.mle.parts.IMlePropPart;

// Import title classes.
import com.wizzer.mle.title.hellocube.roles.PositionCarrier;

import java.util.Arrays;

/**
 *
 */
public class PositionProperty implements IMlePropPart
{
    // The array managed by the property.
    private float[] m_values = null;

    /**
     * Default constructor.
     */
    public PositionProperty()
    {
        super();
    }

    /**
     * Get the property value.
     *
     * @return An array of <code>float</code> is returned.
     */
    public float[] getProperty()
    {
        return m_values;
    }

    /**
     * Set the property value.
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
            retValue = PositionCarrier.set(actor.getRole(), m_values);
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
            retValue = PositionCarrier.get(actor.getRole(), m_values);
        }

        return retValue;
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.parts.props.IMlePropPart#equals(com.wizzer.mle.parts.props.IMlePropPart)
     */
    public boolean equals(IMlePropPart property)
    {
        boolean retValue = false;

        if (property instanceof PositionProperty)
        {
            PositionProperty tmp = (PositionProperty) property;

            if (Arrays.equals(tmp.m_values, this.m_values))
                retValue = true;
        }

        return retValue;
    }
}
