package org.fujionclinical.fhir.api.common.core;

import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class Constants {

    public static final String SYS_IDENTIFIER_TYPE = "http://hl7.org/fhir/v2/0203";

    public static final String SSN_SYSTEM = "http://hl7.org/fhir/sid/us-ssn";

    public static final String SP_IDENTIFIER = "identifier";

    public static final String SP_PATIENT = "patient";

    public static final TokenClientParam PARAM_IDENTIFIER = new TokenClientParam(SP_IDENTIFIER);

    public static final ReferenceClientParam PARAM_PATIENT = new ReferenceClientParam(SP_PATIENT);

}
