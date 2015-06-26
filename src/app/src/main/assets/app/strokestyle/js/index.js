net.config({
    baseUrl: "/"
});
require("index",function() {
    var context = Canvas.getContext('2d');
    context.moveTo(0,0);
    context.lineTo(400,600);
    context.strokeStyle = '#ff0000';
    context.lineWidth = 30;
    context.stroke();
    setTimeout(function(){
        context.lineTo(800,400);
        context.strokeStyle = 'black';
        context.stroke();
        setTimeout(function(){
                context.lineTo(800,800);
                context.strokeStyle = 'blue';
                context.stroke();
            }, 1000);
    }, 1000);
});