(function(global){
	//属性操作
    var fillStyle = "#000000";	//cite: 7 Fill and stroke styles

    var context = {};

    context.__defineGetter__("fillStyle", function(){
        return fillStyle;
    });
    context.__defineSetter__("fillStyle", function(val){
        var color = new InColor(val);
        var rgba = color.getRGBA();
        fillStyle = color.toString();
        inContext.setFillStyle(rgba.a, rgba.r, rgba.g, rgba.b);
    });

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