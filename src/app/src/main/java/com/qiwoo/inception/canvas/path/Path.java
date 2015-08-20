package com.qiwoo.inception.canvas.path;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.qiwoo.inception.canvas.Constants;
import com.qiwoo.inception.canvas.state.State;
import com.qiwoo.inception.canvas.state.Style;
import com.qiwoo.inception.canvas.util.VertexArray;

import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

/**
 *
 */
public class Path {
    private static ArrayList segmentList = new ArrayList();
    private static Context context;
    private static int mProgram, maPositionHandle, maColorHandle, muMatrixHandle;
    private static boolean haveStartPoint = false;
    private static boolean isBroken = true;
    private static float startPoint_x=0,startPoint_y=0;

    private static float lineWidth;
    private static String lineCap;

    private static float[] meshVertexes;
    private static float piFloat = (float)Math.PI;
    private static float twoPIFloat = 2*piFloat;

    public static void init(Context context, int mProgram, int maPositionHandle, int maColorHandle, int muMatrixHandle){
        Path.context = context;
        Path.mProgram = mProgram;
        Path.maPositionHandle = maPositionHandle;
        Path.maColorHandle = maColorHandle;
        Path.muMatrixHandle = muMatrixHandle;
    }
    public static void beginPath(){
        segmentList.clear();
    }
    public static void lineTo(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        if(haveStartPoint) {
            if(x!=startPoint_x || y!=startPoint_y) {
                float[] line = new float[]{
                        startPoint_x,
                        startPoint_y,
                        x,
                        y
                };

                segmentList.add(new Segment(SegmentType.LINE_TO, line, isBroken));
                isBroken = false;
            }
        }
        startPoint_x = x;
        startPoint_y = y;
        haveStartPoint = true;
    }
    public static void moveTo(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        startPoint_x = x;
        startPoint_y = y;
        isBroken = true;
        haveStartPoint = true;

        if(segmentList.size()>0){
            Segment sm = (Segment)segmentList.get(segmentList.size() - 1);
            sm.needEndCap = true;
        }
    }
    private static float[] normalizeAngles(float startAngle, float endAngle, boolean anticlockwise)
    {
        float newStartAngle = startAngle;
        if (newStartAngle < 0)
            newStartAngle = (2 * piFloat) + newStartAngle%(-2 * piFloat);
        else
            newStartAngle = newStartAngle%2 * piFloat;

        float delta = newStartAngle - startAngle;
        startAngle = newStartAngle;
        endAngle = endAngle + delta;

        if (anticlockwise && startAngle - endAngle >= 2 * piFloat)
            endAngle = startAngle - 2 * piFloat;
        else if (!anticlockwise && endAngle - startAngle >= 2 * piFloat)
            endAngle = startAngle + 2 * piFloat;

        if(!anticlockwise && endAngle < startAngle){
            endAngle = twoPIFloat + endAngle%twoPIFloat;
            if(endAngle < startAngle)
                endAngle += twoPIFloat;
        }
        if(anticlockwise && endAngle>startAngle){
            endAngle = endAngle%twoPIFloat;
            if(endAngle>startAngle)
                endAngle -= twoPIFloat;
        }
        return new float[]{startAngle, endAngle};
    }

    /**
     * 1,-1
     * 3,-3  5,-5
     *
     */
    public static void arc(Scriptable params){
        float x = ((Number)params.get(0, params)).floatValue();
        float y = ((Number)params.get(1, params)).floatValue();
        float radius = ((Number)params.get(2, params)).floatValue();
        float startAngle = ((Number)params.get(3, params)).floatValue();
        float endAngle = ((Number)params.get(4, params)).floatValue();
        boolean anticlockwise = (Boolean)params.get(5, params);
        float deltaAngle = startAngle-endAngle;

        float[] angles = normalizeAngles(startAngle, endAngle, anticlockwise);
        Log.i("info", String.valueOf(startAngle)+" "+String.valueOf(endAngle)+" "+String.valueOf(anticlockwise));
        startAngle = angles[0];
        endAngle = angles[1];
        float[] ps = {
                x,y,radius,startAngle,endAngle,anticlockwise?1:0
        };
        Log.i("info", Arrays.toString(ps));

        if(haveStartPoint){
            float to_x = x + (float)Math.cos(startAngle)*radius;
            float to_y = y + (float)Math.sin(startAngle)*radius;
            segmentList.add(new Segment(
                    SegmentType.LINE_TO,
                    new float[]{startPoint_x,startPoint_y,to_x,to_y},
                    isBroken
            ));
            isBroken = false;
        }

        if(deltaAngle != 0) {
            Log.i("info", "add arc");
            segmentList.add(new Segment(
                    SegmentType.ARC,
                    ps,
                    !haveStartPoint
            ));
        }
        haveStartPoint = true;
        startPoint_x = x + (float)Math.cos(endAngle)*radius;
        startPoint_y = y + (float)Math.sin(endAngle)*radius;
    }
    public static void quadraticCurveTo(Scriptable params){
        float cpx = ((Number)params.get(0, params)).floatValue();
        float cpy = ((Number)params.get(1, params)).floatValue();
        float x = ((Number)params.get(2, params)).floatValue();
        float y = ((Number)params.get(3, params)).floatValue();

        float x0, y0;
        if(haveStartPoint){
            x0 = startPoint_x;
            y0 = startPoint_y;
            segmentList.add(new Segment(
                    SegmentType.QUADRATIC_CURVE_TO,
                    new float[]{ x0,y0,cpx,cpy,x,y},
                    isBroken
            ));
            isBroken = false;
        }else{
            segmentList.add(new Segment(
                    SegmentType.LINE_TO,
                    new float[]{cpx,cpy,x,y},
                    isBroken
            ));
            isBroken = false;
        }
        startPoint_x = x;
        startPoint_y = y;
        haveStartPoint = true;
    }
    public static void bezierCurveTo(Scriptable params){}
    public static void stroke(){
        Style strokeStyle = State.curState.m_style;
        if(strokeStyle.type == Style.Color) {
            float r = Color.red(strokeStyle.color);
            float g = Color.green(strokeStyle.color);
            float b = Color.blue(strokeStyle.color);
            float a = 1;
            rebuildMesh();
            float[] colorVertexes = new float[meshVertexes.length/2*3];
            for(int i=0,len=meshVertexes.length/2;i<len;i++){
                colorVertexes[i*3] = r;
                colorVertexes[i*3+1] = g;
                colorVertexes[i*3+2] = b;
            }


            VertexArray vaVertex = new VertexArray(meshVertexes);
            VertexArray vaColor = new VertexArray(colorVertexes);
            glUseProgram(mProgram);
            vaVertex.setVertexAttribPointer(maPositionHandle, 2, 2 * 4);
            vaColor.setVertexAttribPointer(maColorHandle, 3, 3 * 4);
            glUniformMatrix4fv(muMatrixHandle, 1, false, Constants.getProjectionMatrix(), 0);
            glDrawArrays(GL_TRIANGLES, 0, meshVertexes.length / 2);
        }
    }


    public static void rebuildMesh(){
        lineWidth = State.curState.m_lineWidth;
        lineCap = State.curState.m_lineCap;

        meshVertexes = new float[0];
        Iterator it = segmentList.iterator();
        while (it.hasNext()) {
            Segment segment = (Segment) it.next();
            boolean needEndCap = segment.needEndCap || !it.hasNext();
            float[] params = segment.params;
            switch (segment.type){
                case LINE_TO:
                    if(segment.needStartCap){
                        buildCap(
                                new float[]{params[0]-params[2], params[1]-params[3]},
                                params[0],
                                params[1]
                        );
                    }
                    if(needEndCap){
                        buildCap(
                                new float[]{params[2]-params[0], params[3]-params[1]},
                                params[2],
                                params[3]
                        );
                    }
                    buildRectMesh(
                            params[0],
                            params[1],
                            params[2],
                            params[3],
                            lineWidth
                    );
                    break;
                case ARC:
                    float x = params[0];
                    float y = params[1];
                    float radius = params[2];
                    float startAngle = params[3];
                    float endAngle = params[4];
                    float antiClockwise = params[5];
                    if(segment.needStartCap) {
                        float start_x = x + (float) Math.cos(startAngle) * radius;
                        float start_y = y + (float) Math.sin(startAngle) * radius;
                        if(antiClockwise<1){
                            buildCap(
                                    new float[]{-(start_y-y),start_x-x},
                                    start_x,
                                    start_y
                            );
                        }else{
                            buildCap(
                                    new float[]{start_y-y, -(start_x-x)},
                                    start_x,
                                    start_y
                            );
                        }
                    }

                    if(needEndCap){
                        float end_x = x + (float)Math.cos(endAngle)*radius;
                        float end_y = x + (float)Math.sin(endAngle)*radius;
                        if(antiClockwise<1){
                            buildCap(
                                    new float[]{end_y-y, -(end_x-x)},
                                    end_x,
                                    end_y
                            );
                        }else{
                            buildCap(
                                    new float[]{-(end_y-y), end_x-x},
                                    end_x,
                                    end_y
                            );
                        }
                    }

                    buildArcMesh(x, y, radius, startAngle, endAngle, antiClockwise);

                    break;
                case QUADRATIC_CURVE_TO:
                    buildQuadraticCurveMesh(params);
                    break;
            }
        }
    }
    private static void addMesh(float[] vertexes){
        int count = meshVertexes.length;
        float[] resultVertexes = new float[count+vertexes.length];
        System.arraycopy(meshVertexes,0, resultVertexes,0,count);
        System.arraycopy(vertexes, 0, resultVertexes, count, vertexes.length);
        meshVertexes = resultVertexes;
    }

    public static void buildQuadraticCurveMesh(float[] params){
        float p0_x = params[0],
                p0_y = params[1],
                p1_x = params[2],
                p1_y = params[3],
                p2_x = params[4],
                p2_y = params[5];
        int divCount = PathUtil.getDivCount(
                PathUtil.getLineLenght(new float[]{p0_x, p0_y, p1_x, p1_y})
                        + PathUtil.getLineLenght(new float[]{p1_x, p1_y, p2_x, p2_y})
        );

        float[] vertexes = new float[2*3*2*divCount];

        float _p0_x=0,_p0_y=0,_p1_x=0,_p1_y=0;
        for(int i=0;i<=divCount;i++){
            float t = (float)i/divCount;
            float nt = 1-t;
            //当前曲线段位置
            float x = nt*nt*p0_x+2*t*nt*p1_x+t*t*p2_x;
            float y = nt*nt*p0_y+2*t*nt*p1_y+t*t*p2_y;
            //切线
            float tangent_x = (p0_x-2*p1_x+p2_x)*t-p0_x+p1_x;
            float tangent_y = (p0_y-2*p1_y+p2_y)*t-p0_y+p1_y;
            //法线
            float vectorLength = (float)Math.sqrt(tangent_x*tangent_x+tangent_y*tangent_y);
            float normal_x = -tangent_y/vectorLength*lineWidth/2;
            float normal_y = tangent_x/vectorLength*lineWidth/2;
            float _p2_x = x + normal_x;
            float _p2_y = y + normal_y;
            float _p3_x = x - normal_x;
            float _p3_y = y - normal_y;

            if(i>0){
                vertexes[12*(i-1)]    = _p0_x;
                vertexes[12*(i-1)+1]  = _p0_y;
                vertexes[12*(i-1)+2]  = _p1_x;
                vertexes[12*(i-1)+3]  = _p1_y;
                vertexes[12*(i-1)+4]  = _p3_x;
                vertexes[12*(i-1)+5]  = _p3_y;
                vertexes[12*(i-1)+6]  = _p0_x;
                vertexes[12*(i-1)+7]  = _p0_y;
                vertexes[12*(i-1)+8]  = _p3_x;
                vertexes[12*(i-1)+9]  = _p3_y;
                vertexes[12*(i-1)+10] = _p2_x;
                vertexes[12*(i-1)+11] = _p2_y;
            }
            _p0_x = _p2_x;
            _p0_y = _p2_y;
            _p1_x = _p3_x;
            _p1_y = _p3_y;
        }
        Log.i("tst", Arrays.toString(vertexes));
        addMesh(vertexes);
    }

    public static void buildRectMesh(float x, float y, float width, float height, float[] directionVector){
        float vectorLenght = PathUtil.getVectorLength(directionVector);
        float[] normalVector = PathUtil.getNormalVector(directionVector);
        float[] delta = {
                normalVector[0] * width/2,
                normalVector[1] * width/2
        };
        float x1 = x+directionVector[0]/vectorLenght*height;
        float y1 = y+directionVector[1]/vectorLenght*height;
        float[] vertexes = {
                x+delta[0],y+delta[1], x-delta[0],y-delta[1], x1-delta[0],y1-delta[1],
                x+delta[0],y+delta[1], x1-delta[0],y1-delta[1], x1+delta[0],y1+delta[1],
        };
        addMesh(vertexes);
    }
    public static void buildRectMesh(float x, float y, float x1, float y1,float width){
        float[] directionVector = {
                x1-x,
                y1-y
        };
        float[] normalVector = PathUtil.getNormalVector(directionVector);
        float[] delta = {
                normalVector[0] * width/2,
                normalVector[1] * width/2
        };
        float[] vertexes = {
                x+delta[0],y+delta[1], x-delta[0],y-delta[1], x1-delta[0],y1-delta[1],
                x+delta[0],y+delta[1], x1-delta[0],y1-delta[1], x1+delta[0],y1+delta[1],
        };
        addMesh(vertexes);
    }
    public static void buildArcMesh(float x, float y, float radius, float startAngle, float endAngle, float antiClockwise){
        int divCount = PathUtil.getDivCount(radius*(antiClockwise>0.0 ? startAngle-endAngle : endAngle-startAngle));

        float[] vertexes = new float[2*3*2*divCount];

        float _p0_x=0,_p0_y=0,_p1_x=0,_p1_y=0;
        float perAngle = (endAngle-startAngle)/divCount;
        for(int i=0;i<=divCount;i++){
            //当前曲线段位置
            float curAngle = startAngle+i*perAngle;
            float rx = (float) Math.cos(curAngle) * radius;
            float ry = (float) Math.sin(curAngle) * radius;
            float arc_x = x + rx;
            float arc_y = y + ry;
            //法线
            float[] unitVector = PathUtil.getUnitVector(new float[]{
                    rx,
                    ry
            });
            float delta_x = unitVector[0]*lineWidth/2;
            float delta_y = unitVector[1]*lineWidth/2;
            float _p2_x = arc_x + delta_x;
            float _p2_y = arc_y + delta_y;
            float _p3_x = arc_x - delta_x;
            float _p3_y = arc_y - delta_y;

            if(i>0){
                vertexes[12*(i-1)]    = _p0_x;
                vertexes[12*(i-1)+1]  = _p0_y;
                vertexes[12*(i-1)+2]  = _p1_x;
                vertexes[12*(i-1)+3]  = _p1_y;
                vertexes[12*(i-1)+4]  = _p3_x;
                vertexes[12*(i-1)+5]  = _p3_y;
                vertexes[12*(i-1)+6]  = _p0_x;
                vertexes[12*(i-1)+7]  = _p0_y;
                vertexes[12*(i-1)+8]  = _p3_x;
                vertexes[12*(i-1)+9]  = _p3_y;
                vertexes[12*(i-1)+10] = _p2_x;
                vertexes[12*(i-1)+11] = _p2_y;
            }
            _p0_x = _p2_x;
            _p0_y = _p2_y;
            _p1_x = _p3_x;
            _p1_y = _p3_y;
        }
        addMesh(vertexes);
    }
    public static void buildRoundMesh(
            float x,
            float y,
            float radius,
            double startAngle,
            double angle
    ){
        int divCount = 2*PathUtil.getDivCount(radius*angle);
        float[] vertexes = new float[divCount*2*3];
        float p0_x=0, p0_y=0;
        double perAngle = angle/divCount,
                curAngle;
        for(int i=0;i<=divCount;i++){
            if(i==divCount)
                curAngle = startAngle+angle;
            else
                curAngle = startAngle+i*perAngle;
            float p1_x = x + (float)(radius*Math.cos(curAngle));
            float p1_y = y + (float)(radius*Math.sin(curAngle));

            if(i>0){
                vertexes[6*(i-1)] = x;
                vertexes[6*(i-1)+1] = y;
                vertexes[6*(i-1)+2] = p1_x;
                vertexes[6*(i-1)+3] = p1_y;
                vertexes[6*(i-1)+4] = p0_x;
                vertexes[6*(i-1)+5] = p0_y;
            }
            p0_x = p1_x;
            p0_y = p1_y;
        }
        addMesh(vertexes);
    }
    public static void buildCap(float[] directionVector, float x, float y){
        if(lineCap.equals("square")){
            Log.i("info", "build cap");
            buildRectMesh( x, y, lineWidth, lineWidth/2, directionVector);
        }else if(lineCap.equals("round")){
            double startAngle = Math.atan2(directionVector[1], directionVector[0])-Math.PI/2;
            buildRoundMesh(x, y, lineWidth / 2, startAngle, Math.PI);
        }
    }
}
