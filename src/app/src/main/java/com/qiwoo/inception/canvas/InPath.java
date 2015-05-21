package com.qiwoo.inception.canvas;

import android.util.Log;

import com.qiwoo.inception.canvas.util.VertexArray;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by liupengke on 15/5/21.
 */
public class InPath {
    private ArrayList lineList = new ArrayList();
    private float[] startPoint = new float[]{0,0};
    private int mProgram, maPositionHandle, maColorHandle, muMatrixHandle;
    public  InPath(int mProgram, int maPositionHandle, int maColorHandle, int muMatrixHandle){
        this.mProgram = mProgram;
        this.maPositionHandle = maPositionHandle;
        this.maColorHandle = maColorHandle;
        this.muMatrixHandle = muMatrixHandle;
    }
    public void lineTo(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        float[] line = new float[]{
            startPoint[0],
            startPoint[1],
            x,
            y
        };

        lineList.add(line);
        startPoint[0] = x;
        startPoint[1] = y;

        Log.i("test", Arrays.toString(startPoint));
    }
    public void moveTo(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        startPoint[0] = x;
        startPoint[1] = y;
    }
    public void stroke(){
        float[] rectVertices = new float[lineList.size()*4];
        float[] colorVertices = new float[lineList.size()*6];
        float[] strokeStyle = InState.getStrokeStyle();
        float r = strokeStyle[0];
        float g = strokeStyle[1];
        float b = strokeStyle[2];
        Iterator it = lineList.iterator();
        int i=0;
        while(it.hasNext()){
            float[] line = (float[])it.next();
            rectVertices[4*i] = line[0];
            rectVertices[4*i+1] = line[1];
            rectVertices[4*i+2] = line[2];
            rectVertices[4*i+3] = line[3];

            colorVertices[6*i] = r;
            colorVertices[6*i+1] = g;
            colorVertices[6*i+2] = b;
            colorVertices[6*i+3] = r;
            colorVertices[6*i+4] = g;
            colorVertices[6*i+5] = b;
            i++;
        }
        Log.i("test", Arrays.toString(rectVertices));

        VertexArray vaVertex = new VertexArray(rectVertices);
        //Log.i("test", Arrays.toString(rectVertices));

        VertexArray vaColor = new VertexArray(colorVertices);




        glUseProgram(mProgram);
        vaVertex.setVertexAttribPointer(maPositionHandle,2,2*4);
        vaColor.setVertexAttribPointer(maColorHandle,3,3*4);
        glUniformMatrix4fv(muMatrixHandle, 1, false, Constants.getProjectionMatrix(), 0);
        glDrawArrays(GL_LINES,0,lineList.size()*2);
    }
}
