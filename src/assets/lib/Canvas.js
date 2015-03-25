/**
 * 首先，请各位注意。rhino是不允许对反射的java对象进行setter和getter的，只有在js里定义的对象才可以。
 * 本接口所有实现参考w3c 2dcontext 文档，http://www.w3.org/TR/2dcontext/
 */

var Canvas = (function(){
	var context = {};
    function getARGB(color){
        return {
            A: 255,
            R: parseInt('0x'+color.substr(1,2)),
            G: parseInt('0x'+color.substr(3,2)),
            B: parseInt('0x'+color.substr(5,2))
        }
    }
	//属性操作
	var fillStyle = "#000000";	//cite: 7 Fill and stroke styles
	context.__defineGetter__("fillStyle", function(){
	    return fillStyle;
	});
	context.__defineSetter__("fillStyle", function(val){
	    fillStyle = val;
	    //DaContext.addCmd('setFileStyle', [val]);
	    var ARGB = getARGB(val);
        inContext.setFillStyle(ARGB.A, ARGB.R, ARGB.G, ARGB.B);
	});

	//方法
	context.fillRect = function(x, y, w, h){	//cite: 8 Drawing rectangles to the canvas
		//DaContext.addCmd('fillRect',[x, y, w, h]);
		inContext.fillRect(x, y, w, h);
	}

	return {
		getContext: function(type){
			return context;
		}
	}
})();