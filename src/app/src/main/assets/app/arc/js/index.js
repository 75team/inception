net.config({
    baseUrl: "/"
});
require("index",function() {
    var context = Canvas.getContext('2d');

    var centerX = 200;
    var centerY = 200;
    var radius = 100;

    context.lineWidth = 10;
    context.beginPath();
    context.arc(centerX, centerY, radius, 0, 1 * Math.PI, false);
    setTimeout(function(){
        context.arc(300, 300, 500, 0, 1 * Math.PI, false);
    }, 2000);
    //context.strokeStyle = '#003300';
    //context.stroke();
});