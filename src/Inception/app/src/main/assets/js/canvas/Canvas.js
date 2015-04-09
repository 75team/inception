define("Canvas", function(){
	var context = {};
	//属性操作
	var fillStyle = "#000000";	//cite: 7 Fill and stroke styles
	context.__defineGetter__("fillStyle", function(){
	    return fillStyle;
	});
	context.__defineSetter__("fillStyle", function(val){
	    var color = new InColor(val);
        var rgba = color.getRGBA();
        fillStyle = color.toString();
        inContext.setFillStyle(rgba.a, rgba.r, rgba.g, rgba.b);
	});

	//方法
	context.fillRect = function(x, y, w, h){	//cite: 8 Drawing rectangles to the canvas
		inContext.fillRect(x, y, w, h);
	}
	context.clearRect = function(x, y, w, h){
	    inContext.clearRect(x, y, w, h);
	}
	context.drawImage = function(img, dx, dy, dw, dh){
		inContext.drawImage(img, dx, dy, dw, dh);
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
	return Canvas;
});