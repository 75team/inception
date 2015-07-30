(function(global) {
    var uid = 0;
    var queue = [];

    function getUid() {
        return ++uid;
    }

    function getDateTime() {
        return new Date().getTime();
    }

    function requestAnimationFrame(fun) {
        if (typeof fun === 'function') {
            var id = getUid();
            queue.push({
                'id': id,
                'time': getDateTime(),
                'fn': fun
            });

            return id;
        } else {
            throw new Error('the param must be function!');
        }
    }

    function frameAction() {
        var cur = queue;
        queue = [];
        var tmp = cur.shift();

        while (tmp) {
            tmp.fn(this);
            tmp = cur.shift();
        }
    }

    function cancelAnimationFrame(id) {
        var i = 0,
            tmp;

        while (queue[i++]) {
            if (tmp.id == id) {
                queue.splice(i,1);
                return true;
            }
        }

        return false;
    }

    global.frameQueue = queue;
    global.requestAnimationFrame = requestAnimationFrame;
    global.cancelAnimationFrame = cancelAnimationFrame;
    global.frameAction = frameAction;

})(this);