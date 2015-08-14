package com.qiwoo.inception.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.text.TextPaint;
import android.util.Log;

import com.qiwoo.inception.canvas.Constants;
import com.qiwoo.inception.canvas.state.State;
import com.qiwoo.inception.canvas.state.Style;
import com.qiwoo.inception.canvas.util.TextureHelper;
import com.qiwoo.inception.canvas.util.TextureShaderProgram;
import com.qiwoo.inception.canvas.util.VertexArray;

import org.mozilla.javascript.Scriptable;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by GR on 15/7/13.
 * 文本相关操作
 */
public class Text {
    private TextureShaderProgram textureProgram;//纹理着色器
    public Text(Context context){
        this.textureProgram = new TextureShaderProgram(context);;
    }

    /**
     * canvas中的filltext方法
     * rhino接口函数
     * @param params
     */
    public void fillText(Scriptable params){
        String text = (String)params.get(0, params);
        float dx = ((Number)params.get(1, params)).floatValue();
        float dy = ((Number)params.get(2, params)).floatValue();
        Boolean moreParam = ((Boolean)params.has(3, params));
        float maxWidth = moreParam?((Number)params.get(3, params)).floatValue(): -1.0f;
        this.fillText(text, dx, dy, maxWidth);
    }

    public void strokeText(Scriptable params){
        String text = (String)params.get(0, params);
        float dx = ((Number)params.get(1, params)).floatValue();
        float dy = ((Number)params.get(2, params)).floatValue();
        Boolean moreParam = ((Boolean)params.has(3, params));
        float maxWidth = moreParam?((Number)params.get(3, params)).floatValue(): -1.0f;
        this.strokeText(text, dx, dy, maxWidth);
    }

    /**
     * filltext的java实现
     * @param text
     * @param dx
     * @param dy
     * @param maxWidth
     */
    public void fillText(String text, float dx, float dy, float maxWidth){
        float textSize = State.curState.m_fontSize;
        Typeface typeFace = State.curState.m_typeFace;
        //文字RGB
        int R = 0;
        int G = 0;
        int B = 0;
        //位置坐标、Rect初始化
        int sx = 0;
        int sy = 0;
        int sw = 512;
        int sh = 512;
        int dw = 512;
        int dh = 512;
        //当前文字长度
        int layoutWidth = (int)Math.ceil(this.measureText(text, textSize));
        //文字高度
        Paint.FontMetrics fm = this.getFontHeight(textSize);
        int layoutHeight = (int)Math.ceil(fm.descent - fm.ascent);
        //初始化画笔
        TextPaint textPaint = new TextPaint();
        //抗锯齿效率较低
        //textPaint.setAntiAlias(true);
        //画笔颜色
        Style fillStyle = State.curState.m_fillStyle;
        if (fillStyle.type == Style.Color) {
            //AGBA
            R = Color.red(fillStyle.color);
            G = Color.green(fillStyle.color);
            B = Color.blue(fillStyle.color);
            textPaint.setARGB(255, R, G, B);
        }else if (fillStyle.type == Style.Gradient){
            //gradient
            textPaint.setShader(fillStyle.getGradientShader());
        }

        //画笔文字大小
        textPaint.setTextSize(textSize);
        //画笔字体
        textPaint.setTypeface(typeFace);
        //生成的图像大小
        sw = layoutWidth;
        sh = layoutHeight;
        dw = sw;
        dh = sh;

        Bitmap bitmap = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //背景透明
//        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawARGB(0, 255, 255, 255);
        //绘制
        int textline = (int)Math.round(-fm.ascent);
        canvas.drawText(text, 0, textline, textPaint);

        //是否需要缩放图像
        if (maxWidth>0 && maxWidth < sw){
            //限制最大宽度小于实际宽度，需要缩放
            float scaleWidth = maxWidth/sw;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, 1f);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, sw, sh, matrix,
                    true);
            //更正参数
            sw = (int)Math.ceil(maxWidth);
            dw = sw;
        }

        //处理纹理
        this.handleTexture(bitmap, sx, sy, sw, sh, dx, dy, dw, dh);
    }

    /**
     * strokeText的java实现
     * @param text
     * @param dx
     * @param dy
     * @param maxWidth
     */
    public void strokeText(String text, float dx, float dy, float maxWidth){
        //stroke宽度
        int strokeSize = (int)State.curState.m_lineWidth;
        float textSize = State.curState.m_fontSize;
        Typeface typeFace = State.curState.m_typeFace;
        //文字RGB
        int R = 0;
        int G = 0;
        int B = 0;
        //位置坐标、Rect初始化
        int sx = 0;
        int sy = 0;
        int sw = 512;
        int sh = 512;
        int dw = 512;
        int dh = 512;
        //当前文字长度
        int layoutWidth = (int)Math.ceil(this.measureText(text, textSize));
        //文字高度
        Paint.FontMetrics fm = this.getFontHeight(textSize);
        int layoutHeight = (int)Math.ceil(fm.descent - fm.ascent);
        //初始化画笔
        TextPaint strokePaint = new TextPaint();
        //抗锯齿效率较低
        //strokePaint.setAntiAlias(true);
        //stroke颜色
        Style strokeStyle = State.curState.m_strokeStyle;
        if (strokeStyle.type == Style.Color) {
            R = Color.red(strokeStyle.color);
            G = Color.green(strokeStyle.color);
            B = Color.blue(strokeStyle.color);
        }
        strokePaint.setARGB(255, R, G, B);
        strokePaint.setTextSize(textSize);
        //画笔字体
        strokePaint.setTypeface(typeFace);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeSize);

        //生成的图像大小
        sw = layoutWidth;
        sh = layoutHeight;
        dw = sw;
        dh = sh;

        Bitmap bitmap = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //背景透明
        //canvas.drawARGB(0, 0, 0, 0);
        canvas.drawARGB(0, 255, 255, 255);
        //绘制
        int textline = (int)Math.round(-fm.ascent);
        canvas.drawText(text, 0, textline, strokePaint);

        //是否需要缩放图像
        if (maxWidth>0 && maxWidth < sw){
            //限制最大宽度小于实际宽度，需要缩放
            float scaleWidth = maxWidth/sw;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, 1f);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, sw, sh, matrix,
                    true);
            //更正参数
            sw = (int)Math.ceil(maxWidth);
            dw = sw;
        }

        //处理纹理
        this.handleTexture(bitmap, sx, sy, sw, sh, dx, dy, dw, dh);
    }

    /**
     * 纹理处理
     * @param bitmap
     * @param sx
     * @param sy
     * @param sw
     * @param sh
     * @param dx
     * @param dy
     * @param dw
     * @param dh
     */
    public void handleTexture(Bitmap bitmap, int sx, int sy, int sw, int sh, float dx, float dy, int dw, int dh){
        //加载纹理
        int texture = TextureHelper.loadTexture(bitmap, sx, sy, sw, sh);
        if (texture != 0) {
            float[] VERTEX_DATA = {
                    // Order of coordinates: X, Y, S, T
                    // Triangle Fan
                    dx, dy + dh, 0, 1f, // 下
                    dx + dw, dy + dh, 1f, 1f, // 右
                    dx, dy, 0, 0, // 上
                    dx + dw, dy + dh, 1f, 1f, // 下
                    dx + dw, dy, 1f, 0, // 右
                    dx, dy, 0, 0 // 上
            };


            VertexArray va = new VertexArray(VERTEX_DATA);
            va.setVertexAttribPointer(
                    0,
                    this.textureProgram.getPositionAttributeLocation(),
                    2,
                    4 * 4);

            va.setVertexAttribPointer(
                    2,
                    this.textureProgram.getTextureCoordinatesAttributeLocation(),
                    2,
                    4 * 4);

            this.textureProgram.useProgram();
            this.textureProgram.setUniforms(Constants.getProjectionMatrix(), texture);
            glDrawArrays(GL_TRIANGLES, 0, 6);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // 解除绑定
            GLES20.glDeleteTextures(1, new int[texture], 0); // 删除纹理
        } else {
            Log.w("fillText", "fillText failed!!");
        }
    }
//    public float measureText(Scriptable params){
//        String text = (String)params.get(0, params);
//        return measureText(text);
//    }

    /**
     * 计算文本长度
     * @param text
     * @param fontSize
     * @return
     */
    public float measureText(String text, float fontSize){
        //初始化画笔
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        //字体
        paint.setTypeface(State.curState.m_typeFace);
        //字符串长度
        float textWidth = 0f;
        if (text != null && text.length()>0){
            int len = text.length();
            float[] widths = new float[len];
            //使用paint的方法
            paint.getTextWidths(text, widths);
            //遍历计算总长度
            for (int i = 0; i < len ; i++){
                textWidth += widths[i];
            }
        }
        //返回计算值
        return textWidth;
    }

    /**
     * 获取当前文字相关矩阵
     * @param fontSize
     * @return
     */
    public Paint.FontMetrics getFontHeight(float fontSize)
    {
        //初始化画笔
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        //字体
        paint.setTypeface(State.curState.m_typeFace);
        //字符矩阵
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm;
    }
}
