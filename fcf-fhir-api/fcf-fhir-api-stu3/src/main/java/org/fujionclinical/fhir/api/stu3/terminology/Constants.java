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
package org.fujionclinical.fhir.api.stu3.terminology;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

public class Constants {

    public static final String SYS_RXNORM = "http://www.nlm.nih.gov/research/umls/rxnorm";

    public static final String SYS_UCUM = "http://unitsofmeasure.org";

    public static final String SYS_SNOMED = "http://snomed.info/sct";

    public static final String SYS_IDENTIFIER_TYPE = "http://hl7.org/fhir/v2/0203";

    public static final Coding CODING_MRN = new Coding(SYS_IDENTIFIER_TYPE, "MR", "MRN");

    public static final Coding CODING_SSN = new Coding(SYS_IDENTIFIER_TYPE, "SS", "SSN");

    public static final Coding CODING_DEA = new Coding(SYS_IDENTIFIER_TYPE, "DEA", "DEA");

    public static final CodeableConcept IDENT_MRN = new CodeableConcept().addCoding(CODING_MRN);

    public static final CodeableConcept IDENT_SSN = new CodeableConcept().addCoding(CODING_SSN);

    public static final CodeableConcept IDENT_DEA = new CodeableConcept().addCoding(CODING_DEA);

    public static final String VS_SNOMED = SYS_SNOMED + "?fhir_vs=refset/";

    public static final String VS_SNOMED_CLINICAL = VS_SNOMED + "32570581000036105";

    private Constants() {
    }
}
