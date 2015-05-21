package com.qiwoo.inception.canvas;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.qiwoo.inception.canvas.util.FileHelper;
import com.qiwoo.inception.canvas.util.ShaderHelper;
import com.qiwoo.inception.canvas.util.VertexArray;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * Created by liupengke on 15/5/19.
 */
public class InRender implements GLSurfaceView.Renderer {
    private ArrayList cmdList;
    private Context context;

    private int viewWidth;
    private int viewHeight;
    private float ratio;

    int mProgram, maPositionHandle, maColorHandle, muMatrixHandle;
    ScreenBuffer screenBuffer;
    boolean isBegin = true;
    //private Triangle texRect;

    private InPath path;
    public InRender( Context context, ArrayList cmdList){
        this.context = context;
        this.cmdList = cmdList;
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //texRect = new Triangle(context);
        String vertexShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/vertex_shader.glsl");
        String fragShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/fragment_shader.glsl");
        mProgram = ShaderHelper.createProgram(vertexShaderS, fragShaderS);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        //获取程序中顶点颜色属性引用id
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "a_Color");
        //获取程序中总变换矩阵引用id
        muMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        viewWidth = width;
        viewHeight = height;

        ratio = (float) width/height;
        //MatrixState.setProjectOrtho(-ratio, ratio, -1f, 1f, -1f, 1f);
        //MatrixState.setProjectOrtho(0, width, height, 0f, -1f, 1f);
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

            float[] projectionMatrix = new float[16];
            orthoM(projectionMatrix, 0, 0, (float)width, (float)viewHeight, 0f, -1f, 1f);
            Constants.setProjectionMatrix(projectionMatrix);

        screenBuffer = new ScreenBuffer(viewWidth, viewHeight, context);
        path = new InPath(mProgram, maPositionHandle, maColorHandle, muMatrixHandle);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear the rendering surface.
        //清除深度缓冲与颜色缓冲
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        screenBuffer.openBuffer();
        screenBuffer.drawPreviousFrame();


        synchronized (cmdList) {
            if(cmdList.size() > 0) {
                while (cmdList.size() > 0) {
                    ArrayList cmdItem = (ArrayList) cmdList.remove(0);
                    String cmdName = (String) cmdItem.get(0);
                    Scriptable params;

                    switch (cmdName) {
                        case "fillRect":
                            params = (Scriptable) cmdItem.get(1);
                            fillRect(params);
                            break;
                        case "moveTo":
                            params = (Scriptable) cmdItem.get(1);
                            path.moveTo(params);
                            break;
                        case "lineTo":
                            params = (Scriptable) cmdItem.get(1);
                            path.lineTo(params);
                            break;
                        case "stroke":
                            path.stroke();
                            break;
                        default:
                            Log.i("error", cmdName + " is not valid");
                    }
                }


            }
        }
        screenBuffer.drawFrame();

    }

    private void fillRect(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        float w = ((Number)params.get(2, params)).floatValue();
        float h = ((Number)params.get(3, params)).floatValue();

        float[] rectVertices = {
                x,y,
                x,y+h,
                x+w,y,
                x+w,y,
                x,y+h,
                x+w,y+h
        };

        VertexArray vaVertex = new VertexArray(rectVertices);
        //Log.i("test", Arrays.toString(rectVertices));

        VertexArray vaColor = new VertexArray(new float[]{
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0
        });




        glUseProgram(mProgram);
        vaVertex.setVertexAttribPointer(maPositionHandle,2,2*4);
        vaColor.setVertexAttribPointer(maColorHandle,3,3*4);
        glUniformMatrix4fv(muMatrixHandle, 1, false, Constants.getProjectionMatrix(), 0);
        glDrawArrays(GL_TRIANGLES,0,6);
    }
}
