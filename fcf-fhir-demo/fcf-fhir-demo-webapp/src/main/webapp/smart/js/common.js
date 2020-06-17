// Create query string from parameter map.

function toQueryString(params, qs) {
    qs = qs || '';

    var dlm = qs ? '&' : '?';

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
    var qs = window.location.search.substring(1),
        params = qs.split('&'),
        map = {};

    for (var i = 0; i < params.length; i++) {
        var keyval = params[i].split('='),
            key = keyval[0],
            val = keyval[1];

        map[key] = decodeURIComponent(val);
    }

    return map;
}

// Resolve relative path.

var re = /https?:\/\/(\w+\.?)+:(\d+)?/;

function resolvePath(path, base) {
    if (path.indexOf('/') === 0 && re.test(base)) {
        path = base.match(re)[0] + path;
    }

    return path;
}
