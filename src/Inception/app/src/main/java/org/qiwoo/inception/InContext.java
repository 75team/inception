package org.qiwoo.inception;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by liupengke on 15/4/9.
 */
public class InContext {
    private Paint paint;
    private SurfaceHolder holder;
    private InView view;
    //private Canvas canvas;
    public InContext(SurfaceHolder holder, InView view){
        this.holder = holder;
        this.view = view;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        //canvas = holder.lockCanvas();
    }
    public void setFillStyle(int a, int r, int g, int b){
        paint.setARGB(a, r, g, b);
    }
    public void fillRect(int x, int y, int w, int h){
        Rect rect = new Rect(x,y,x+w,y+h);
        Canvas canvas = holder.lockCanvas(rect);
        canvas.drawRect(x, y, x+w, y+h, paint);
        holder.unlockCanvasAndPost(canvas);
    }
    public void clearRect(int x, int y, int w, int h){
        Rect rect = new Rect(x,y,x+w,y+h);
        Canvas canvas = holder.lockCanvas(rect);
        Paint p = new Paint();
        p.setColor(Color.TRANSPARENT);
        canvas.drawRect(x, y, x+w, y+h, p);

        holder.unlockCanvasAndPost(canvas);
    }
    public void drawImage(Image img, int dx, int dy, int dw, int dh){
        if(img.bm != null){
            synchronized (holder) {
                Canvas canvas = holder.lockCanvas();
                Paint p = new Paint();
                p.setAntiAlias(true);
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                canvas.drawPaint(p);
                Rect src = new Rect(0, 0, img.jsGet_width(), img.jsGet_height());
                Rect dst = new Rect(dx, dy, dx + dw, dy + dh);
                canvas.drawBitmap(img.bm, src, dst, null);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
    public int getCanvasWidth(){
        return view.getWidth();
    }
    public int getCanvasHeight(){
        return view.getHeight();
    }
}
