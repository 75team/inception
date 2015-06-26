package com.qiwoo.inception.canvas.state;

import android.graphics.Color;

/**
 * Created by liupengke on 15/6/11.
 */
public class MState {
    public Style m_strokeStyle = new Style();
    public Style m_fillStyle = new Style();
    public float m_lineWidth = 1;
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
