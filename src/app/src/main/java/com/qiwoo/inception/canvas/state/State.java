package com.qiwoo.inception.canvas.state;

import android.graphics.Typeface;

import com.qiwoo.inception.canvas.path.Path;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 设置字体相关属性（目前可以设置字号和字体）
     * @param params
     */
    public static void setFont(Scriptable params){
        Pattern pFontSize = Pattern.compile("^([0-9]+)px$");
        //Matcher m = p.matcher(colorStr);
        String confStr = (String)params.get(0, params);
        //分割配置项
        String[] confArray = confStr.split(" ");
        for (int i=0; i<confArray.length; i++){
            String confItem = confArray[i];
            //fontSize正则
            Matcher mFontSize = pFontSize.matcher(confItem);
            if (mFontSize.matches()){
                //设置字号
                State.setFontSize(confItem);
            } else if (confArray[i].length()>0){
                //默认其他字符串为设置字体
                State.setTypeFace(confItem);
            }
        }
    }

    public static int px2sp(float pxValue) {
        float fontScale = State.curState.m_fontScale;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 设置当前设备下的fontScale
     * @param fontScale
     */
    public static void setFontScale(float fontScale){
        State.curState.m_fontScale = fontScale;
    }

    /**
     * 设置字号 px=>sp
     * @param fontSize
     */
    public static void setFontSize(String fontSize){
        //px=>sp
        curState.m_fontSize = State.px2sp(Float.parseFloat(fontSize.split("px")[0]));
    }

    /**
     * 设置字体
     * @param typeFace
     */
    public static void setTypeFace(String typeFace){
        switch (typeFace.toLowerCase()){
            case "default":
                curState.m_typeFace = Typeface.DEFAULT;
                break;
            case "default-bold":
                curState.m_typeFace = Typeface.DEFAULT_BOLD;
                break;
            case "monospace":
                curState.m_typeFace = Typeface.MONOSPACE;
                break;
            case "sans-serif":
                curState.m_typeFace = Typeface.SANS_SERIF;
                break;
            case "serif":
                curState.m_typeFace = Typeface.SERIF;
                break;
        }

    }

    /**
     * 设置stroke颜色
     * @param params
     */
    public static void setStrokeStyle(Scriptable params){
        String color = (String)params.get(0, params);
        curState.m_strokeStyle = new Style(color);
    }

    /**
     * 设置边线宽度strokeText
     * @param params
     */
    public static void setLineWidth(Scriptable params){
        float lineWidth = ((Number)params.get(0, params)).floatValue();
        curState.m_lineWidth = lineWidth;
    }

    /**
     * 设置填充颜色
     * @param params
     */
    public static void setFillStyle(Scriptable params){
        String color = (String)params.get(0, params);
        curState.m_fillStyle = new Style(color);
    }

    /**
     * 线头类型修改，重新计算path的网格
     * @param params
     */
    public static void setLineCap(Scriptable params){
        String lineCap = (String)params.get(0, params);
        curState.m_lineCap = lineCap;
        Path.rebuildMesh();
    }
}
