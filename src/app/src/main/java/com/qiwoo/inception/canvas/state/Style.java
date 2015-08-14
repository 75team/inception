package com.qiwoo.inception.canvas.state;

import android.graphics.LinearGradient;
import android.graphics.Shader;

import java.util.ArrayList;

/**
 * Created by liupengke on 15/6/11.
 * Modify by guorui on 15/7/31. add gradient code
 */
public class Style {
    public static final int Color = 1;
    public static final int Gradient = 2;
    public static final int Pattern = 3;

    public int type;
    public int color;
    /**
     * 渐变类型
     * 1：linearGradient 线性渐变
     * 2：radialGradient 放射性渐变
     * 默认线性渐变
     */
    private int gradientType = 1;
    /* 起始坐标 */
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    /* 颜色断点 */
    private ArrayList<Integer> grdColors = new ArrayList<Integer>();
    private ArrayList<Float> grdStops = new ArrayList<Float>();
    private Shader.TileMode tile = Shader.TileMode.CLAMP;

    public Style(){
        type = Style.Color;
        color = android.graphics.Color.BLACK;
    }
    public Style(String color){
        type = Style.Color;
        this.color = android.graphics.Color.parseColor(color);
    }
    public Style(float x0, float y0, float x1, float y1){
        //线性渐变初始化
        type = Style.Gradient;
        this.gradientType = 1;
        this.startX = x0;
        this.startY = y0;
        this.endX = x1;
        this.endY = y1;
    }

    public void setColorStop(ArrayList<Float> stops, ArrayList<Integer> colors){
        //直接设置断点集合
        this.grdStops = stops;
        this.grdColors = colors;
    }

//    public void addColorStop(float stop, String color){
//        //渐变添加颜色断点
//        this.grdStops.add(stop);
//        this.grdColors.add(android.graphics.Color.parseColor(color));
//    }

    public Shader getGradientShader(){
        Shader shader = new Shader();
        //返回当前设置下的相应shader
        switch (this.gradientType){
            case 1:
                //线性渐变
                shader = new LinearGradient(this.startX, this.startY, this.endX, this.endY,
                        this.toIntArray(this.grdColors),
                        this.toFloatArray(this.grdStops),
                                            this.tile);
                break;
            case 2:
                //放射性渐变
                break;
        }

        return shader;
    }

    private int[] toIntArray(ArrayList list){
        int len = list.size();
        int[] intArray = new int[len];
        for (int i=0; i<len; i++){
            intArray[i] = (Integer)list.get(i);
        }
        return intArray;
    }
    private float[] toFloatArray(ArrayList list){
        int len = list.size();
        float[] floatArray = new float[len];
        for (int i=0; i<len; i++){
            floatArray[i] = Float.parseFloat(list.get(i).toString());
        }
        return floatArray;
    }
}
