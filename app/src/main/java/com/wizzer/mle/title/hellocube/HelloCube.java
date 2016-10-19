// COPYRIGHT_BEGIN
// COPYRIGHT_END

// Declare package.
package com.wizzer.mle.title.hellocube;

// Import standard Java classes.
import java.io.ByteArrayInputStream;
import java.nio.ByteOrder;

// Import Android classes.
import android.app.Activity;
import android.app.ActivityManager;
import android.content.res.Resources;
import android.content.pm.ConfigurationInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

// Import Magic Lantern Math classes.
import com.wizzer.mle.math.MlMath;

// Import Magic Lantern Runtime Engine classes.
import com.wizzer.mle.runtime.MleTitle;
import com.wizzer.mle.runtime.ResourceManager;
import com.wizzer.mle.runtime.core.MleRuntimeException;
import com.wizzer.mle.runtime.core.MleSet;
import com.wizzer.mle.runtime.core.MleProp;
import com.wizzer.mle.runtime.core.MleStage;
import com.wizzer.mle.runtime.event.MleEventDispatcher;
import com.wizzer.mle.runtime.event.MleEventManager;
import com.wizzer.mle.runtime.scheduler.MlePhase;
import com.wizzer.mle.runtime.scheduler.MleScheduler;

// Import Magic Lantern J3D Parts classes;
import com.wizzer.mle.parts.MleShutdownCallback;
import com.wizzer.mle.parts.j3d.MleJ3dPlatformData;
import com.wizzer.mle.parts.stages.Mle3dStage;
import com.wizzer.mle.parts.sets.Mle3dSet;

// Import Magic Lantern title parts.
import com.wizzer.mle.title.hellocube.actors.CubeActor;
import com.wizzer.mle.title.hellocube.roles.CubeRole;

public class HelloCube extends Activity
{
   // The number of phases in the scheduler.
    private static int NUM_PHASES = 6;
    
    // The width of the stage to display.
    private static int m_width = 320;
    // The height of the stage to display.
    private static int m_height = 480;
    
    // Container for title specific data.
    private MleTitle m_title = null;

    /**
     * The main loop of execution.
     */
    protected class Mainloop extends Thread
    {
    	/**
    	 * Dispatch Magic Lantern events and process scheduled phases.
    	 * This will continue indefinitely until the application indicates
    	 * that it is Ok to exit via the <code>MleEventManager</code>.
    	 */
    	public void run()
    	{
	        while (! MleEventManager.okToExit())
	        {
	            // Process delayed events.
	            m_title.m_theDispatcher.dispatchEvents();
	        
	            // Run the scheduled phases.
	            m_title.m_theScheduler.run();
	            
                // Attempt to garbage collect.
	            System.gc();
	        }
    	}
    }
    
    // Parse the title resources.
    private boolean parseResources(Resources resources)
    {
        boolean retValue = false;
        
        if (resources != null)
        {
            // ToDo: parse resources here.
            retValue = true;
        }
        
        return retValue;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (! supportsEs2)
        {
            Log.e(MleTitle.DEBUG_TAG, "OpenGL ES 2.0 required to run this title.");
            System.exit(-1);
        }
        
        // Parse the application resources.
        if (! parseResources(getResources()))
        {
        	Log.e(MleTitle.DEBUG_TAG, "Unable to parse title resources.");
            System.exit(-1);
        }
        
        // Get a reference to the global title container.
        m_title  = MleTitle.getInstance();
        
        // Initialize the platform specific data.
        MleJ3dPlatformData platformData = new MleJ3dPlatformData();
        platformData.m_width = m_width;
        platformData.m_height = m_height;
        platformData.m_context = this;
        platformData.m_R = com.wizzer.mle.title.hellocube.R.class;
        m_title.m_platformData = platformData;
        
        // Create the event dispatcher.
        MleEventDispatcher manager = new MleEventDispatcher();
        m_title.m_theDispatcher = manager;
        
        //  Create the scheduler.
        MleScheduler scheduler = new MleScheduler(NUM_PHASES);
        MleTitle.g_theActorPhase = new MlePhase("Actor Phase");
        scheduler.addPhase(MleTitle.g_theActorPhase);
        MleTitle.g_thePostActorPhase = new MlePhase("Post Actor Phase");
        scheduler.addPhase(MleTitle.g_thePostActorPhase);
        MleTitle.g_thePreRolePhase = new MlePhase("Pre Role Phase");
        scheduler.addPhase(MleTitle.g_thePreRolePhase);
        MleTitle.g_theRolePhase = new MlePhase("Role Phase");
        scheduler.addPhase(MleTitle.g_theRolePhase);
        MleTitle.g_theSetPhase = new MlePhase("Set Phase");
        scheduler.addPhase(MleTitle.g_theSetPhase);
        MleTitle.g_theStagePhase = new MlePhase("Stage Phase");
        scheduler.addPhase(MleTitle.g_theStagePhase);
        m_title.m_theScheduler = scheduler;
        
        MleEventManager.setExitStatus(false);
     
        // Create a Stage.
        try
        {
        	Mle3dStage theStage = new Mle3dStage();
	        theStage.init();
	        
	        // Set the Activity's View.
	        setContentView(theStage.m_windowView);
	        
        } catch (MleRuntimeException ex)
        {
        	Log.e(MleTitle.DEBUG_TAG, "Unable to create and initialize the Stage.");
            System.exit(-1);
        }
    }

    /**
     * Called after onCreate(Bundle) or onStop() when the current activity is now being displayed to the user.
     * It will be followed by onResume(). 
     */
    @Override
    public void onStart()
    {
    	super.onStart();
        
        // Create a Set. The model specified by the Actor will be
        // rendered onto this Set via the Role.
        try
        {
	        Mle3dSet modelSet = new Mle3dSet();
	        modelSet.init();
	        MleSet.setCurrentSet(modelSet);
        } catch (MleRuntimeException ex)
        {
        	Log.e(MleTitle.DEBUG_TAG, "Unable to create and initialize the Set.");
            System.exit(-1);
        }

        // Create a model Actor.
        CubeActor cubeActor = new CubeActor();
        
        // Initialize the Actor's properties. Note that this will usually be done
        // by loading a Group from a Digital Workprint (Rehearsal Player) or the
        // Digital Playprint (Target Player).

        try
        {
        	// Set the 'position' property on the actor.
            byte[] position = createPositionProperty(0.0F, 0.0F, 0.0F);
            MleProp positionProp = new MleProp(position.length, new ByteArrayInputStream(position));
            cubeActor.setProperty("position", positionProp);

            // Set the 'orientation' property on the actor.
            byte[] orientation = createOrientationProperty(0.0F, 1.0F, 1.0F, 1.0F);
            MleProp orientationProp = new MleProp(orientation.length, new ByteArrayInputStream(orientation));
            cubeActor.setProperty("orientation", orientationProp);

            // Set the 'scale' property on the actor.
            byte[] scale = createScaleProperty(1.0F, 1.0F, 1.0F);
            MleProp scaleProp = new MleProp(scale.length, new ByteArrayInputStream(scale));
            cubeActor.setProperty("scale", scaleProp);
        } catch (MleRuntimeException ex)
        {
        	Log.e(MleTitle.DEBUG_TAG, "Unable to set property.");
            System.exit(-1);
        }
       
        // Create a 3D Role. This constructor will attach the Role to
        // the specified Actor. It will also be associated with the current Set.
        CubeRole cubeRole = new CubeRole(cubeActor);
        cubeRole.init();

        // Attach the Role to the Set.
        try
        {
            ((Mle3dSet)MleSet.getCurrentSet()).attachRoles(null,cubeRole);
        } catch (MleRuntimeException ex)
        {
        	Log.e(MleTitle.DEBUG_TAG, "Unable to bind Role to Set.");
            System.exit(-1);            
        }
        
        // Initialize the Actor after it has been properly bound to the Role.
        try
        {
            cubeActor.init();
        } catch (MleRuntimeException ex)
        {
        	Log.e(MleTitle.DEBUG_TAG, "Unable to initialize Actor.");
            System.exit(-1);            
        }
        
        // Install a callback for exiting the title cleanly.
        try
        {
            MleTitle.getInstance().m_theDispatcher.installEventCB(
                    MleEventManager.MLE_QUIT,new MleShutdownCallback(),null);
        } catch (MleRuntimeException ex)
        {
        	Log.e(MleTitle.DEBUG_TAG, "Unable to install shutdown callback.");
            System.exit(-1);            
        }
        
        // Return to Android application life cycle.
    }
    
    @Override
    public void onRestart()
    {
    	super.onRestart();
    	Log.i(MleTitle.DEBUG_TAG, "Received onRestart().");
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	Log.i(MleTitle.DEBUG_TAG, "Received onResume().");

        // The activity must call the GL surface view's onResume() on activity onResume().
        // This is handled indirectly by the 3D Stage because it owns the GLViewSurface.
        MleStage theStage = Mle3dStage.getInstance();
        ((Mle3dStage) theStage).resume();

        // Begin main loop execution.
        Mainloop mainloop = new Mainloop();
        mainloop.start();
    }
      
    @Override
    public void onPause()
    {
    	super.onPause();
    	Log.i(MleTitle.DEBUG_TAG, "Received onPause().");

        // The activity must call the GL surface view's onPause() on activity onPause().
        // This is handled indirectly by the 3D Stage because it owns the GLViewSurface.
        MleStage theStage = Mle3dStage.getInstance();
        ((Mle3dStage) theStage).pause();

    	// Stop the scheduler and event manager.
    	MleEventManager.setExitStatus(true);
    }
    
    @Override
    public void onStop()
    {
    	super.onStop();
    	Log.i(MleTitle.DEBUG_TAG, "Received onStop().");

        // Todo: stop the stage
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    	Log.i(MleTitle.DEBUG_TAG, "Received onDestroy().");
    }

    // Convenience utility for packing position parameters into a byte array.
    private byte[] createPositionProperty(float x, float y, float z)
        throws MleRuntimeException
    {
        byte[] property = new byte[12];

        int offset = 0;
        MlMath.convertFloatToByteArray(x, property, offset, ByteOrder.BIG_ENDIAN);
        offset += 4;
        MlMath.convertFloatToByteArray(y, property, offset, ByteOrder.BIG_ENDIAN);
        offset += 4;
        MlMath.convertFloatToByteArray(z, property, offset, ByteOrder.BIG_ENDIAN);

        return property;
    }

    // Convenience utility for packing orientation parameters into a byte array.
    private byte[] createOrientationProperty(float x, float y, float z, float w)
        throws MleRuntimeException
    {
        byte[] property = new byte[16];

        int offset = 0;
        MlMath.convertFloatToByteArray(x, property, offset, ByteOrder.BIG_ENDIAN);
        offset += 4;
        MlMath.convertFloatToByteArray(y, property, offset, ByteOrder.BIG_ENDIAN);
        offset += 4;
        MlMath.convertFloatToByteArray(z, property, offset, ByteOrder.BIG_ENDIAN);
        offset += 4;
        MlMath.convertFloatToByteArray(w, property, offset, ByteOrder.BIG_ENDIAN);

        return property;
    }

    // Convenience utility for packing scale parameters into a byte array.
    private byte[] createScaleProperty(float x, float y, float z)
       throws MleRuntimeException
    {
        byte[] property = new byte[12];

        int offset = 0;
        MlMath.convertFloatToByteArray(x, property, offset, ByteOrder.BIG_ENDIAN);
        offset += 4;
        MlMath.convertFloatToByteArray(y, property, offset, ByteOrder.BIG_ENDIAN);
        offset += 4;
        MlMath.convertFloatToByteArray(z, property, offset, ByteOrder.BIG_ENDIAN);

        return property;
    }
}