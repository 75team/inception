package com.qiwoo.inception.canvas;

import android.util.Log;

import com.qiwoo.inception.base.Image;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by liupengke on 15/5/19.
 * InScript调用rhino，联接js与java环境，并向js环境注入一些全局变量和方法，以实现两个环境的通讯
 */
public class InScript extends ScriptableObject implements Runnable {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(InScript.class
            .toString());

    private static final boolean silent = false;
    private static String root = "";

    private InScript support;

    public static ScriptableObject globalScope;

    public boolean isRun;
    HashMap<String, Object> objectMap = new HashMap<String, Object>();
    ArrayList classList = new ArrayList();

    public InScript(String appName){
        root = "/assets/app/"+appName+"/js/";
    }
    public InScript(){
    }

    @Override
    public String getClassName() {
        return "InScript";
    }
    public void putObject(String name, Object obj) {
        if(obj != null){
            objectMap.put(name, obj);
        }
    }
    public void putClass(Class classs) {
        //if(class != null){

        //}
    }

    public Object getObject(String name, Class<?> desiredType) {
        Object obj = ScriptableObject.getProperty(globalScope, name);
        return Context.jsToJava(obj, desiredType);
    }

    public static void print(Context cx, Scriptable thisObj, Object[] args,
                             Function funObj) {
        if (silent)
            return;
        for (int i = 0; i < args.length; i++)
            logger.info(Context.toString(args[i]));
    }

    public static void load(Context cx, Scriptable thisObj, Object[] args,
                            Function funObj) throws FileNotFoundException, IOException {
        InScript shell = (InScript) getTopLevelScope(thisObj);

        for (int i = 0; i < args.length; i++) {
            shell.processSource(cx, Context.toString(args[i]));
        }
    }

    public static void emptyFunc(Context cx, Scriptable thisObj, Object[] args,
                                 Function funObj) {
    }

    public static void defineClass(Context cx, Scriptable thisObj,
                                   Object[] args, Function funObj) throws IllegalAccessException,
            InstantiationException, InvocationTargetException {
        Class<?> clazz = getClass(args);
        if (!Scriptable.class.isAssignableFrom(clazz)) {
            logger.info("msg.must.implement.Scriptable");
        }

        InScript shell = (InScript) getTopLevelScope(thisObj);
        ScriptableObject.defineClass(shell.globalScope,
                (Class<? extends Scriptable>) clazz);

    }

    private static Class<?> getClass(Object[] args) {
        if (args.length == 0) {
            logger.warning("msg.expected.string.arg");
        }
        Object arg0 = args[0];
        if (arg0 instanceof Wrapper) {
            Object wrapped = ((Wrapper) arg0).unwrap();
            if (wrapped instanceof Class)
                return (Class<?>) wrapped;
        }
        String className = Context.toString(args[0]);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            logger.warning("msg.class.not.found:" + className);
        }
        return null;
    }

    private void processSource(Context cx, String filename) throws IOException {
        InputStream is = getInputStream(filename);
        if (is != null) {
            cx.evaluateReader(this, new InputStreamReader(is), filename, 1,
                    null);
        }else{
            logger.warning("msg.file.not.found:" + filename);
        }
    }

    private InputStream getInputStream(String filename) throws IOException {
        InputStream is = null;
        String path;
        if(filename.charAt(0)=='/')
            path = filename;
        else
            path = root + filename;

        Log.i("test", path);
        is = getClass().getResourceAsStream(path);
//			is = new FileInputStream(path);
        return is;
    }
    @Override
    public void run() {
        try {
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1);
            support = new InScript();
            globalScope = cx.initStandardObjects(support, true);

            String[] names = { "print", "load", "defineClass", "emptyFunc" };
            globalScope.defineFunctionProperties(names, globalScope.getClass(),
                    ScriptableObject.DONTENUM);

            try {
                ScriptableObject.defineClass(globalScope, Image.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Iterator it = objectMap.keySet().iterator();
            while(it.hasNext()){
                String name = (String) it.next();
                ScriptableObject.putProperty(globalScope, name, Context.javaToJS(objectMap.get(name), globalScope));
            }



            Scriptable argsObj = cx.newArray(globalScope, new Object[] {});
            globalScope.defineProperty("arguments", argsObj,
                    ScriptableObject.DONTENUM);

            String incepFoler = "/assets/inception/";
            String[] jsFiles = {incepFoler+"timer.js", incepFoler+"net.js", incepFoler+"Color.js", incepFoler+"canvas.js", "index.js" };//"r.js","loader.js"
//			FunctionObject f = (FunctionObject) globalScope.get("emptyFunc", globalScope);
//			InScript.load(cx, globalScope, jsFiles, f);
            ScriptableObject.callMethod(globalScope, "load", jsFiles);
        } finally {
            Context.exit();
        }
    }


}
