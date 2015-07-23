package com.qiwoo.inception.canvas;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import com.qiwoo.inception.base.Image;
import com.qiwoo.inception.canvas.util.TextureHelper;
import com.qiwoo.inception.canvas.util.TextureShaderProgram;
import com.qiwoo.inception.canvas.util.VertexArray;
import org.mozilla.javascript.Scriptable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by fumin on 2015/7/7.
 */
public class DrawImage {

    private static TextureShaderProgram textureProgram;
    private static HashMap<Integer, Method> drawMethods = new HashMap<>();

    public static void init(Context context) {
        textureProgram = new TextureShaderProgram(context);
    }

    public static void drawImage(Image img, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        int texture = TextureHelper.loadTexture(img, sx, sy, sw, sh);

        if (texture != 0) {

            float[] VERTEX_DATA = {
                    // Order of coordinates: X, Y, S, T
                    // Triangle Fan
                    dx, dy + dh, 0, 1f, // ��
                    dx + dw, dy + dh, 1f, 1f, // ��
                    dx, dy, 0, 0, // ��
                    dx + dw, dy + dh, 1f, 1f, // ��
                    dx + dw, dy, 1f, 0, // ��
                    dx, dy, 0, 0 // ��
            };

            VertexArray va = new VertexArray(VERTEX_DATA);
            va.setVertexAttribPointer(
                    0,
                    textureProgram.getPositionAttributeLocation(),
                    2,
                    4 * 4);

            va.setVertexAttribPointer(
                    2,
                    textureProgram.getTextureCoordinatesAttributeLocation(),
                    2,
                    4 * 4);

            textureProgram.useProgram();
            textureProgram.setUniforms(Constants.getProjectionMatrix(), texture);
            glDrawArrays(GL_TRIANGLES, 0, 6);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // �����
            GLES20.glDeleteTextures(1, new int[texture], 0); // ɾ������
        } else {
            Log.w("drawImage", "failed!!");
        }
    }

    public static void drawImage(Image img, int dx, int dy) {
        drawImage(img, dx, dy, img.getNaturalWidth(), img.getNaturalHeight());
    }

    public static void drawImage(Image img, int dx, int dy, int dw, int dh) {
        drawImage(img, 0, 0, img.getNaturalWidth(), img.getNaturalHeight(), dx, dy, dw, dh);
    }

    public static void drawImage(Scriptable params) {
        int len = params.getIds().length;
        Object[] parameters;
        Method method = drawMethods.get(len); // ���ݲ��������ҵ���Ӧ�ķ���

        if (null != method) {
            parameters = new Object[len];

            // ��װ����
            for (int i = 0; i < len; i++) {
                if (i == 0) {
                    parameters[0] = params.get(0, params);
                } else {
                    parameters[i] = ((Number) params.get(i, params)).intValue();
                }
            }

            try {
                // �������
                method.invoke(null, parameters);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("error", "can not find the real method");
        }
    }

    static {
        // ��̬��Ĭִ��, ��ȡ����drawImage�ķ������������
        Method[] methods = DrawImage.class.getMethods();
        Method method;
        Class<?> classes[];

        for (int i = 0; i < methods.length; i++) {
            method = methods[i];
            if (method.getName().equals("drawImage")) {
                classes = method.getParameterTypes();
                drawMethods.put(classes.length, method);
            }
        }
    }
}
