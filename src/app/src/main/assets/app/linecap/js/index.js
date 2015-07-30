net.config({
    baseUrl: "/"
});
require("index",function() {
    var context = Canvas.getContext('2d');
    context.moveTo(100,50);
    context.lineTo(300,50);
    context.moveTo(350, 50);
    context.lineTo(550,50);
    context.lineWidth=30;
    context.stroke();
    setTimeout(function(){
        context.beginPath();
        context.lineCap = 'square';
        context.moveTo(100,100);
        context.lineTo(300,100);
        context.moveTo(350, 100);
        context.lineTo(550,100);
        context.stroke();
        setTimeout(function(){
            context.beginPath();
            context.lineCap = 'round';
            context.moveTo(100,150);
            context.lineTo(300,150);
            context.stroke();
            setTimeout(function(){
                        context.beginPath();
                        context.lineCap = 'round';
                        context.moveTo(100,350);
                        context.lineTo(300,350);
                        context.lineTo(300,750);
                        context.stroke();
                    }, 1000);
        }, 1000);
    }, 1000);
});