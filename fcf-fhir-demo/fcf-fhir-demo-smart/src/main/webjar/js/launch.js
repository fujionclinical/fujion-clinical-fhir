// The SMART app's client id

const clientId = 'bd755a49-d9fd-43d3-9125-637c2a17bdf4';

// The scopes that the app will request from the authorization server.

const scope = 'launch patient/*.read openid user/*.read profile';

// Extract parameters passed to launch target.

const params = fromQueryString(),
    serviceUri = params.iss,
    launchContextId = params.launch;

// Generate a unique session key string.

const state = newSessionKey();

// Form the redirectUri from the baseUri.

let baseUri = window.location.href;

baseUri = baseUri.substring(0, baseUri.indexOf('/launch'));

const redirectUri = baseUri + '/redirect/index.html';

// FHIR Service Conformance Statement URL.

const conformanceUri = serviceUri + '/metadata';

// Fetch the application type from the configuration.

$.get(conformanceUri, process_conformance, 'json');

// Callback to process the conformance statement and redirect accordingly.

function process_conformance(conf) {
    const oauth2_uri = {};

    const smartExtension = conf.rest[0].security.extension.filter(function (e) {
        return (e.url === 'http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris');
    });

    // Extract the URIs from the SMART extension.

    smartExtension[0].extension.forEach(function(arg) {
        arg.url ? oauth2_uri[arg.url] = resolvePath(arg.valueUri, serviceUri) : null;
    });

    // Place parameters in the session for use in redirect target.

    const session_params = {
        clientId: clientId,
        serviceUri: serviceUri,
        redirectUri: redirectUri,
        tokenUri: oauth2_uri.token,
        scope: scope
    };

    $.extend(session_params, params);
    sessionStorage[state] = JSON.stringify(session_params);

    // Finally, redirect the browser to the authorization server and pass the needed
    // parameters for the authorization request in the URL.

    const redirect_params = {
        response_type: 'code',
        client_id: clientId,
        scope: scope,
        redirect_uri: redirectUri,
        aud: serviceUri,
        launch: launchContextId,
        state: state
    };

    window.location.href = oauth2_uri.authorize + toQueryString(redirect_params);
}

