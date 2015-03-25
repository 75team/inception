/**
 * 首先，请各位注意。rhino是不允许对反射的java对象进行setter和getter的，只有在js里定义的对象才可以。
 * rhino有自己实现getter和setter的方案，需要按它的方式来。这里参用js的settter和getter
 * 本接口所有实现参考w3c 2dcontext 文档，http://www.w3.org/TR/2dcontext/
 */

var Canvas = (function(){
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

	return {
		getContext: function(type){
			return context;
		}
	}
})();