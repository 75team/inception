net.config({
    baseUrl: "/"
});
require("index",function() {
    //获取上下文
    var ctx = Canvas.getContext('2d');
    //查询text宽度
    //将文字画到canvas上
    setTimeout(function(){

        //android默认提供“sans-serif”、“serif”、“monospace”三种字体
        /* 对照表
            default    => Typeface.DEFAULT
            bold       => Typeface.DEFAULT_BOLD
            monospace  => Typeface.MONOSPACE
            sans-serif => Typeface.SANS_SERIF
            serif      => Typeface.SERIF

        */
        ctx.font="300px monospace";

        ctx.fillStyle = 'red';
        ctx.fillText("Hello World!", 10, 100);
        ctx.fillStyle = 'green';
        ctx.fillText("FillText example!", 10, 300, 400);
        ctx.strokeStyle = 'blue';
        ctx.lineWidth = 3;
        ctx.font="300px default";
        ctx.strokeText("strokeText strokeText ...", 10, 500);
    }, 500);
});