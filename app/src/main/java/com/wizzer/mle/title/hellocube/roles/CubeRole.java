// COPYRIGHT_BEGIN
// COPYRIGHT_END

// Declare package.
package com.wizzer.mle.title.hellocube.roles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// Import Android classes.
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

// Import Magic Lantern classes.
import com.wizzer.mle.math.MlTransform;
import com.wizzer.mle.runtime.core.IMleRole;
import com.wizzer.mle.runtime.core.MleActor;
import com.wizzer.mle.runtime.core.MleRole;
import com.wizzer.mle.runtime.core.MleRuntimeException;

import com.wizzer.mle.parts.j3d.roles.I3dRole;
import com.wizzer.mle.parts.j3d.sets.I3dSet;

/**
 * A Magic Lantern Role that defines and renders a 3D cube.
 */
public class CubeRole extends MleRole implements I3dRole
{
    private static final String TAG = "titles-hellocube";

    /** The cube's translation property - (x, y, z). */
    protected float[] m_translation;

    /** The cube's rotation property - (angle, x, y, z). */
    protected float[] m_rotation;

    /** The cube's scale property - (x, y, z) */
    protected float[] m_scale;

    /** The cubes uniform color - (r, g, b, a). */
    protected float[] m_color;

    /* Store our model data in a float buffer. */
    private FloatBuffer m_vertices;
    private FloatBuffer m_colorPerVertex;

    /* This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /* This will be used to pass in model position information. */
    private int mPositionHandle;

    /* This will be used to pass in model color information. */
    private int mColorHandle;

    /* How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /* Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /* Size of the color data in elements. */
    private final int mColorDataSize = 4;

    /* This is a handle to our per-vertex cube shading program. */
    private int mPerVertexProgramHandle;

    /**
     * Constructor used to associate corresponding Actor.
     *
     * @param actor The Magic Lantern Actor associated with this Role.
     */
    public CubeRole(MleActor actor)
    {
        super(actor);

        m_vertices = null;
        m_colorPerVertex = null;
        m_translation = new float[3];
        m_rotation = new float[4];
        m_scale = new float[3];
        m_color = new float[4];
    }

    @Override
    public void init()
    {
        // Define points for a cube.

        // X, Y, Z
        final float[] cubePositionData =
            {
                // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                // usually represent the backside of an object and aren't visible anyways.

                // Front face
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                 1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                 1.0f, -1.0f, 1.0f,
                 1.0f, 1.0f, 1.0f,

                // Right face
                 1.0f, 1.0f, 1.0f,
                 1.0f, -1.0f, 1.0f,
                 1.0f, 1.0f, -1.0f,
                 1.0f, -1.0f, 1.0f,
                 1.0f, -1.0f, -1.0f,
                 1.0f, 1.0f, -1.0f,

                // Back face
                 1.0f, 1.0f, -1.0f,
                 1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                 1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,

                // Left face
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,

                // Top face
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                 1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                 1.0f, 1.0f, 1.0f,
                 1.0f, 1.0f, -1.0f,

                // Bottom face
                 1.0f, -1.0f, -1.0f,
                 1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                 1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
            };

        // R, G, B, A
        final float[] cubeColorData =
            {
                // Front face (red)
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                // Right face (green)
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                // Back face (blue)
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                // Left face (yellow)
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                // Top face (cyan)
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,

                // Bottom face (magenta)
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f
            };

        // Initialize the buffers.
        m_vertices = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        m_vertices.put(cubePositionData).position(0);

        m_colorPerVertex = ByteBuffer.allocateDirect(cubeColorData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        m_colorPerVertex.put(cubeColorData).position(0);
    }

    @Override
    public void dispose()
    {
        m_vertices = null;
        m_colorPerVertex = null;
    }

    // This Role does not manage any children.

    @Override
    public void addChild(IMleRole role) {}

    @Override
    public void removeChild(IMleRole role) {}

    @Override
    public IMleRole getChildAt(int index)
    {
        return null;
    }

    @Override
    public void clearChildren() {}

    @Override
    public int numChildren()
    {
        return 0;
    }

    /**
     * Set the cube's translation property.
     *
     * @param translation An array of three floating-point values representing the
     * cube's translation to set.
     */
    public synchronized void setTranslation(float[] translation)
    {
        if ((translation != null) && (translation.length == 3))
        {
            m_translation[0] = translation[0];
            m_translation[1] = translation[1];
            m_translation[2] = translation[2];
        }
    }

    /**
     * Retrieve the cube's translation property.
     *
     * @return An array of three floating-point values representing the cube's
     * translation property is returned.
     */
    public synchronized float[] getTranslation()
    { return m_translation; }

    /**
     * Set the cube's rotation property.
     *
     * @param rotation An array of four floating-point values representing the
     * cube's rotation to set.
     */
    public synchronized void setRotation(float[] rotation)
    {
        if ((rotation != null) && (rotation.length == 4))
        {
            m_rotation[0] = rotation[0];
            m_rotation[1] = rotation[1];
            m_rotation[2] = rotation[2];
            m_rotation[3] = rotation[3];
        }
    }

    /**
     * Retrieve the cube's rotation property.
     *
     * @return An array of four floating-point values representing the cube's
     * rotation property is returned.
     */
    public synchronized float[] getRotation()
    { return m_rotation; }

    /**
     * Set the cube's scale property.
     *
     * @param scale An array of three floating-point values representing the
     * cube's scale to set.
     */
    public synchronized void setScale(float[] scale)
    {
        if ((scale != null) && (scale.length == 3))
        {
            m_scale[0] = scale[0];
            m_scale[1] = scale[1];
            m_scale[2] = scale[2];
        }
    }

    /**
     * Retrieve the cube's scale property.
     *
     * @return An array of three floating-point values representing the cube's
     * scale property is returned.
     */
    public synchronized float[] getScale()
    { return m_scale; }

    /**
     * Set the cube's color property.
     *
     * @param color An array of four floating-point values representing the
     * cube's red, green, blue and alpha values to set.
     */
    public synchronized void setColor(float[] color)
    {
        if ((color != null) && (color.length == 4))
        {
            m_color[0] = color[0];  // red
            m_color[1] = color[1];  // green
            m_color[2] = color[2];  // blue
            m_color[3] = color[3];  // alpha

            // Update color buffer.
            m_colorPerVertex.position(0);
            for (int i = 0; i < 36; i++) {
                m_colorPerVertex.position(i * m_color.length);
                m_colorPerVertex.put(m_color);
            }
        }
    }

    /**
     * Retrieve the cube's color property.
     *
     * @return An array of four floating-point values representing the cube's
     * color property is returned.
     */
    public synchronized float[] getColor()
    { return m_color; }

    @Override
    public boolean setTransform(MlTransform transform)
    {
        // Not used in this title specific implementation.
        return false;
    }

    @Override
    public boolean getTransform(MlTransform transform)
    {
        // Not used in this title specific implementation.
        return false;
    }

    /**
     * Retrieve the vertex shader for the cube.
     *
     * @return The shader program is returned.
     */
    protected String getVertexShader()
    {
        final String vertexShader =
              "uniform mat4 u_MVPMatrix;      \n"    // A constant representing the combined model/view/projection matrix.

            + "attribute vec4 a_Position;     \n"    // Per-vertex position information we will pass in.
            + "attribute vec4 a_Color;        \n"    // Per-vertex color information we will pass in.

            + "varying vec4 v_Color;          \n"    // This will be passed into the fragment shader.

            + "void main()                    \n"    // The entry point for our vertex shader.
            + "{                              \n"
            + "   v_Color = a_Color;          \n"    // Pass the color through to the fragment shader.
            + "   gl_Position = u_MVPMatrix   \n"    // gl_Position is a special variable used to store the final position.
            + "               * a_Position;   \n"    // Multiply the vertex by the matrix to get the final point in
            + "}                              \n";   // normalized screen coordinates.

        return vertexShader;
    }

    /**
     * Retrieve the fragment shader for the cube.
     *
     * @return The shader program is returned.
     */
    protected String getFragmentShader()
    {
        final String fragmentShader =
              "precision mediump float;       \n"    // Set the default precision to medium. We don't need as high of a
                                                     // precision in the fragment shader.
            + "varying vec4 v_Color;          \n"    // This is the color from the vertex shader interpolated across the
                                                     // triangle per fragment.
            + "void main()                    \n"    // The entry point for our fragment shader.
            + "{                              \n"
            + "   gl_FragColor = v_Color;     \n"    // Pass the color directly through the pipeline.
            + "}                              \n";

        return fragmentShader;
    }

    /*
     * Helper function to compile a shader.
     *
     * @param shaderType The shader type.
     * @param shaderSource The shader source code.
     *
     * @return An OpenGL handle to the shader.
     */
    private int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }

    /*
     * Helper function to compile and link a program.
     *
     * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
     * @param attributes Attributes that need to be bound to the program.
     *
     * @return An OpenGL handle to the program.
     */
    private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    /*
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] m_modelMatrix = new float[16];

    /*
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] m_viewMatrix = new float[16];

    /* Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] m_projectionMatrix = new float[16];

    /* Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /**
     * Set the view matrix that will be used to render the cube.
     *
     * @param matrix An array of floating-point values representing the view matrix
     * [a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33].
     */
    public synchronized void setViewMatrix(float[] matrix)
    {
        for (int i = 0;  i < 16; i++)
            m_viewMatrix[i] = matrix[i];
    }

    /**
     * Retrieve a reference to the view matrix being used to render the cube.
     *
     * @return An array of <code>float</code> values is returned.
     */
    public synchronized float[] getViewMatrix()
    { return m_viewMatrix; }

    /**
     * Set the projection matrix that will be used to render the cube.
     *
     * @param matrix An array of floating-point values representing the projection matrix
     * [a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33].
     */
    public synchronized void setProjectionMatrix(float[] matrix)
    {
        for (int i = 0;  i < 16; i++)
            m_projectionMatrix[i] = matrix[i];
    }

    /**
     * Retrieve a reference to the projection matrix being used to render the cube.
     *
     * @return An array of <code>float</code> values is returned.
     */
    public synchronized float[] getProjectionMatrix()
    { return m_projectionMatrix; }

    /**
     * Initialize rendering.
     * <p>
     * The vertex and fragment shaders are constructed.
     * </p>
     *
     * @throws MleRuntimeException
     */
    public void initRender()
        throws MleRuntimeException
    {
        final String vertexShader = getVertexShader();
        final String fragmentShader = getFragmentShader();

        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position",  "a_Color"});

        // Update the view matrix. Note that initRender is currently only being called once from
        // the Set; therefore, if the view changes on the Set, we are not yet updating our
        // local copy of the view. ToDo: update this role's view matrix via a Magic Lantern
        // event.
        setViewMatrix(((I3dSet)m_set).getViewMatrix());

        // Update the projection matrix. Note that initRender is currently only being called once
        // from the Set; therefore, if the projection changes on the Set, we are not yet updating
        // our local copy of the projection. ToDo: update this role's projection matrix via a Magic
        // Lantern event.
        setProjectionMatrix(((I3dSet)m_set).getProjectionMatrix());
    }

    /**
     * Draw the cube.
     */
    public void render()
    {
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mPerVertexProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Color");

        // Update model matrix with cube transformations.
        Matrix.setIdentityM(m_modelMatrix, 0);
        Matrix.translateM(m_modelMatrix, 0, m_translation[0], m_translation[1], m_translation[2]);
        Matrix.rotateM(m_modelMatrix, 0, m_rotation[0], m_rotation[1], m_rotation[2], m_rotation[3]);
        Matrix.scaleM(m_modelMatrix, 0, m_scale[0], m_scale[1], m_scale[2]);

        // Pass in the position information.
        m_vertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, m_vertices);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information.
        m_colorPerVertex.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                0, m_colorPerVertex);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, m_viewMatrix, 0, m_modelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, m_projectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }
}
