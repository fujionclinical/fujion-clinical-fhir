/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
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
package org.fujionclinical.fhir.api.common.core;

import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class Constants {

    public static final String SYS_IDENTIFIER_TYPE = "http://hl7.org/fhir/v2/0203";

    public static final String SSN_SYSTEM = "http://hl7.org/fhir/sid/us-ssn";

    public static final String MARITAL_STATUS_SYSTEM = "http://terminology.hl7.org/CodeSystem/v3-MaritalStatus";

    public static final String NULL_FLAVOR_SYSTEM = "http://terminology.hl7.org/CodeSystem/v3-NullFlavor";

    public static final String CONDITION_CLINICAL_STATUS_SYSTEM = "http://terminology.hl7.org/CodeSystem/condition-clinical";

    public static final String CONDITION_VERIFICATION_STATUS_SYSTEM = "http://terminology.hl7.org/CodeSystem/condition-ver-status";

    public static final String DOCUMENT_REFERENCE_STATUS_SYSTEM = "http://hl7.org/fhir/document-reference-status";

    public static final String DOCUMENT_COMPOSITION_STATUS_SYSTEM = "http://hl7.org/fhir/composition-status";

    public static final String SP_IDENTIFIER = "identifier";

    public static final String SP_PATIENT = "patient";

    public static final TokenClientParam PARAM_IDENTIFIER = new TokenClientParam(SP_IDENTIFIER);

    public static final ReferenceClientParam PARAM_PATIENT = new ReferenceClientParam(SP_PATIENT);

}
