package com.qiwoo.inception.canvas;

/**
 * Created by liupengke on 15/5/21.
 * 实现canvas中所有跟状态相关的方法和属性处理
 */
public class InState {
    private static float[] strokeStyle = {0,0,0,1};
    public InState(){}

    protected static float[] getStrokeStyle(){
        return strokeStyle;
    }
}
