/**
 * Canvas对象，js环境全局对象，提供所有canvas方法和属性。
 */
(function(global){

	//属性操作
    var fillStyle = "#000000";	//cite: 7 Fill and stroke styles

    /**
     * context对象，提供绘图的所有接口
     */
    var context = {};
    /**
     * 在形状内部填充的颜色或模式
     */
    context.__defineGetter__("fillStyle", function(){
        return fillStyle;
    });
    context.__defineSetter__("fillStyle", function(val){
        var color = new InColor(val);
        var rgba = color.getRGBA();
        fillStyle = color.toString();
        inContext.setFillStyle(rgba.a, rgba.r, rgba.g, rgba.b);
    });

    //线条颜色
    context.__defineGetter__("strokeStyle", function(){
        return fillStyle;
    });
    context.__defineSetter__("strokeStyle", function(val){
        $CmdCollector.addCmd('setStrokeStyle', [val]);
    });

    //state处理
    context.save = function(){
        $CmdCollector.addCmd('save');
    }
    context.restore = function(){
        $CmdCollector.addCmd('restore');
    }
    /**
     * 开始绘制路径，清空之前路径设定
     */
    context.beginPath = function(){
        $CmdCollector.addCmd('beginPath');
    }
    context.fill = function(){
        $CmdCollector.addCmd('fill');
    }
    context.stroke = function(){
        $CmdCollector.addCmd('stroke');
    }
    context.moveTo = function(x, y){
        $CmdCollector.addCmd('moveTo',[x, y]);
    }
    context.lineTo = function(x, y){
        $CmdCollector.addCmd('lineTo', [x, y]);
    }

    context.fillRect = function(x, y, w, h){
        $CmdCollector.addCmd('fillRect',[x,y,w,h]);
    }
    var Canvas =  {
        getContext: function(type){
            return context;
        }
    }
    Canvas.__defineGetter__("width", function(){
        return inContext.getCanvasWidth();
    });
    Canvas.__defineGetter__("height", function(){
        return inContext.getCanvasHeight();
    });
	global.Canvas = Canvas;
})(this);