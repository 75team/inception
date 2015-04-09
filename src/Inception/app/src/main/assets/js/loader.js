net.config({
    baseUrl: "canvas/"
});
require("loader", ["index"], function(page) {
    print('modules loaded');
});

//print(typeof DaContext);
////print(typeof DaJSContext);
//print(typeof Holder);
//DaContext.sf(Holder);

//print(typeof DaJSContext);