package com.qiwoo.inception.canvas;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;

/**
 * Created by liupengke on 15/5/19.
 */
public class CmdCollector {
    private ArrayList cmdList;
    public CmdCollector(ArrayList cmdList){
        this.cmdList = cmdList;
    }
    public void addCmd(String cmdName){
        synchronized (cmdList){
            ArrayList cmdItem = new ArrayList(2);
            cmdItem.add(cmdName);
            cmdItem.add("");
            cmdList.add(cmdItem);
        }
    }
    public void addCmd(String cmdName, Scriptable params){
        synchronized (cmdList){
            ArrayList cmdItem = new ArrayList(2);
            cmdItem.add(cmdName);
            cmdItem.add(params);
            cmdList.add(cmdItem);
        }
    }
}
