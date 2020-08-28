const system_loinc = 'http://loinc.org|',
    code_smoking_status = system_loinc + '72166-2',			  // Smoking status
    code_ppd = system_loinc + '8663-7',				          // Packs/day
    code_years_smoking = system_loinc + '88029-4',            // Years smoking
    code_asbestos = 'urn:oid:2.16.840.1.113883.6.90|Z77.090'; // Asbestos exposure

const btnRefresh$ = $('#btnRefresh'),
    status$ = $('#status'),
    iframe$ = $('#iframe');

btnRefresh$.click(refresh);

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
    profile,
    patient;

// Obtain authorization token from the authorization service using the authorization code.

$.ajax(authOptions).done(function (resp) {
    authData = resp;
    refresh();
});

// Refresh the view.

function refresh() {
    iframe$.removeAttr('src');

    profile = {
        age: null,
        asbestos: null,
        cigs_per_day: null,
        healthy: true,
        quit_smoking: null,
        sex: null,
        years_quit: null,
        years_smoked: null
    }

    patient = null;

    doSteps([fetchPatient, fetchAsbestosExposure, fetchSmokingHistory, loadDecisionPrecision]);
}

// Perform a series of asynchronous steps in sequential order.

function doSteps(steps, result) {
    const step = steps.shift();

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
    return ajax(callback, path, params, 'GET');
}

function ajax(callback, path, params, type) {
    return $.ajax({
        url: serviceUri + '/' + path,
        type: type,
        data: params,
        dataType: 'json',
        headers: {
            'Authorization': 'Bearer ' + authData.access_token
        },
    }).done(callback).fail(onFail);
}

function onFail(jqXHR, textStatus, errorThrown) {
    showMessage(textStatus + ': ' + errorThrown);
}

function extractResourceByCode(bundle, acode) {
    const entries = bundle.entry || [];
    acode = acode.split('|');

    for (let i = 0; i < entries.length; i++) {
        let resource = entries[i].resource,
            code = resource.code,
            codings = code ? code.coding : null;

        codings = codings || [];

        for (let j = 0; j < codings.length; j++) {
            const c = codings[j];

            if (c.system === acode[0] && c.code === acode[1]) {
                return resource;
            }
        }
    }
}

function extractComponentByCode(resource, acode) {
    const components = resource.component || [];
    acode = acode.split('|');

    for (let i = 0; i < components.length; i++) {
        let component = components[i],
            code = component.code,
            codings = code ? code.coding : null;

        codings = codings || [];

        for (let j = 0; j < codings.length; j++) {
            const c = codings[j];

            if (c.system === acode[0] && c.code === acode[1]) {
                return component;
            }
        }
    }
}

function getValue(obs, type) {
    type = type || 'valueQuantity';
    return obs && obs[type] ? obs[type].value : null;
}

function fetchPatient() {
    return fetchData(onFetchPatient, 'Patient/' + authData.patient);
}

function onFetchPatient(pat) {
    patient = pat;

    if (patient.gender) {
        profile.sex = patient.gender.charAt(0).toUpperCase() + patient.gender.slice(1);
    }

    if (patient.birthDate) {
        profile.age = getYearsDiff(patient.birthDate);
    }
}

function fetchAsbestosExposure() {
    return fetchData(onFetchAsbestosExposure, 'Condition', {
        patient: authData.patient
    });
}

function onFetchAsbestosExposure(bundle) {
    const condition = extractResourceByCode(bundle, code_asbestos);
    profile.asbestos = condition ? true : null;
}

function fetchSmokingHistory() {
    return fetchData(onFetchSmokingHistory, 'Observation', {
        patient: authData.patient,
        code: code_smoking_status
    });
}

function onFetchSmokingHistory(bundle) {
    const obs_status = extractResourceByCode(bundle, code_smoking_status);

    if (!obs_status) {
        return;
    }

    const period = obs_status.effectivePeriod || {},
        cmp_ppd = extractComponentByCode(obs_status, code_ppd),
        ppd = getValue(cmp_ppd),
        cmp_years_smoking = extractComponentByCode(obs_status, code_years_smoking),
        years_smoked = getValue(cmp_years_smoking);

    let end_date;

    if (ppd !== null) {
        profile.cigs_per_day = ppd * 20;
    }

    if (period.end) {
        profile.quit_smoking = true;
        end_date = period.end;
        profile.years_quit = getYearsDiff(end_date);
    } else {
        profile.quit_smoking = false;
        end_date = Date.now();
    }

    if (years_smoked !== null) {
        profile.years_smoked = years_smoked;
    } else if (period.start) {
        profile.years_smoked = getYearsDiff(period.start, end_date);
    }
}

function loadDecisionPrecision() {
    let payload = JSON.stringify({profile: profile});
    payload = encodeURIComponent(btoa(payload));
    const url = 'https://dev.lungdecisionprecision.com/api/' + payload;
    iframe$.attr('src', url);
}

function showMessage(message) {
    status$.text(!message ? '' : typeof message === 'string' ? message : JSON.stringify(message));
}

