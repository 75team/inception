var net = (function() {
	var cache = {};
	var options = {
		baseUrl:"res/js"
	};
	function config(opt){
		if(typeof opt == "object"){
			for(var i in opt){
				options[i] = opt[i];
			}
		}
	}

	function require(modName, deps, callback) {
		var params = [];
		var depCount = 0;
		var i, len, isEmpty = false;
		if(typeof modName != "string"){
			print("js module failed: need module name");
			return false;			
		}
		if(typeof deps == "function"){
			callback = deps;
			deps = [];
		}		
//		if(typeof modName == "function"){
//			callback = modName;
//			deps = [];
//		}else if(typeof Object.prototype.toString.call(modName) == "[object Array]"){
//			callback = deps;
//			deps = modName;
//		}else if(typeof modName == "string"){
//			if(typeof deps == "function"){
//				callback = deps;
//				deps = [];
//			}
//		}else{
//			print("js module failed:" + modName);
//			return false;
//		}

		if (deps.length) {
			depCount = deps.length;
			for (i = 0, len = deps.length; i < len; i++) {
				eachLoad(i);
			}
		} else {
			isEmpty = true;
		}
		
		if (isEmpty) {
			saveModule(modName, deps, callback);
//			setTimeout(function() {
//				saveModule(modName, null, callback);
//			}, 0);
		}
		
		function eachLoad(i) {
//			depCount++;
			loadModule(deps[i], loadCb);
			function loadCb(param) {
				params[i] = param;						
				depCount--;				
				if (depCount == 0) {
					saveModule(modName, params, callback);
				}
			}
			
		}
	};

	
	function addDotJs(modName) {
		var url = modName;

		if (url.indexOf('.js') == -1){
			url = url + '.js';
		}
		return url;
	};

	function loadModule(modName, callback) {
		var url = addDotJs(modName), mod;
		mod = cache[modName];
		if(!mod){
			load(options.baseUrl+url);
			mod = cache[modName];
		}

		callback(mod.exports);
		
//		if (cache[modName]) {
//			mod = cache[modName];
//			callback(mod.exports);
////			if (mod.status == 'loaded') {
////				callback(mod.exports);
//////				setTimeout(callback(this.params), 0);
////			} else {
////				mod.onload.push(callback);
////			}
//		} else {
//			
////			mod = cache[modName] = {
////				modName : modName,
////				status : 'loading',
////				exports : null,
////				onload : [ callback ]
////			};
////			print(options.baseUrl+url);
//			load(options.baseUrl+url);
//			
//			callback()
//			
////			print(cache["page"]);
//		}
	};

	function saveModule(modName, params, callback) {
		var mod, fn;
		
		if (cache.hasOwnProperty(modName)) {
			mod = cache[modName];
			mod.status = 'loaded';
			mod.exports = callback ? callback(params) : mod.exports;
			while (fn = mod.onload.shift()) {
				fn(mod.exports);
			}
		} else {
			mod = cache[modName] = {
				modName : modName,
				status : 'loaded',
				exports : null
			};
			if(!callback){
				mod.exports = params;
			}else{
				mod.exports = callback.apply(callback, params);
			}
		}
	};
	return {
		require:require,
		define:require,
		config:config
	}
})();
var require = net.require;
var define = net.require;