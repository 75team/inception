(function(global) {
    var uid = 0;

    function Image() {
        if (!(this instanceof Image)) {
            return new Image();
        }
        this.__uid = ++uid;
    }

    var fn = {};

    fn.__defineSetter__("src", function(val) {

    });

    this.Image = Image;
})(this);