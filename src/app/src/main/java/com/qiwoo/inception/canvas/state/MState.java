package com.qiwoo.inception.canvas.state;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Created by liupengke on 15/6/11.
 */
public class MState {
    //px=>sp转换使用的系数，大多数情况下是1.5
    float m_fontScale = 1.5f;

    public Style m_strokeStyle = new Style();
    public Style m_fillStyle = new Style();
    public float m_lineWidth = 1;
    //默认14sp
    public int m_fontSize = 14;
    //默认字体
    public Typeface m_typeFace = Typeface.DEFAULT;
    //m_lineCap
    //m_lineJoin
    //float m_miterLimit = 10.0;
    float m_shadowBlur = 0;
    int m_shadowColor = Color.TRANSPARENT;
    float m_globalAlpha = 1;
    //m_globalComposite
    //m_globalBlend
    //m_

    public MState(){

    }
    public MState(MState mState){
        this.m_strokeStyle = mState.m_strokeStyle;
        this.m_fillStyle = mState.m_fillStyle;
        this.m_lineWidth = mState.m_lineWidth;
        this.m_shadowBlur = mState.m_shadowBlur;
        this.m_shadowColor = mState.m_shadowColor;
        this.m_globalAlpha = mState.m_globalAlpha;
    }
}
