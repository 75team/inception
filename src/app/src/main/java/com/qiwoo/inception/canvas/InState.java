package com.qiwoo.inception.canvas;

/**
 * Created by liupengke on 15/5/21.
 */
public class InState {
    private static float[] strokeStyle = {0,0,0,1};
    public InState(){}

    protected static float[] getStrokeStyle(){
        return strokeStyle;
    }
}
