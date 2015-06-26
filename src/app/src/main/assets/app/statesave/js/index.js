net.config({
    baseUrl: "/"
});
require("index",function() {
    var context = Canvas.getContext('2d');
    context.moveTo(0,0);
    context.lineTo(400,600);
    context.strokeStyle = '#ff0000';
    context.stroke();
    setTimeout(function(){
        context.beginPath();
        context.moveTo(400,600);
        context.lineTo(800,400);
        context.save();
        context.strokeStyle = 'black';
        context.stroke();
        setTimeout(function(){
                context.beginPath();
                context.moveTo(800,400);
                context.lineTo(800,800);
                context.restore();
                context.stroke();
            }, 1000);
    }, 1000);
});