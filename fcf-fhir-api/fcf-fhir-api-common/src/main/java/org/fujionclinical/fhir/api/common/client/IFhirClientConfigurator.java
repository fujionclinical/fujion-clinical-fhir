/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2019 fujionclinical.org
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

import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.SummaryEnum;
import org.fujionclinical.fhir.security.common.IAuthInterceptor;

public interface IFhirClientConfigurator {

    /**
     * Returns the qualifier for the configurator, if any.
     *
     * @return The qualifier for the configurator, if any.
     */
    String getQualifier();

    /**
     * Returns the base URL of the FHIR server.
     *
     * @return The base URL of the FHIR server.
     */
    String getServerBase();

    /**
     * Returns the interceptor to use for authentication.
     *
     * @return The interceptor to use for authentication (may be null for no authentication).
     */
    IAuthInterceptor getAuthInterceptor();

    /**
     * Returns true if the client should request a conformance statement upon initial connection.
     *
     * @return True if the client should request a conformance statement upon initial connection.
     */
    boolean isValidateConformance();

    /**
     * Returns the encoding type(s) to be used in communication with the FHIR server.
     *
     * @return The encoding type(s) to be used in communication with the FHIR server.
     */
    EncodingEnum getEncoding();

    /**
     * Returns true to enable pretty print formatting of resources.
     *
     * @return True to enable pretty print formatting of resources.
     */
    boolean isPrettyPrint();

    /**
     * Returns the value for the <code>_summary</code> parameter to be applied globally on this
     * client.
     *
     * @return The value for the <code>_summary</code> parameter to be applied globally on this
     *         client. Return null to disable.
     */
    SummaryEnum getSummary();

}
