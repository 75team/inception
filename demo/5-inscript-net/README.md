# 简介

目前 InScript 实现了单独线程进行js文件同步加载
并提供了 "print", "load", "defineClass", "emptyFunc" 四个常用方法

* print 提供打印信息，debug时可使用
* load 提供js文件加载方法，传入文件路径即可，本例专供net.js使用 （默认根地址为assets/js目录）
* defineClass 定义JAVA对象依赖，要求全部路径，将会以文件名做为原型对象输出
* emptyFunc 空包方法，备用

net.js 少数实现了符合AMD的编程规范 
_（因为requirejs在安卓下报递归太多的错误，只好自己写了个简单的）_
提供 net.config, define, require方法，待后期完善

* net.config 提供baseUrl配置，可设定js载入根目录
* define && require 
	* args[0] 为唯一id，必选；
	* args[1] 如有其他文件依赖填ids数组，如没有可直接填回调方法或对象，必选；
	* args[2] 回调方法，可选；

如：
```javascript
net.config({
    baseUrl: "app/"
});

require("loader", ["page"], function(page) {
    print('modules loaded');
});
```

# 目录结构建议

* src/org/inception/InScript.java
* assets/js/net.js
* assets/js/loader.js
* assets/js/app/page.js


# 编码规范

```javascript
define("base", function() {
    return {
    	alert:function(str){
    		print(str);
    	},
        mix: function(source, target) {
        	for(var property in source){
        		target[property] = source[property];
        	}
        	return target;
        }
    };
});

define("ui", ['base'], function(base) {
    return {
        show: function(str) {
            base.alert(str);
        }
    }
});

define("data", {
    users: ['chajn','curry'],
    members: ['admin']
});

define("page", ["data","ui"], function(data, ui) {
	var str = data.users.join(',');
    ui.show(str);
});
```


# 本例说明

本例中，我们load -> DaContext、DaJSContext 两种形式的java文件
并传递了Holder对象，并进行图像的输出


比如：
```java
DaContext
public class DaContext{
	public void change(SurfaceHolder h2){
	  Canvas canvas = h2.lockCanvas();
	}
}

DaContext dc = new DaContext();
inScript.putObject("DaContext", dc);
inScript.putObject("Holder", holder);
```

```javascript
defineClass('com.chajn.jscanvas.DaJSContext');

print(typeof DaContext); object
print(typeof Holder); object
DaContext.fillRect(Holder);         

var dc = new DaJSContext(Holder);
dc.fillRect(x, y, w, h);
```
