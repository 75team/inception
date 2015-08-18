net.config({
    baseUrl: "/"
});
require("index",function() {
    //获取上下文
    var ctx = Canvas.getContext('2d');
    ctx.font="300px monospace";
    //设置线性渐变
    var grd = ctx.createLinearGradient(0, 0, 660, 117);
    grd.addColorStop(0, 'red');
    grd.addColorStop(.5, 'green');
    grd.addColorStop(1, 'blue');

    ctx.fillStyle = grd;
    ctx.fillText("linearGradient",10,100);

    ctx.strokeStyle = grd;
    ctx.lineWidth = 3;
    ctx.strokeText("strokeText", 10, 300);

    //放射性渐变
    var grd2 = ctx.createRadialGradient(330, 50, 330);
    grd2.addColorStop(0, 'red');
    grd2.addColorStop(1, 'blue');

    ctx.fillStyle = grd2;
    ctx.fillText("radialGradient",10,500);

    ctx.strokeStyle = grd2;
    ctx.lineWidth = 3;
    ctx.strokeText("strokeText", 10, 700);
});