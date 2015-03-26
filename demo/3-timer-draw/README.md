此demo验证三个问题：
1. Timer组件的setTimeout是可以用的；
2. setTimeout中的函数，是可以正常访问上下文件中的变量的，比如ctx
3. lockCanvas(dirty rect)方案是可用的