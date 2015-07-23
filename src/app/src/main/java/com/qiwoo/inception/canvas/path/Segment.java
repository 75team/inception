package com.qiwoo.inception.canvas.path;

/**
 * Created by liupengke on 15/7/17.
 */
public class Segment {
    SegmentType type;
    float[] params;
    boolean needStartCap = false;
    public Segment(SegmentType st, float[] params){
        type = st;
        this.params = params;
    }
    public Segment(SegmentType st, float[] params, boolean startCap){
        type = st;
        this.params = params;
        this.needStartCap = startCap;
    }
}
