package org.qiwoo.inception;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liupengke on 15/4/9.
 */
public class Image extends ScriptableObject {
    private long width;
    private long height;
    private String src;
    public Bitmap bm = null;
    static InView view;

    public Image(){}
    public Image(long width, long height){
        this.width = width;
        this.height = height;
    }
    public void jsConstructor(){}

    public String getClassName(){
        return "Image";
    }

    public void jsSet_src(String src){
        this.src = src;
        try {
            InputStream is = view.getResources().getAssets().open(src);
            bm = BitmapFactory.decodeStream(is);
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public String jsGet_src(){
        return this.src;
    }

    public int jsGet_width(){
        return bm.getWidth();
    }
    public int jsGet_height(){
        return bm.getHeight();
    }
}
