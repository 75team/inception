package org.qiwoo.inception;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by liupengke on 15/4/9.
 */
public class InView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder holder;
    Thread jsRunner;
    Boolean isRun = false;
    private Context context;
    public InView(Context context){
        super(context);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);


        InScript inScript = new InScript();
        inScript.putObject("Holder", holder);
        InContext inContext = new InContext(holder, this);
        inScript.putObject("inContext", inContext);
        Thread t = new Thread(inScript);
        jsRunner = new Thread(inScript);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        if(!isRun) {

            jsRunner.start();
            isRun = true;
        }
        Log.i("info", "surface created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        //render.isRun = true;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

}
