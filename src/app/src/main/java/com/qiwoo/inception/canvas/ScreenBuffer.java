package com.qiwoo.inception.canvas;

import android.content.Context;
import android.opengl.GLES20;

import com.qiwoo.inception.canvas.util.FileHelper;
import com.qiwoo.inception.canvas.util.ShaderHelper;
import com.qiwoo.inception.canvas.util.VertexArray;

import static android.opengl.GLES20.glViewport;

/**
 * Created by liupengke on 15/5/19.
 */
public class ScreenBuffer {
    private int fbo_mProgram, fbo_maPositionHandle, fbo_maTexCoorHandle, fbo_muMatrixHandle;
    private int frameBufferId, renderBufferId, ftextureId;
    //private int previous_ftextureId = -1, renderBufferId;
    private int previous_frameBufferId, previous_renderBufferId, previous_ftextureId;
    private int viewWidth, viewHeight;
    private Context context;
    public boolean hasBuffer = false;

    private boolean isBegin = true;
    public ScreenBuffer(int w, int h, Context context){
        viewWidth = w;
        viewHeight = h;
        this.context = context;

        initProgram();
        //initFRBuffer();
    }
    void initProgram(){
        String fboVertexShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/fbo_vertex_shader.glsl");
        String fboFragShaderS = FileHelper.loadFromAssetsFile(context, "inception/glsl/fbo_fragment_shader.glsl");
        fbo_mProgram = ShaderHelper.createProgram(fboVertexShaderS, fboFragShaderS);
        fbo_maPositionHandle = GLES20.glGetAttribLocation(fbo_mProgram, "a_Position");
        //获取程序中顶点颜色属性引用id
        fbo_maTexCoorHandle= GLES20.glGetAttribLocation(fbo_mProgram, "a_TexCoor");
        //获取程序中总变换矩阵引用id
        //fbo_muMatrixHandle = GLES20.glGetUniformLocation(fbo_mProgram, "u_MVPMatrix");
    }

    //首先打开framebuffer，接管系统buffer
    public void openBuffer(){
        glViewport(0,0, viewWidth, viewHeight);
        int[] tia = new int[1];
        GLES20.glGenFramebuffers(1, tia, 0);
        frameBufferId = tia[0];

        //if(isBegin) {
            GLES20.glGenRenderbuffers(1, tia, 0);
            renderBufferId = tia[0];

            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId);
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, viewWidth, viewHeight);
            isBegin = false;
        //}
        int[] tempIds = new int[1];
        GLES20.glGenTextures(1, tempIds, 0);
        ftextureId = tempIds[0];

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ftextureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                ftextureId,
                0
        );
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGB,
                viewWidth,
                viewHeight,
                0,
                GLES20.GL_RGB,
                GLES20.GL_UNSIGNED_SHORT_5_6_5,
                null
        );

        GLES20.glFramebufferRenderbuffer(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER,
                renderBufferId
        );

        //清除深度缓冲与颜色缓冲
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    }
    //使用上一帧的ftexture渲染底层图片
    public void drawPreviousFrame(){
        //Log.i("test", "why");
        if(previous_ftextureId > 0){
            //Log.i("test", "what");
            GLES20.glUseProgram(fbo_mProgram);

            VertexArray vaVertices = new VertexArray(new float[]{
                    -1,1,
                    -1,-1,
                    1,-1,
                    1,-1,
                    1,1,
                    -1,1
            });

            VertexArray vaFrag = new VertexArray(new float[]{
                    0,1,
                    0,0,
                    1,0,
                    1,0,
                    1,1,
                    0,1
            });

            vaVertices.setVertexAttribPointer(fbo_maPositionHandle, 2, 2*4);
            vaFrag.setVertexAttribPointer(fbo_maTexCoorHandle, 2, 2*4);
            //绑定纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, previous_ftextureId);

            //绘制纹理矩形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
            GLES20.glDeleteFramebuffers(1, new int[]{previous_frameBufferId},0);
            GLES20.glDeleteTextures(1, new int[]{previous_ftextureId}, 0);
            GLES20.glDeleteRenderbuffers(1, new int[]{previous_renderBufferId}, 0);

            previous_ftextureId = -1;
        }
    }
    //把buffer中texture渲染到当前帧
    public void drawFrame(){
        glViewport(0,0, viewWidth, viewHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);


        GLES20.glUseProgram(fbo_mProgram);

        VertexArray vaVertices = new VertexArray(new float[]{
                -1,1,
                -1,-1,
                1,-1,
                1,-1,
                1,1,
                -1,1
        });
        /*
        VertexArray vaFrag = new VertexArray(new float[]{
                0,0,
                0,1,
                1,1,
                1,1,
                1,0,
                0,0
        });*/
        VertexArray vaFrag = new VertexArray(new float[]{
                0,1,
                0,0,
                1,0,
                1,0,
                1,1,
                0,1
        });
        vaVertices.setVertexAttribPointer(fbo_maPositionHandle, 2, 2*4);
        vaFrag.setVertexAttribPointer(fbo_maTexCoorHandle, 2, 2*4);
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ftextureId);

        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        //GLES20.glDeleteFramebuffers(1, new int[]{frameBufferId}, 0);
        previous_frameBufferId = frameBufferId;
        previous_renderBufferId = renderBufferId;
        previous_ftextureId = ftextureId;

        //GLES20.glDeleteFramebuffers(1, new int[]{previous_frameBufferId},0);
        //GLES20.glDeleteTextures(1, new int[]{previous_ftextureId}, 0);
        //GLES20.glDeleteRenderbuffers(1, new int[]{previous_renderBufferId}, 0);
    }
}
