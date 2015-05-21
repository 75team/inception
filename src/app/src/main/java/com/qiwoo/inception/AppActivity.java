package com.qiwoo.inception;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.qiwoo.inception.canvas.CmdCollector;
import com.qiwoo.inception.canvas.InRender;
import com.qiwoo.inception.canvas.InScript;
import com.qiwoo.inception.canvas.util.GLHelper;

import java.util.ArrayList;


public class AppActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    boolean isGlSet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        if(GLHelper.checkSupportsEs2(this)){
            isGlSet = true;
            glSurfaceView.setEGLContextClientVersion(2);


            Intent intent = getIntent();
            String appName = intent.getStringExtra("appName");

            ArrayList cmdList = new ArrayList();

            glSurfaceView.setRenderer(new InRender(this, cmdList));


            CmdCollector cc = new CmdCollector(cmdList);
            InScript inScript = new InScript(appName);
            inScript.putObject("$CmdCollector", cc);
            Thread t = new Thread(inScript);
            t.start();
        }
        setContentView(glSurfaceView);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isGlSet)
            glSurfaceView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isGlSet)
            glSurfaceView.onResume();

    }
}
