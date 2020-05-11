package com.yu.task8;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Pyramid {

    //Geometry
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawOrderBuffer;
    private final int POSITION_COUNT = 3; //number of position coordinates


    private final int COORDINATES_PER_VERTEX = POSITION_COUNT;

    static float pyramidCoords[] = {
            0.0f,  -0.8f,0.0f,
            0.8f, 0.8f,0.8f,
            -0.8f, 0.8f,0.8f,
            -0.8f, 0.8f,-0.8f,
            0.8f, 0.8f,-0.8f,
            0.0f, 0.8f,0.0f,
    };
    private short drawOrder[] = {0,1,2,
            0,2,3,
            0,3,4,
            0,4,1,
            5,2,1,
            5,3,2,
            5,4,3,
            5,1,4};
    float color[] = { 1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 0.4f, 0.4f, 1.0f,
            0.4f, 1.0f, 0.4f, 1.0f,
            0.4f, 0.4f, 1.0f, 1.0f,
            0.4f, 1.0f, 1.0f, 1.0f,
    };

    //Rendering
    private final String vertexShaderCode =
            "attribute vec4 aPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix*aPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 uColor;" +
                    "void main() {" +
                    "  gl_FragColor = uColor;" +
                    "}";
    private final int mProgram;


    //Draw
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private final int vertexCount = pyramidCoords.length / COORDINATES_PER_VERTEX;
    private final int vertexStride = COORDINATES_PER_VERTEX * 4; // 4 bytes per vertex
    private final int TRIANGLES_COUNT = drawOrder.length/3; //3 coords for 1 triangle



    public Pyramid() {
        ByteBuffer bb = ByteBuffer.allocateDirect(pyramidCoords.length*4);//4 bytes for 1 float
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(pyramidCoords);

        //Buffer for drawOrder
        ByteBuffer orderBuffer = ByteBuffer.allocateDirect(drawOrder.length*2);//2 bytes for 1 short
        orderBuffer.order(ByteOrder.nativeOrder());
        drawOrderBuffer = orderBuffer.asShortBuffer();
        drawOrderBuffer.put(drawOrder);

        int vertexShader = OpenGLRendererPyr.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = OpenGLRendererPyr.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix){

        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        //glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false,mvpMatrix,0);

        vertexBuffer.position(0);
        drawOrderBuffer.position(0);

        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_COUNT,
                GLES20.GL_FLOAT, false,vertexStride, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);


        for(int i=0;i < TRIANGLES_COUNT;i++) {
            GLES20.glUniform4fv(mColorHandle, 1, color, i*4); //4 numbers per color

            drawOrderBuffer.position(i*3);                    //3 vertices for triangle
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3,
                    GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);
        }
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
