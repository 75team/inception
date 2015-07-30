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
    var slice = Array.prototype.slice;
    /**
     * font设置
     */
    context.__defineSetter__("font", function(val){
        $CmdCollector.addCmd('setFont', [val]);
    });
    /**
     * 在形状内部填充的颜色或模式
     */
    context.__defineGetter__("fillStyle", function(){
        return fillStyle;
    });
//    context.__defineSetter__("fillStyle", function(val){
//        var color = new Color(val);
//        var rgba = color.getRGBA();
//        fillStyle = color.toString();
//        inContext.setFillStyle(rgba.a, rgba.r, rgba.g, rgba.b);
//    });
    context.__defineSetter__("fillStyle", function(val){
        $CmdCollector.addCmd('setFillStyle', [val]);
    });

    //线条颜色
    context.__defineGetter__("strokeStyle", function(){
        return fillStyle;
    });
    context.__defineSetter__("strokeStyle", function(val){
        $CmdCollector.addCmd('setStrokeStyle', [val]);
    });
    //线条宽度
    context.__defineGetter__("lineWidth", function(){
        return fillStyle;
    });
    context.__defineSetter__("lineWidth", function(val){
        $CmdCollector.addCmd('setLineWidth', [val]);
    });
    //线头样式
    context.__defineSetter__("lineCap", function(val){
        if(val!='butt' && val!="round" && val!="square")
            val='butt';
        $CmdCollector.addCmd('setLineCap', [val]);
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
    context.arc = function(x, y, radius, startAngle, endAngle, anticlockwise){
        if(!radius || radius<=0) return;
        anticlockwise = anticlockwise|| false;
        $CmdCollector.addCmd('arc',[x, y, radius, startAngle, endAngle, anticlockwise]);
    }
    context.quadraticCurveTo = function(cpx, cpy, x, y){
        //ensure is number,todo
        $CmdCollector.addCmd('quadraticCurveTo',[cpx, cpy, x, y]);
    }
    context.bezierCurveTo = function(cp1x, cp1y, cp2x, cp2y, x, y){
        //ensure is number, todo
        $CmdCollector.addCmd('bezierCurveTo',[cp1x, cp1y, cp2x, cp2y, x, y]);
    }
    context.fillRect = function(x, y, w, h){
        $CmdCollector.addCmd('fillRect',[x,y,w,h]);
    }
    context.fillText = function(text, x, y, maxWidth){
        if(maxWidth){
            $CmdCollector.addCmd('fillText', [text, x, y, maxWidth]);
        }else{
            $CmdCollector.addCmd('fillText', [text, x, y]);
        }
    }
    context.strokeText = function(text, x, y, maxWidth){
            if(maxWidth){
                $CmdCollector.addCmd('strokeText', [text, x, y, maxWidth]);
            }else{
                $CmdCollector.addCmd('strokeText', [text, x, y]);
            }
        }
    context.drawImage = function() {
        var len = arguments.length,
            req = [3, 5, 9],
            i = 0,
            params;

        if (!arguments[0] instanceof Image) {
            throw new Error('The provided value is not of type Image');
        }

        for (; i < req.length; i++) {
            if (len < req[i]) {
                throw new Error('Valid arities are: [3, 5, 9], but ' + len + ' arguments provided.');
            } else if (len === req[i]) {
                params = slice.call(arguments, 0);
                break;
            }
        }

        if (!params) {
            params = slice.call(arguments, 0, req[2]);
        }

        $CmdCollector.addCmd('drawImage', params);
    }
    context.clearRect = function(x, y, w, h) {
         $CmdCollector.addCmd('clearRect',[x,y,w,h]);
    }
    var Canvas =  {
        getContext: function(type){
            return context;
        }
    }
    Canvas.__defineGetter__("width", function(){
        return 640;
    });
    Canvas.__defineGetter__("height", function(){
        return 960;
    });
	global.Canvas = Canvas;
})(this);