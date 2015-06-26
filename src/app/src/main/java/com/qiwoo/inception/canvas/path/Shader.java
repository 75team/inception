package com.qiwoo.inception.canvas.path;

import android.content.Context;
import android.opengl.GLES20;

import com.qiwoo.inception.canvas.util.FileHelper;
import com.qiwoo.inception.canvas.util.ShaderHelper;

/**
 * Created by liupengke on 15/6/25.
 */
public class Shader {
    public static int mArcProgram, maPositionHandle, maColorHandle, muMatrixHandle;
    public static void init(Context context){
        String vertexShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/vertex_arc_shader.glsl");
        String fragShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/fragment_shader.glsl");
        mArcProgram = ShaderHelper.createProgram(vertexShaderS, fragShaderS);
        /*
        maArcPositionHandle = GLES20.glGetAttribLocation(mArcProgram, "a_Position");
        //获取程序中顶点颜色属性引用id
        maArcColorHandle= GLES20.glGetAttribLocation(mArcProgram, "a_Color");
        //获取程序中总变换矩阵引用id
        muArcMatrixHandle = GLES20.glGetUniformLocation(mArcProgram, "u_Matrix");

        maArcRadiusHandle= GLES20.glGetAttribLocation(mArcProgram, "radius");
        maArcXHandle= GLES20.glGetAttribLocation(mArcProgram, "x");
        maArcYHandle= GLES20.glGetAttribLocation(mArcProgram, "y");*/
    }
}
