//本测试用例的入口程序
//它实现在界面中连续画两个方框，中间相隔一秒
net.config({
    baseUrl: "/"
});
require("index", function() {
    var context = Canvas.getContext('2d');
    context.fillRect(0,0,100, 100);
    setTimeout(function(){
        context.fillRect(200,200,300, 300);
    }, 1000);

    $in.callRPC("canvas.RPCTest.Test", function(){
        print('aaaaaaaaaaaa');
    });
});