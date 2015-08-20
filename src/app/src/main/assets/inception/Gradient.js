(function(global) {
    /* 线性渐变 */
    function LinearGradient(x0, y0, x1, y1) {
        this.startX = x0;
        this.startY = y0;
        this.endX = x1;
        this.endY = y1;
        this.colors = [];
        this.stops = [];
    }

    var fn = LinearGradient.prototype;

    fn.addColorStop = function(stop, color){
        this.colors.push(color);
        this.stops.push(stop);
    }

    global.LinearGradient = LinearGradient;

    /* 放射性渐变 */
    function RadialGradient(x, y, r){
        this.centerX = x;
        this.centerY = y;
        this.radius = r;
        this.colors = [];
        this.stops = [];
    }

    var rfn = RadialGradient.prototype;

    rfn.addColorStop = function(stop, color){
        this.colors.push(color);
        this.stops.push(stop);
    }

    global.RadialGradient = RadialGradient;
})(this);