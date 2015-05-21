net.config({
    baseUrl: "/"
});
require("index", function() {
    var context = Canvas.getContext('2d');
    context.fillRect(0,0,100, 100);
    setTimeout(function(){
        context.fillRect(200,200,300, 300);
    }, 1000);
});