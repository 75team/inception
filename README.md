#使用方法
1. 从github上下载此项目；
2. 然后打开android studio，使用“open an existing Android Studio project”方法，打开根目录下的src文件夹
3. 等待一系列自动的构建和更新操作之后，看到工具栏上的“运行”按钮变绿，就说明项目打开正常。可以直接点击“运行”按钮，在虚拟机或手机上安装这个app了。
4. 有时候，项目打开后，看不到代码树。需要点一下左侧的导航“project”。


# 文件夹结构
根目录下有两个文件夹，一个是demo/，它里面是一些比较老的例子，可以不用看了。另一个是src/，它里面是项目所有的代码。

其中src/app/src/main目录下，是我们编辑的代码。此目录下有三个文件夹，java里是android app的java代码，assets下面是我们的js代码和opengl的着色器代码。

## java代码部分
java代码在src/app/src/main/java/com/qiwoo/inception，其中

* MainActivity.java，主activity，显示demo列表
* AppActivity.java，每个demo的主程序，它会调起inception去加载js执行
* canvas/文件夹，下面是我们inception的代码
	* CmdCollector.java, 命令集合类，它用来保存js传过来的绘图命令，然后gl的drawFrame方法会去消费它存储的命令。
	* Constants.java，inception库的一些常量定义
	* ScreenBuffer.java，负责保存上一帧内容，并且在下一帧绘制前，先把前一帧的内容绘制一遍。因为canvas是不会主动清空画板的，但gl需要在每次drawFrame前要清空画板。
	* util/，util文件夹里是跟opengGL es相关的、常用的gl es使用的方法库。比如加载着色器文件并编译成program。
	* InRender.java，Inception的主程序，它继承GLSurfaceView.Renderer，其中drawFrame方法，负责消费CmdCollector中存储的绘图命令，并且分发给相应的处理模块。
	* InScript.java, 负责使用rhino加载js文件并执行，并且把常用的变量和方法注册到js环境中。
	* InPath.java, 跟path绘图相关的操作都能这个类完成，比如moveTo\lineto\stroke等命令
	* InState.java，负责处理canvas的状保存和恢复，主要命令有save()和restore();

## assets文件
assets/下有两个文件夹，一个是app/，一个是inception/，app/放的是为了测试各个canvas命令而做的demo，每个demo都在主界面中对应一条，需要在Conf.json文件中配置一下。

inception/是下放的是inception的js和着色器代码。

* timer.js，调用java实现js中的setTimeout和setInterval方法
* net.js，加载js，计划使用amd方式
* canvas.js，对js暴露的canvas接口
* glsl/，着色器代码