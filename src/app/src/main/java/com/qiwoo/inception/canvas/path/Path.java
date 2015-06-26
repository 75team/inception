package com.qiwoo.inception.canvas.path;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.qiwoo.inception.canvas.Constants;
import com.qiwoo.inception.canvas.state.State;
import com.qiwoo.inception.canvas.state.Style;
import com.qiwoo.inception.canvas.util.FileHelper;
import com.qiwoo.inception.canvas.util.ShaderHelper;
import com.qiwoo.inception.canvas.util.VertexArray;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by liupengke on 15/6/18.
 */
public class Path {
    private static ArrayList lineList = new ArrayList();
    private static float[] startPoint = new float[]{0,0};
    private static Context context;
    private static int mProgram, maPositionHandle, maColorHandle, muMatrixHandle;
    private static int halfCircleFragCount = 100;
    private static double perFragRad = Math.PI/halfCircleFragCount;

    public static void init(Context context, int mProgram, int maPositionHandle, int maColorHandle, int muMatrixHandle){
        Path.context = context;
        Path.mProgram = mProgram;
        Path.maPositionHandle = maPositionHandle;
        Path.maColorHandle = maColorHandle;
        Path.muMatrixHandle = muMatrixHandle;
    }

    public static void beginPath(){
        lineList.clear();
    }
    public static void lineTo(Scriptable params){
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
    }
    public static void moveTo(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        startPoint[0] = x;
        startPoint[1] = y;
    }
    public static void stroke(){
        float[] rectVertices = new float[lineList.size()*4];
        float[] colorVertices = new float[lineList.size()*6];
        Style strokeStyle = State.curState.m_strokeStyle;
        if(strokeStyle.type == Style.Color) {
            float r = Color.red(strokeStyle.color);
            float g = Color.green(strokeStyle.color);
            float b = Color.blue(strokeStyle.color);
            glLineWidth(State.curState.m_lineWidth);

            Iterator it = lineList.iterator();
            int i = 0;
            while (it.hasNext()) {
                float[] line = (float[]) it.next();
                rectVertices[4 * i] = line[0];
                rectVertices[4 * i + 1] = line[1];
                rectVertices[4 * i + 2] = line[2];
                rectVertices[4 * i + 3] = line[3];

                colorVertices[6 * i] = r;
                colorVertices[6 * i + 1] = g;
                colorVertices[6 * i + 2] = b;
                colorVertices[6 * i + 3] = r;
                colorVertices[6 * i + 4] = g;
                colorVertices[6 * i + 5] = b;
                i++;
            }

            VertexArray vaVertex = new VertexArray(rectVertices);

            VertexArray vaColor = new VertexArray(colorVertices);


            glUseProgram(mProgram);
            vaVertex.setVertexAttribPointer(maPositionHandle, 2, 2 * 4);
            vaColor.setVertexAttribPointer(maColorHandle, 3, 3 * 4);
            glUniformMatrix4fv(muMatrixHandle, 1, false, Constants.getProjectionMatrix(), 0);
            glDrawArrays(GL_LINES, 0, lineList.size() * 2);
        }
    }

    public static void _arc(Scriptable params){
        Style strokeStyle = State.curState.m_strokeStyle;
        float r = Color.red(strokeStyle.color);
        float g = Color.green(strokeStyle.color);
        float b = Color.blue(strokeStyle.color);

        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        float radius = ((Number)params.get(2, params)).floatValue();
        float startAngle = ((Number)params.get(3, params)).floatValue();
        float endAngle = ((Number)params.get(4, params)).floatValue();
        boolean anticlockwise = (Boolean)params.get(5, params);

        //startAngle = startAngle%(Math.PI*2);
        //如果之前有路径，连把之前的点跟弧的开始点连起来
        if(lineList.size()>0){

        }


    }
    public static void arc(Scriptable params){
        Style strokeStyle = State.curState.m_strokeStyle;
        float r = Color.red(strokeStyle.color);
        float g = Color.green(strokeStyle.color);
        float b = Color.blue(strokeStyle.color);

        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        float radius = ((Number)params.get(2, params)).floatValue();
        float startAngle = ((Number)params.get(3, params)).floatValue();
        float endAngle = ((Number)params.get(4, params)).floatValue();
        boolean anticlockwise = (Boolean)params.get(5, params);

        int count = ((Number)Math.ceil(Math.abs(endAngle-startAngle)*halfCircleFragCount/Math.PI)).intValue();
        float vertex[] = new float[(count+1)*2];
        float[] colorVertices = new float[(count+1)*3];
        vertex[0] = x + (float)Math.cos(startAngle)*radius;
        vertex[1] = y + (float)Math.sin(startAngle)*radius;
        for(int i=0;i<count;i++){
            vertex[i*2] = x + (float)Math.cos(startAngle + i * perFragRad)*radius;
            vertex[i*2+1] = y + (float)Math.sin(startAngle + i * perFragRad)*radius;

            colorVertices[i*3] = r;
            colorVertices[i*3+1] = g;
            colorVertices[i*3+2] = b;
        }
        vertex[count*2] = x + (float)Math.cos(endAngle)*radius;
        vertex[count*2+1] = y + (float)Math.sin(endAngle)*radius;

        colorVertices[count*3] = r;
        colorVertices[count*3+1] = g;
        colorVertices[count*3+2] = b;

        VertexArray vaVertex = new VertexArray(vertex);
        VertexArray vaColor = new VertexArray(colorVertices);


        glUseProgram(mProgram);
        vaVertex.setVertexAttribPointer(maPositionHandle, 2, 2 * 4);
        vaColor.setVertexAttribPointer(maColorHandle, 4, 4 * 4);
        glUniformMatrix4fv(muMatrixHandle, 1, false, Constants.getProjectionMatrix(), 0);

        glLineWidth(State.curState.m_lineWidth);
        glDrawArrays(GL_LINE_STRIP, 0, count+1);

    }
}
