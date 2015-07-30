package com.qiwoo.inception.canvas.path;

import java.lang.Math;

/**
 * Created by liupengke on 15/7/17.
 */
public class PathUtil {
    public static int getDivCount(double length){
        int count = 0;
        double segments = length/20;
        count = (int)Math.ceil(Math.sqrt(segments * segments * 0.6+225.0));
        return count;
    }
    public static float getVectorLength(float[] vector){
        float len = (float)Math.sqrt(
                vector[0]*vector[0]
                +vector[1]*vector[1]
        );
        return len;
    }
    public static float getLineLenght(float[] points){
        return getVectorLength(
                new float[]{
                        points[2]-points[0],
                        points[3]-points[1]
                }
        );
    }
    public static float[] getNormalVector(float[] vector){
        float len = getVectorLength(vector);
        return new float[]{
                -vector[1]/len,
                vector[0]/len
        };
    }
    public static float[] getUnitVector(float[] vector){
        float len = getVectorLength(vector);
        return new float[]{
                vector[0]/len,
                vector[1]/len
        };
    }
}
