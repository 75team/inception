package com.qiwoo.inception.canvas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liupengke on 15/6/18.
 * android.graphics.Color，这个类中支持的color类型太少，rgb(255,255,255)这种类型都不支持
 * 所以我们自己做一个Color类，以兼容所有Color类型
 */
public class Color {
    public Color(String colorStr){
        colorStr = colorStr.trim();
        Pattern p = Pattern.compile("^#([a-f0-9]{6})$");
        Matcher m = p.matcher(colorStr);
        if(m.matches()){    //hex6

        }else{
            p = Pattern.compile("^#([a-f0-9]{3})$");
            m = p.matcher(colorStr);
            if(m.matches()){    //hex3

            }else{

            }
        }
    }
    public static int alpha(int color){
        return (color>>>24);
    }
    public static int red(int color){
        return (color>>16)&0xff;
    }
    public static int green(int color){
        return (color>>8)&0xff;
    }
    public static int blue(int color){
        return (color&0xff);
    }
}
