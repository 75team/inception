package com.qiwoo.inception.canvas;

/**
 * Created by liupengke on 15/5/19.
 */
public class Constants {
    public static final int BYTES_PER_FLOAT = 4;
    private static float[] projectionMatrix;

    public static void setProjectionMatrix(float[] matrix){
        projectionMatrix = matrix;
    }
    public static float[] getProjectionMatrix(){
        return projectionMatrix;
    }
}
