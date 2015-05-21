net.config({
    baseUrl: "/"
});
require("index",function() {
    var context = Canvas.getContext('2d');
    context.moveTo(0,0);
    context.lineTo(400,600);
    context.stroke();
    setTimeout(function(){
        context.lineTo(800,400);
        context.stroke();
        setTimeout(function(){
                context.lineTo(800,800);
                context.stroke();
            }, 1000);
    }, 1000);
});