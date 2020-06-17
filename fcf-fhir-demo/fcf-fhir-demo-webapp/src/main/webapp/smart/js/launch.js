// The SMART app's id.

var clientId = 'bd755a49-d9fd-43d3-9125-637c2a17bdf4';

// The scopes that the app will request from the authorization server.

var scope = 'launch patient/*.read openid user/*.read profile';

// For demonstration purposes, if you registered a confidential client
// you can enter its secret here. The demo app will pretend it's a confidential
// app (in reality it cannot be confidential, since it cannot keep secrets in the
// browser).

var secret = null;    // Set me only if confidential.

// Extract parameters passed to launch target.

var params = fromQueryString(),
    serviceUri = params.iss,
    launchContextId = params.launch;

// Generate a unique session key string.

var state = uuidv4();

// Form the redirectUri from the baseUri.

var baseUri = window.location.href;

baseUri = baseUri.substring(0, baseUri.indexOf('/launch'));

var redirectUri = baseUri + '/redirect/index.html';

// FHIR Service Conformance Statement URL.

var conformanceUri = serviceUri + '/metadata'

// Let's request the conformance statement from the SMART on FHIR API server and
// find out the endpoint URLs for the authorization server.

$.get(conformanceUri, process_conformance, 'json');

// Callback to process the conformance statement and redirect accordingly.

function process_conformance(conf) {
    var oauth2_uri = {};

    var smartExtension = conf.rest[0].security.extension.filter(function (e) {
        return (e.url === 'http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris');
    });

    // Extract the URIs from the SMART extension.

    smartExtension[0].extension.forEach(function (arg) {
        arg.url ? oauth2_uri[arg.url] = resolvePath(arg.valueUri, serviceUri) : null;
    });

    // Place parameters in the session for use in redirect target.

    var session_params = {
        clientId: clientId,
        serviceUri: serviceUri,
        redirectUri: redirectUri,
        tokenUri: oauth2_uri.token
    };

    secret ? session_params.secret = secret : null;

    sessionStorage[state] = JSON.stringify(session_params);

    // Finally, redirect the browser to the authorization server and pass the needed
    // parameters for the authorization request in the URL.

    var redirect_params = {
        response_type: 'code',
        client_id: clientId,
        scope: scope,
        redirect_uri: redirectUri,
        aud: serviceUri,
        launch: launchContextId,
        state: state
    };

    //showResult({oauth: oauth2_uri, params: redirect_params});
    window.location.href = oauth2_uri.authorize + toQueryString(redirect_params);
}

function showResult(result) {
    $('<div style="white-space: pre"/>').appendTo('body').text('Result: ' + (typeof result === 'string' ? result : JSON.stringify(result)));
}
