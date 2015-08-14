(function(global) {
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

    function RadialGradient(x0, y0, r0, x1, y1, r1){

    }
})(this);