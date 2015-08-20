net.config({
    baseUrl: "/"
});
require("index", function () {
    var Log = Packages.android.util.Log;
    function log(str) {
        Log.i('info', str);
    }
    var ctx = Canvas.getContext('2d'),
        imgs = [],
        index = 0;
    for (var i = 0; i < 96; i++) {
        imgs[i] = new Image();
        imgs[i].src = "app/drawimg/run/" + i + ".png";
    }
    var w = imgs[0].width * 2;
    var h = imgs[0].height * 2;
    var DIRECTIONS = {
        'RIGHT': 6,
        'DOWN': 0,
        "LEFT": 2,
        "UP": 4
    };
    var canvasWidth = Canvas.width,
        canvasHeight = Canvas.height,
        x = 0,
        y = 0,
        xSpeed = 10,
        ySpeed = 0,
        curDir = DIRECTIONS.RIGHT,
        curFrame = 0;
	log(canvasWidth + " ######## " + canvasHeight);
    function draw() {
        ctx.clearRect(0 ,0 , canvasWidth, canvasHeight);
        if (curFrame >= 12) curFrame = 0;
        if (x + w > canvasWidth && curDir == DIRECTIONS.RIGHT) {
            curDir = DIRECTIONS.DOWN;
            xSpeed = 0;
            ySpeed = 10;
        }
        if (y + h > canvasHeight && curDir == DIRECTIONS.DOWN) {
            curDir = DIRECTIONS.LEFT;
            xSpeed = -10;
            ySpeed = 0;
        }
        if (x - w < 0 && curDir == DIRECTIONS.LEFT) {
            curDir = DIRECTIONS.UP;
            xSpeed = 0;
            ySpeed = -10;
        }
        if (y - h < 0 && curDir == DIRECTIONS.UP) {
            curDir = DIRECTIONS.RIGHT;
            xSpeed = 10;
            ySpeed = 0;
        }
        x += xSpeed;
        y += ySpeed;
        ctx.drawImage(imgs[curDir * 12 + curFrame], x, y, w, h);
        curFrame++;

        requestAnimationFrame(draw);
    }

    requestAnimationFrame(draw);
//    setInterval(draw, 100);
});