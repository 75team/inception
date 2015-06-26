package com.qiwoo.inception.canvas.state;

/**
 * Created by liupengke on 15/6/11.
 */
public class Style {
    public static final int Color = 1;
    public static final int Gradient = 2;
    public static final int Pattern = 3;

    public int type;
    public int color;
    public Style(){
        type = Style.Color;
        color = android.graphics.Color.BLACK;
    }
    public Style(String color){
        type = Style.Color;
        this.color = android.graphics.Color.parseColor(color);
    }
}
