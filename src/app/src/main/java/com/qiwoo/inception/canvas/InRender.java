package com.qiwoo.inception.canvas;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.qiwoo.inception.base.Image;
import com.qiwoo.inception.base.Text;
import com.qiwoo.inception.canvas.path.Path;
import com.qiwoo.inception.canvas.state.State;
import com.qiwoo.inception.canvas.util.FileHelper;
import com.qiwoo.inception.canvas.util.ShaderHelper;
import com.qiwoo.inception.canvas.util.TextureShaderProgram;
import com.qiwoo.inception.canvas.util.VertexArray;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * Created by liupengke on 15/5/19.
 * Inception的主程序，它负责消费js传过来的绘图命令，并交给相应的绘图模块处理
 */
public class InRender implements GLSurfaceView.Renderer {
    private ArrayList cmdList;
    private Context context;

    private int viewWidth;
    private int viewHeight;
    private float ratio;
    private TextureShaderProgram textureProgram;

    int mProgram, maPositionHandle, maColorHandle, muMatrixHandle;
    ScreenBuffer screenBuffer;
    boolean isBegin = true;
    //private Triangle texRect;

    public InRender( Context context, ArrayList cmdList){
        this.context = context;
        this.cmdList = cmdList;
        //设置全局State中的fontScale，字体缩放比例
        State.setFontScale(this.context.getResources().getDisplayMetrics().scaledDensity);
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //编译全局的顶点和片断着色器，它可以被所有的绘图命令使用
        String vertexShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/vertex_shader.glsl");
        String fragShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/fragment_shader.glsl");
        mProgram = ShaderHelper.createProgram(vertexShaderS, fragShaderS);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        //获取程序中顶点颜色属性引用id
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "a_Color");
        //获取程序中总变换矩阵引用id
        muMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");

        DrawImage.init(this.context); // 初始化纹理绘制
        GLES20.glEnable(GL_BLEND); // 开启混色模式
        GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // 如果源色 alpha 为0，则取目标色，如果源色alpha为1，则取源色，否则视源色的alpha大小各取一部分。源色的alpha越大，则源色取的越多，最终结果源色的表现更强；源色的alpha越小，则目标色“透过”的越多。
        Image.setContext(this.context); // 绑定Image的Context，便于获取文件流
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        viewWidth = width;
        viewHeight = height;

        //正交投影短阵设定，它实现了下面两个功能
        // 1、保证我们程序中使用的坐标系元点(0,0)在左上角，跟浏览器一致，而不是opengl中统一坐标系的屏幕中心点
        // 2、保证我们操作的高宽是屏幕高宽，而不是统一坐标系的-1到1
        ratio = (float) width/height;
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

            float[] projectionMatrix = new float[16];
            orthoM(projectionMatrix, 0, 0, (float)width, (float)viewHeight, 0f, -1f, 1f);
            Constants.setProjectionMatrix(projectionMatrix);

        screenBuffer = new ScreenBuffer(viewWidth, viewHeight, context);
        Path.init(context, mProgram, maPositionHandle, maColorHandle, muMatrixHandle);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear the rendering surface.
        //清除深度缓冲与颜色缓冲
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //screenBuffer的作用很重要，它把上一帧的内容存储下来，并在绘制新一帧前重绘到屏幕上
        //以期达到“canvas中，不主动clear屏幕，内容一致存在”的效果
        screenBuffer.openBuffer();
        screenBuffer.drawPreviousFrame();


        synchronized (cmdList) {
            //命令分发消费中心，把绘图命令交给相应模块处理
            if(cmdList.size() > 0) {
                while (cmdList.size() > 0) {
                    ArrayList cmdItem = (ArrayList) cmdList.remove(0);
                    String cmdName = (String) cmdItem.get(0);
                    Scriptable params;
                    Log.i("error", cmdName + " is herer");
                    switch (cmdName) {
                        case "fillRect":
                            params = (Scriptable) cmdItem.get(1);
                            fillRect(params);
                            break;
                        case "beginPath":
                            Path.beginPath();
                            break;
                        case "moveTo":
                            params = (Scriptable) cmdItem.get(1);
                            Path.moveTo(params);
                            break;
                        case "lineTo":
                            params = (Scriptable) cmdItem.get(1);
                            Path.lineTo(params);
                            break;
                        case "stroke":
                            Path.stroke();
                            break;
                        case "save":
                            State.save();
                            break;
                        case "restore":
                            State.restore();
                            break;
                        case "setFont":
                            params = (Scriptable) cmdItem.get(1);
                            State.setFont(params);
                            break;
                        case "setStrokeStyle":
                            params = (Scriptable) cmdItem.get(1);
                            State.setStrokeStyle(params);
                            break;
                        case "setLineWidth":
                            params = (Scriptable) cmdItem.get(1);
                            State.setLineWidth(params);
                            break;
                        case "setLineCap":
                            params = (Scriptable) cmdItem.get(1);
                            State.setLineCap(params);
                            break;
                        case "setFillStyle":
                            params = (Scriptable) cmdItem.get(1);
                            State.setFillStyle(params);
                            break;
                        case "arc":
                            params = (Scriptable) cmdItem.get(1);
                            Path.arc(params);
                            break;
                        case "quadraticCurveTo":
                            params = (Scriptable) cmdItem.get(1);
                            Path.quadraticCurveTo(params);
                            break;
                        case "bezierCurveTo":
                            params = (Scriptable) cmdItem.get(1);
                            Path.bezierCurveTo(params);
                            break;
                        case "clearRect":
                            params = (Scriptable) cmdItem.get(1);
                            clearRect(params);
                            break;
                        case "drawImage":
                            params = (Scriptable) cmdItem.get(1);
                            DrawImage.drawImage(params);
                            break;
                        case "fillText":
                            params = (Scriptable) cmdItem.get(1);
                            Text text = new Text(this.context);
                            text.fillText(params);
                            break;
                        case "strokeText":
                            params = (Scriptable) cmdItem.get(1);
                            Text textStroke = new Text(this.context);
                            textStroke.strokeText(params);
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
        vaVertex.setVertexAttribPointer(maPositionHandle, 2, 2 * 4);
        vaColor.setVertexAttribPointer(maColorHandle, 3, 3 * 4);
        glUniformMatrix4fv(muMatrixHandle, 1, false, Constants.getProjectionMatrix(), 0);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public void clearRect(Scriptable params) {
        float x = ((Number) params.get(0, params)).floatValue();
        float y = ((Number) params.get(1, params)).floatValue();
        float w = ((Number) params.get(2, params)).floatValue();
        float h = ((Number) params.get(3, params)).floatValue();

        float[] rectVertices = {
                x, y,
                x, y + h,
                x + w, y,
                x + w, y,
                x, y + h,
                x + w, y + h
        };

        VertexArray vaVertex = new VertexArray(rectVertices);

        VertexArray vaColor = new VertexArray(new float[]{
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,
        });
        glUseProgram(mProgram);
        vaVertex.setVertexAttribPointer(maPositionHandle, 2, 2 * 4);
        vaColor.setVertexAttribPointer(maColorHandle, 3, 0);
        glUniformMatrix4fv(muMatrixHandle, 1, false, Constants.getProjectionMatrix(), 0);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
