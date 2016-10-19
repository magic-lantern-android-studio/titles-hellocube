package com.wizzer.mle.title.hellocube.actors;

import android.os.SystemClock;

import com.wizzer.mle.runtime.core.IMleProp;
import com.wizzer.mle.runtime.core.MleActor;
import com.wizzer.mle.runtime.scheduler.MleScheduler;
import com.wizzer.mle.runtime.scheduler.MlePhase;
import com.wizzer.mle.runtime.scheduler.MleTask;
import com.wizzer.mle.runtime.MleTitle;
import com.wizzer.mle.runtime.core.MleRuntimeException;

import com.wizzer.mle.math.MlMath;

import com.wizzer.mle.title.hellocube.props.ColorProperty;
import com.wizzer.mle.title.hellocube.props.PositionProperty;
import com.wizzer.mle.title.hellocube.props.RotationProperty;
import com.wizzer.mle.title.hellocube.props.ScaleProperty;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * A cube represented as an Actor.
 * <p>
 * The cube has four properties: "position", "orientation", "scale" and "color".
 * </p>
 */
public class CubeActor extends MleActor
{
    // The properties are "position", "orientation", "scale",
    // and "color".
    public PositionProperty position;
    public RotationProperty orientation;
    public ScaleProperty    scale;
    public ColorProperty    color;

    // This class is used to perform the behavior (via the Scheduler's Task).
    private class DoBehave implements Runnable
    {
        // The actor which will perform the behavior.
        private CubeActor m_actor = null;

        // Use constructor to set actor.
        public DoBehave(CubeActor actor) { m_actor = actor; }

        // Execute the behavior.
        public void run()
        {
            if (m_actor != null)
                CubeActor.behave(m_actor);
        }

        // Hide default constructor.
        private DoBehave() {}
    }

    // The behavior task executed during the Actor phase.
    private MleTask m_behaveTask = null;

    /**
     * The default constructor.
     */
    public CubeActor() { super(); }

    /* (non-Javadoc)
     * @see com.wizzer.mle.runtime.core.MleActor#init()
     */
    public void init() throws MleRuntimeException
    {
        // Update the Role by pushing the property values.
        if (color != null) color.push(this);
        update();

        // Register with the scheduler.
        MleScheduler scheduler = MleTitle.getInstance().m_theScheduler;
        MlePhase actorPhase = MleTitle.g_theActorPhase;
        if (actorPhase == null)
            throw new MleRuntimeException("CubeActor: Actor phase does not exist.");
        m_behaveTask = new MleTask(new DoBehave(this), "Do behave");
        scheduler.addTask(actorPhase, m_behaveTask);
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.runtime.core.MleActor#dispose()
     */
    public void dispose() throws MleRuntimeException
    {
        // Remove the behave function from the scheduler.
        MleScheduler scheduler = MleTitle.getInstance().m_theScheduler;
        MlePhase actorPhase = MleTitle.g_theActorPhase;
        if (actorPhase == null)
            throw new MleRuntimeException("CubeActor: Actor phase does not exist.");
        actorPhase.deleteTask(m_behaveTask);
        m_behaveTask = null;
    }

    /**
     * Update the Actor's transformation properties by pushing to the associated Role.
     */
    public void update()
    {
        try {
            // Update transform-related properties only.
            if (scale != null) scale.push(this);
            if (orientation != null) orientation.push(this);
            if (position != null) position.push(this);
        } catch (MleRuntimeException ex)
        {
            // ToDo: do we just ignore the fault?
        }
    }

    // Change in rotation; currently a constant spin around y axis.
    //static private MlRotation m_delta = null;

    static void behave(CubeActor actor)
    {
        // Orientation must be defined in order to spin.
        if ((actor == null) || (actor.orientation == null)) return;

        // Define spin parameters.

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Update rotational behavior.
        float [] rotation = actor.orientation.getProperty();
        rotation[0] = angleInDegrees;
        actor.orientation.setProperty(rotation);

        // Update associated Role.
        try {
            actor.orientation.push(actor);
        } catch (MleRuntimeException ex)
        {
            // ToDo: do we just ignore the fault?
        }
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.runtime.core.IMleObject#getProperty(java.lang.String)
     */
    public Object getProperty(String name) throws MleRuntimeException
    {
        if (name != null)
        {
            if (name.equals("position"))
                return position;
            else if (name.equals("orientation"))
                return orientation;
            else if (name.equals("scale"))
                 return scale;
            else if (name.equals("color"))
                return color;
        }

        // Specified name does not exist.
        throw new MleRuntimeException("CubeActor: Unable to get property " + name + ".");
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.runtime.core.IMleObject#setProperty(java.lang.String, IMleProp)
     */
    public void setProperty(String name, IMleProp property)
            throws MleRuntimeException
    {
        if (name != null)
        {
            try
            {
                if (name.equals("position"))
                {
                    // Read the data in from the input stream.
                    DataInputStream in = new DataInputStream(property.getStream());
                    byte[] data = new byte[property.getLength()];
                    in.readFully(data);

                    // Create a position property and initialize it.
                    position = new PositionProperty();
                    float[] translation = new float[3];
                    // Expecting 3 floating-point values in stream.
                    int offset = 0;
                    translation[0] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    offset += 4;
                    translation[1] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    offset += 4;
                    translation[2] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    position.setProperty(translation);

                    // Notify property change listeners.
                    notifyPropertyChange("position", null, null);

                    return;
                } else if (name.equals("orientation"))
                {
                    // Read the data in from the input stream.
                    DataInputStream in = new DataInputStream(property.getStream());
                    byte[] data = new byte[property.getLength()];
                    in.readFully(data);

                    // Create a rotation property and initialize it.
                    orientation = new RotationProperty();
                    float[] rotation = new float[4];
                    // Expecting 4 floating-point values in stream.
                    int offset = 0;
                    rotation[0] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    offset += 4;
                    rotation[1] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    offset += 4;
                    rotation[2] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    offset += 4;
                    rotation[3] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    orientation.setProperty(rotation);

                    // Notify property change listeners.
                    notifyPropertyChange("orientation", null, null);

                    return;
                } else if (name.equals("scale"))
                {
                    // Read the data in from the input stream.
                    DataInputStream in = new DataInputStream(property.getStream());
                    byte[] data = new byte[property.getLength()];
                    in.readFully(data);

                    // Create a scale property and initialize it.
                    scale = new ScaleProperty();
                    float[] value = new float[3];
                    // Expecting 3 floating-point values in stream.
                    int offset = 0;
                    value[0] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    offset += 4;
                    value[1] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    offset += 4;
                    value[2] = MlMath.convertByteArrayToFloat(data, offset, ByteOrder.BIG_ENDIAN);
                    scale.setProperty(value);

                    // Notify property change listeners.
                    notifyPropertyChange("scale", null, null);

                    return;
                }
            } catch (IOException ex)
            {
                throw new MleRuntimeException("CubeActor: Unable to set property " + name + ".");
            }
        }

        // Specified name does not exist.
        throw new MleRuntimeException("CubeActor: Unable to set property " + name + ".");
    }

    /* (non-Javadoc)
     * @see com.wizzer.mle.runtime.core.IMleObject#setPropertyArray(java.lang.String, int, int, java.io.ByteArrayInputStream)
     */
    public void setPropertyArray(String name, int length, int nElements, ByteArrayInputStream value) throws MleRuntimeException
    {
        throw new MleRuntimeException("CubeActor: Unable to set property array " + name + ".");
    }
}
