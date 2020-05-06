        // Get the URL parameters received from the authorization server.
        
        var params = fromQueryString(),
        	state = params.state,  // session key
        	code = params.code;    // authorization code
        
        // Load the app parameters stored in the session.
        
        var session = JSON.parse(sessionStorage[state]),
        	tokenUri = session.tokenUri,
        	clientId = session.clientId,
        	secret = session.secret,
        	serviceUri = session.serviceUri,
        	redirectUri = session.redirectUri;
        
        // Prep the token exchange call parameters.
        
        var data = {
            code: code,
            grant_type: 'authorization_code',
            redirect_uri: redirectUri
        };
        
        var options = {
            url: tokenUri,
            type: 'POST',
            data: data
        };
            
        if (secret) {
            options['headers'] = {'Authorization': 'Basic ' + btoa(clientId + ':' + secret)};
        } else {
            data['client_id'] = clientId;
        }
        
        var authData;
        
        var patient;
        
        // Obtain authorization token from the authorization service using the authorization code.

        $.ajax(options).done(function(resp){
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
	        		result.done(function() {
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
        
		function extractResourceByCode(bundle, acode) {
			var entries = bundle.entry || [],
				acode = acode.split('|');
		
			for (var i = 0; i < entries.length; i++) {
				var resource = entries[i].resource,
					code = resource.code,
					codings = code ? code.coding : null;
				
				codings = codings || [];
				
				for (var j = 0; j < codings.length; j++) {
					var c = codings[j];
					
					if (c.system === acode[0] && c.code === acode[1]) {
						return resource;
					}
				}
			}
		}
		
		function extractComponentByCode(resource, acode) {
			var components = resource.component || [],
				acode = acode.split('|');
			
			for (var i = 0; i < components.length; i++) {
				var component = components[i],
					code = component.code,
					codings = code ? code.coding : null;
				
				codings = codings || [];
				
				for (var j = 0; j < codings.length; j++) {
					var c = codings[j];
					
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
			showResult(pat);
		}
		
		function showResult(result) {
			$('<div style="white-space: pre"/>').appendTo('body').text('Result: ' + (typeof result === 'string' ? result : JSON.stringify(result)));
		}
