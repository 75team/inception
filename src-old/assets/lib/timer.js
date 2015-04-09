(function(global){
    var $guid = 0;
    function guid(){
        return ++$guid;
    };
    var timers = [];
    // html5 says this should be at least 4
    var MIN_TIMER_TIME = 4;
    function normalizeTime(time) {
        time = time*1;
        if ( isNaN(time) || time < 0 ) {
            time = 0;
        }

        if ( time < MIN_TIMER_TIME ) {
            time = MIN_TIMER_TIME;
        }
        return time;
    }
    timers.addTimerOrInterval = function(fn, time, timerOrInterval){
        // this is Envjs.timers so this function is safe to call from threads
        // and more specifically setTimeout/setInterval should be safe to use
        // from threads
        var id = guid();
        //console.log('setting %s %s %s', timerOrInterval, time, fn);
        time = normalizeTime(time);
        timers.push({
            type: timerOrInterval,
            time: time,
            at: Date.now() + time,
            fn: (typeof fn == 'string') ? new Function(fn) : fn,
            id: id
        });
        return id;
    };

    var tick = new java.lang.Thread(new java.lang.Runnable({
        run: function(){
            var timer,
                i;
            while(true){
                var now = Date.now();
                var callbacks = [];
                for(i=0;i<timers.length;){
                    timer = timers[i];
                    //console.log('scheduled for %s , currently %s', timer.at, now);
                    if(timer.at <= now){
                        //console.log('timer past due: at(%s), now(%s), type(%s)',timer.at, now, timer.type);
                        switch(timer.type){
                        case 'timeout':
                            //we need to remove it from the timers list and add it to the callback list
                            callbacks.push.apply(callbacks, timers.splice(i,1));
                            //dont increment the counter since the timers array was spliced
                            break;
                        case 'interval':
                            //we need to add it to the callback list but leave it in the timers list
                            callbacks.push(timer);
                            timer.at = now+timer.time;
                            //fall through to increament the counter since the timers array is unchanged
                            i++;
                            break;
                        default:
                            i++;
                        }
                    }else{
                        //timer isnt read for execution so just leave it be
                        i++;
                    }
                }

                //console.log('timer tick has %s callbacks', callbacks.length);
                //finally we need to execute the callbacks in the order added to this stack
                for(i = 0; i < callbacks.length; i++){
                    timer = callbacks[i];
                    timer.fn.apply(timer.fn,[]);
                }
                java.lang.Thread.currentThread().sleep(MIN_TIMER_TIME);
            }
            ////////
        }
    }));

    tick.start();
    timers.removeTimerOrInterval = function(id, timerOrInterval){
        // this is Envjs.timers so this function is safe to call from threads
        // and more specifically clearTimeout/clearInterval should be safe to
        // use from threads
        var i;
        //console.log("clearing %s %s", timerOrInterval, id);
        for(i = 0; i < timers.length; i++){
            if(timers[i].id === id){
                timers.splice(i,1);
                break;
            }
        }
        return;
    };
    global.setTimeout = function(fn, time){
        return timers.addTimerOrInterval(fn, time, 'timeout');
    };
    global.clearTimeout = function(id){
        return timers.removeTimerOrInterval(id, 'timeout');
    };
    global.setInterval = function(fn, time){
        return timers.addTimerOrInterval(fn, time, 'interval');
    };
    global.clearInterval = function(id){
        return timers.removeTimerOrInterval(id, 'interval');
    };
})(this);