package com.qiwoo.inception.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qiwoo.inception.canvas.util.FileHelper;

import org.mozilla.javascript.ScriptableObject;

import java.io.InputStream;

/**
 * Created by fumin-iri on 2015/6/10.
 */
public class Image extends ScriptableObject {

    private static final String TAG = "Image";
    private static Context context;
    private String src;
    private int width;
    private int height;
    private int naturalWidth;
    private int naturalHeight;
    private Bitmap bitmap;
    private boolean flag = false;

    public Image() {}

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String getClassName() {
        return "Image";
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
        try {
            InputStream is = FileHelper.readFromAsserts(context, src);
            if (is != null) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                bitmap = BitmapFactory.decodeStream(is);
                this.width = this.naturalWidth = bitmap.getWidth();
                this.height = this.naturalHeight = bitmap.getHeight();
            } else {
                Log.w(TAG, "InputStream could not be decoded.");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNaturalWidth() {
        return naturalWidth;
    }

    public int getNaturalHeight() {
        return naturalHeight;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Image.context = context;
    }

    public int jsGet_naturalWidth() {
        return getNaturalWidth();
    }

    public int jsGet_naturalHeight() {
        return getNaturalHeight();
    }

    public int jsGet_width() {
        return this.width;
    }

    public int jsGet_height() {
        return this.height;
    }

    public void jsSet_src(String src) {
        setSrc(src);
    }

    public String jsGet_src() {
        return getSrc();
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
