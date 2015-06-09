package com.qiwoo.inception.canvas.util;

import android.opengl.Matrix;

/**
 * Created by liupengke on 15/5/19.
 * 负责数组操作的简化
 */
public class MatrixState {
    private static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
    //设置正交投影参数
    public static void setProjectOrtho
    (
            float left,		//near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,		//near面距离
            float far       //far面距离
    )
    {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);

    }

    public static float[] getFinalMatrix()
    {
        return mProjMatrix;
    }
    //获取投影矩阵
    public static float[] getProjMatrix()
    {
        return mProjMatrix;
    }
}
