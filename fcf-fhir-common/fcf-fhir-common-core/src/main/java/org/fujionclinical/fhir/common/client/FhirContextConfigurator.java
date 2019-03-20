/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.common.client;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;

/**
 * Configurator for all FHIR context-related settings.
 */
public class FhirContextConfigurator extends FhirBaseConfigurator implements IFhirContextConfigurator {

    @Param(property = "version", required = true)
    private FhirVersionEnum version;
    
    @Param(property = "proxy")
    private String proxy;
    
    @Param(property = "connection.request.timeout", defaultValue = "10000")
    private int connectionRequestTimeout;

    @Param(property = "connect.timeout", defaultValue = "10000")
    private int connectTimeout;

    @Param(property = "pool.max.per.route", defaultValue = "20")
    private int poolMaxPerRoute;

    @Param(property = "pool.max.total", defaultValue = "20")
    private int poolMaxTotal;
    
    @Param(property = "socket.timeout", defaultValue = "10000")
    private int socketTimeout;

    @Param(property = "server.validation.mode", defaultValue = "ONCE")
    private ServerValidationModeEnum serverValidationMode;

    public FhirContextConfigurator() {
        this(null);
    }

    public FhirContextConfigurator(String qualifier) {
        super("fhir.context", qualifier);
    }

   @Override
    public FhirVersionEnum getVersion() {
        return version;
    }
    
    @Override
    public String getProxy() {
        return proxy;
    }
    
    @Override
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }
    
    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public ServerValidationModeEnum getServerValidationMode() {
        return serverValidationMode;
    }
    
    @Override
    public int getSocketTimeout() {
        return socketTimeout;
    }

    @Override
    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }
    
    @Override
    public int getPoolMaxPerRoute() {
        return poolMaxPerRoute;
    }
    
}
