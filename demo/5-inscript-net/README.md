目前 InScript 实现了js文件符合AMD的同步加载
并提供了 "print", "load", "defineClass", "emptyFunc" 四个方法

本例中，我们load -> DaContext、DaJSContext 两种形式的java文件
并传递了Holder对象，并进行图像的输出



实例可以直接传输

比如：
java:
DaContext
public class DaContext{
	public void change(SurfaceHolder h2){
	  Canvas canvas = h2.lockCanvas();
	}
}

DaContext dc = new DaContext();
inScript.putObject("DaContext", dc);
inScript.putObject("Holder", holder);

js:

defineClass('com.chajn.jscanvas.DaJSContext');

print(typeof DaContext); object
print(typeof Holder); object
DaContext.fillRect(Holder);         

var dc = new DaJSContext(Holder);
dc.fillRect(x, y, w, h);