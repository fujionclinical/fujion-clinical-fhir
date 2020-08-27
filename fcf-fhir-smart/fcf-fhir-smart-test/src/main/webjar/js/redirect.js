// Get the URL parameters received from the authorization server.

const params = fromQueryString(),
    state = params.state,  // session key
    code = params.code;    // authorization code

// Load the app parameters stored in the session.

const session = JSON.parse(sessionStorage[state]),
    tokenUri = session.tokenUri,
    clientId = session.clientId,
    serviceUri = session.serviceUri,
    redirectUri = session.redirectUri;

// Prep the token exchange call parameters.

const authParams = {
    client_id: clientId,
    code: code,
    grant_type: 'authorization_code',
    redirect_uri: redirectUri
};

const authOptions = {
    url: tokenUri,
    type: 'POST',
    data: authParams
};

let authData,
    patient;

// Obtain authorization token from the authorization service using the authorization code.

$.ajax(authOptions).done(function (resp) {
    authData = resp;
    doSteps([fetchPatient]);
});

// Perform a series of asynchronous steps in sequential order.

function doSteps(steps, result) {
    var step = steps.shift();

    if (step) {
        if (!result) {
            doSteps(steps, step());
        } else {
            result.done(function () {
                doSteps(steps, step());
            });
        }
    }
}

function fetchData(callback, path, params) {
    return $.ajax({
        url: serviceUri + '/' + path + toQueryString(params),
        type: 'GET',
        dataType: 'json',
        headers: {
            'Authorization': 'Bearer ' + authData.access_token
        },
    }).done(callback).fail(onFail);
}

function onFail(jqXHR, textStatus, errorThrown) {
    showResult(textStatus + ': ' + errorThrown);
}

function fetchPatient() {
    return fetchData(onFetchPatient, 'Patient/' + authData.patient);
}

function onFetchPatient(pat) {
    patient = pat;
    showResult(pat);
}

function showResult(patient) {
    const html = 'Successful SMART launch on ' + new Date() + '.<p>Current patient is: ' + patient.name[0].text;
    $('#message').html(html);
}
