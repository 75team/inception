net.config({
    baseUrl: "/"
});
require("index",function() {
    var context = Canvas.getContext('2d');

    context.lineWidth = 30;
    context.beginPath();
    //context.arc(100, 100, 100, 0.5*Math.PI, Math.PI, false);


    context.arc(150, 150, 50, 0.5*Math.PI, 1.75*Math.PI, true);
    context.arc(300, 300, 200, 0, 2*Math.PI, false);
    context.stroke();
    return;
    setTimeout(function(){
        context.arc(200, 200, 300, 1.75*Math.PI, 0.5*Math.PI, false);
        context.stroke();
    }, 2000);
});