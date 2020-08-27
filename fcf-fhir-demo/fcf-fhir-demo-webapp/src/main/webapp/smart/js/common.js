// Create query string from parameter map.

function toQueryString(params, qs) {
	qs = qs || '';
	
	let dlm = qs ? '&' : '?';
	
	if (params) {
		for (var param in params) {
			qs += dlm + param + '=' + encodeURIComponent(params[param]);
			dlm = '&';
		}
	}
	
	return qs
}

// Parse query string into parameter map.

function fromQueryString() {
    const qs = window.location.search.substring(1),
    	params = qs.split('&'),
    	map = {};
    
    for (let i = 0; i < params.length; i++) {
        const keyval = params[i].split('='),
        	key = keyval[0],
        	val = keyval[1];
        
        map[key] = decodeURIComponent(val);
    }
    
    return map;
}

// Resolve relative path.

const re = /https?:\/\/(\w+\.?)+:(\d+)?/;

function resolvePath(path, base) {
	if (path.indexOf('/') === 0 && re.test(base)) {
		path = base.match(re)[0] + path;
	}
	
	return path;
}

// Generates a unique session key.

function newSessionKey() {
	return Date.now() + '-' + toAbsInt(Math.random() * 10000000);
}

// Computes number of years (rounded to nearest integer) between two dates.  If not specified, second date defaults to now.

function getYearsDiff(date1, date2) {
	const millis1 = toDate(date1);
	const millis2 = date2 ? toDate(date2) : Date.now();
	return isNaN(millis1) || isNaN(millis2) ? null : toAbsInt((millis2 - millis1) / 31536000000);
}

// Converts a number to its absolute value rounded to the nearest integer.

function toAbsInt(value) {
	return Math.floor(Math.abs(value));
}

// Converts a string to millis since epoch.  If already a number, just return that.

function toDate(value) {
	return typeof value === 'number' ? value : Date.parse(value);
}
