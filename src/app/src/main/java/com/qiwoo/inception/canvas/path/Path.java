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
    private static float startPoint_x, startPoint_y;
    private static boolean haveStartPoint = false;
    private static Context context;
    private static int mProgram, maPositionHandle, maColorHandle, muMatrixHandle;
    private static int halfCircleFragCount = 200;
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
        if(haveStartPoint) {
            float[] line = new float[]{
                    startPoint_x,
                    startPoint_y,
                    x,
                    y
            };

            lineList.add(line);
        }
        startPoint_x = x;
        startPoint_y = y;
        haveStartPoint = true;
    }
    public static void moveTo(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        startPoint_x = x;
        startPoint_y = y;
        haveStartPoint = true;
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

    public static void arc(Scriptable params){
        int fragCount;
        float end_x;
        float end_y;
        double curAngle;

        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        float radius = ((Number)params.get(2, params)).floatValue();
        float startAngle = ((Number)params.get(3, params)).floatValue();
        float endAngle = ((Number)params.get(4, params)).floatValue();
        boolean anticlockwise = (Boolean)params.get(5, params);

        startAngle = (float)(startAngle%(2*Math.PI));
        endAngle = (float)(endAngle%(2*Math.PI));
        float start_x = x + (float)Math.cos(startAngle)*radius;
        float start_y = y + (float)Math.sin(startAngle)*radius;
        //如果之前有路径，连把之前的点跟弧的开始点连起来
        if(lineList.size()>0){
            float[] line = new float[]{
                    startPoint_x,
                    startPoint_y,
                    start_x,
                    start_y
            };
            lineList.add(line);
        }
        if(anticlockwise){
            if(endAngle>startAngle)
                endAngle -= Math.PI*2;
            fragCount = ((Number) Math.ceil((startAngle - endAngle) / Math.PI * halfCircleFragCount)).intValue();
        }else {
            if (startAngle > endAngle)
                endAngle += Math.PI * 2;

            fragCount = ((Number) Math.ceil((endAngle - startAngle) / Math.PI * halfCircleFragCount)).intValue();
        }

        for(int i=1;i<fragCount;i++){
            if(anticlockwise)
                curAngle = startAngle - i*perFragRad;
            else
                curAngle = startAngle + i*perFragRad;
            end_x = x + (float)Math.cos(curAngle)*radius;
            end_y = y + (float)Math.sin(curAngle)*radius;

            float[] line = new float[]{
                    start_x,
                    start_y,
                    end_x,
                    end_y
            };
            lineList.add(line);
            start_x = end_x;
            start_y = end_y;
        }

        end_x = x + (float)Math.cos(endAngle)*radius;
        end_y = y + (float)Math.sin(endAngle)*radius;

        float[] line = new float[]{
                start_x,
                start_y,
                end_x,
                end_y
        };
        lineList.add(line);
        startPoint_x = end_x;
        startPoint_y = end_y;
        haveStartPoint = true;
    }

    public static void quadraticCurveTo(Scriptable params){
        float cpx = ((Number)params.get(0, params)).floatValue();
        float cpy = ((Number)params.get(1, params)).floatValue();
        float x = ((Number)params.get(2, params)).floatValue();
        float y = ((Number)params.get(3, params)).floatValue();
        float t, t2;
        float tx, ty;

        float x0, y0;
        if(haveStartPoint){
            x0 = startPoint_x;
            y0 = startPoint_y;
        }else{
            x0 = cpx;
            y0 = cpy;
        }

        for(int i=0;i<halfCircleFragCount;i++){
            t = (float)i/halfCircleFragCount;
            t2 = t*t;
            Log.i("curve", String.valueOf(i)+" "+String.valueOf(halfCircleFragCount)+" "+String.valueOf(i/halfCircleFragCount));

            tx = t2*(x0+x-2*cpx)+t*(2*cpx-2*x0)+x0;
            ty = t2*(y0+y-2*cpy)+t*(2*cpy-2*y0)+y0;

            float[] line = new float[]{
                    startPoint_x,
                    startPoint_y,
                    tx,
                    ty
            };
            lineList.add(line);
            startPoint_x = tx;
            startPoint_y = ty;
        }
        float[] line = new float[]{
                startPoint_x,
                startPoint_y,
                x,
                y
        };
        lineList.add(line);
        startPoint_x = x;
        startPoint_y = y;
    }

    public static void bezierCurveTo(Scriptable params){
        float cp1x = ((Number)params.get(0, params)).floatValue();
        float cp1y = ((Number)params.get(1, params)).floatValue();
        float cp2x = ((Number)params.get(2, params)).floatValue();
        float cp2y = ((Number)params.get(3, params)).floatValue();
        float x = ((Number)params.get(4, params)).floatValue();
        float y = ((Number)params.get(5, params)).floatValue();
        float t, t2, t3;
        float tx, ty;

        float x0, y0;
        if(haveStartPoint){
            x0 = startPoint_x;
            y0 = startPoint_y;
        }else{
            x0 = cp1x;
            y0 = cp1y;
        }

        //B(t)=P0*(1-t)^3+3*P1*t*(1-t)^2+3*P2*t^2*(1-t)+P3*t^3
        //    =P0*(1-3*t+3*t^2-t^3)+3*P1*(t-2*t^2+t^3)+3*P2*(t^2-t^3)+P3*t^3
        for(int i=0;i<halfCircleFragCount;i++){
            t = (float)i/halfCircleFragCount;
            t2 = t*t;
            t3 = t*t*t;

            tx = x0*(1-3*t+3*t2-t3)+3*cp1x*(t-2*t2+t3)+3*cp2x*(t2-t3)+x*t3;
            ty = y0*(1-3*t+3*t2-t3)+3*cp1y*(t-2*t2+t3)+3*cp2y*(t2-t3)+y*t3;

            float[] line = new float[]{
                    startPoint_x,
                    startPoint_y,
                    tx,
                    ty
            };
            lineList.add(line);
            startPoint_x = tx;
            startPoint_y = ty;
        }
        float[] line = new float[]{
                startPoint_x,
                startPoint_y,
                x,
                y
        };
        lineList.add(line);
        startPoint_x = x;
        startPoint_y = y;
    }
}
