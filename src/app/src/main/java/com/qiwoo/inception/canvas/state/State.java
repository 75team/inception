package com.qiwoo.inception.canvas.state;

import android.graphics.Color;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;

/**
 * Created by liupengke on 15/6/11.
 */
public class State {
    private static ArrayList<MState> stateStack = new ArrayList();
    public static MState curState = new MState();


    public static void save(){
        stateStack.add(new MState(curState));
    }

    public static void restore(){
        if(stateStack.size()>0)
            curState = stateStack.remove(stateStack.size()-1);
    }

    public static void setStrokeStyle(Scriptable params){
        String color = (String)params.get(0, params);
        curState.m_strokeStyle = new Style(color);
    }
    public static void setLineWidth(Scriptable params){
        float lineWidth = ((Number)params.get(0, params)).floatValue();
        curState.m_lineWidth = lineWidth;
    }
}
