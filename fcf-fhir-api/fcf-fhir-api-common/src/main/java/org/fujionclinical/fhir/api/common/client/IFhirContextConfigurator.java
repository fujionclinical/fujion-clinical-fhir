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
package org.fujionclinical.fhir.api.common.client;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;

public interface IFhirContextConfigurator {

    /**
     * Returns the qualifier for the configurator, if any.
     *
     * @return The qualifier for the configurator, if any.
     */
    String getQualifier();

    /**
     * @return The connection request timeout, in milliseconds.
     * @see ca.uhn.fhir.rest.client.api.IRestfulClientFactory#getConnectionRequestTimeout()
     */
    int getConnectionRequestTimeout();

    /**
     * @return The connect timeout, in milliseconds.
     * @see ca.uhn.fhir.rest.client.api.IRestfulClientFactory#getConnectTimeout()
     */
    int getConnectTimeout();
    
    /**
     * @return The socket timeout, in milliseconds.
     * @see ca.uhn.fhir.rest.client.api.IRestfulClientFactory#getSocketTimeout()
     */
    int getSocketTimeout();
    
    /**
     * @return The maximum number of connections per route allowed in the pool.
     * @see ca.uhn.fhir.rest.client.api.IRestfulClientFactory#getPoolMaxPerRoute()
     */
    int getPoolMaxPerRoute();

    /**
     * @return The maximum number of connections allowed in the pool.
     * @see ca.uhn.fhir.rest.client.api.IRestfulClientFactory#getPoolMaxTotal()
     */
    int getPoolMaxTotal();
    
    /**
     * @return The server validation mode.
     * @see ca.uhn.fhir.rest.client.api.IRestfulClientFactory#getServerValidationMode()
     */
    ServerValidationModeEnum getServerValidationMode();
    
    /**
     * Returns the FHIR version supported by this FHIR context.
     *
     * @return The FHIR version supported by this FHIR context.
     */
    FhirVersionEnum getVersion();

    /**
     * @return The HTTP proxy address and port delimited with a colon (or null or empty for no
     *         proxy).
     * @see ca.uhn.fhir.rest.client.apache.ApacheRestfulClientFactory#setProxy(String, Integer)
     */
    String getProxy();

}
