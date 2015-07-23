net.config({
    baseUrl: "/"
});
require("index",function() {
    var context = Canvas.getContext('2d');

    context.lineWidth = 3;
    context.beginPath();
    context.moveTo(10, 10);
    context.lineTo(88, 650);
    context.quadraticCurveTo(288, 0, 588, 650);
    context.lineWidth = 2;

    // line color

    context.strokeStyle = 'black';
    context.stroke();
    /*
    setTimeout(function(){
        context.beginPath();
        context.moveTo(188, 130);
        context.bezierCurveTo(140, 10, 388, 10, 388, 170);
        context.lineWidth = 10;

        // line color
        context.strokeStyle = 'red';
        context.stroke();
    }, 2000);*/
});