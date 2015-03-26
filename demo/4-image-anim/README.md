使用SurfaceView的最后一个demo了。
实现了《新剑侠情缘》男主角在屏幕上到处跑。

它验证了以下几个问题；

1. js调起canvas绘图可行；
2. timer中的setInterval可行；
3. 多次绘图之间，缓存需要主动请除，不然会重影。
4. 使用rhino的“javascript host object”模式产生的类，确实是可以用的。此例中的Image对象就是用这种方式生成的。