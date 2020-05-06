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
package org.fujionclinical.fhir.r5.api.practitioner;

import org.apache.commons.lang.StringUtils;
import org.fujionclinical.fhir.api.common.query.SearchCriteria;
import org.fujionclinical.fhir.r5.api.common.FhirUtil;
import org.fujionclinical.fhir.r5.api.terminology.Constants;
import org.hl7.fhir.r5.model.HumanName;
import org.hl7.fhir.r5.model.Identifier;

/**
 * Represents search criteria supported by FHIR.
 */
public class PractitionerSearchCriteria extends SearchCriteria {

    //private static final String ERROR_MISSING_REQUIRED = "@practitionersearch.error.missing.required";

    private HumanName name;

    private Identifier dea;

    private Identifier ssn;

    private String gender;

    public PractitionerSearchCriteria() {
        super("Insufficient search parameters.");
    }

    /**
     * Creates a criteria instance with settings parsed from search text.
     *
     * @param searchText Search text to parse. Uses pattern matching to determine which criterion is
     *                   associated with a given input component. Separate multiple input components with a
     *                   semicolons.
     */
    public PractitionerSearchCriteria(String searchText) {
        this();
        searchText = searchText == null ? null : searchText.trim();

        if (!StringUtils.isEmpty(searchText)) {
            String[] pcs = searchText.split(";");

            for (String pc : pcs) {
                pc = pc.trim();

                if (pc.isEmpty()) {
                    continue;
                } else if (isValid() && (pc.equalsIgnoreCase("M") || pc.equalsIgnoreCase("F"))) {
                    setGender(pc.toUpperCase());
                } else if (!pc.matches(".*\\d.*")) {
                    setName(pc);
                } else if (pc.matches("^=.+$")) {
                    setId(pc.substring(1));
                } else if (pc.matches("^\\d{3}-\\d{2}-\\d{4}$")) {
                    setSSN(pc);
                } else {
                    setDEA(pc);
                }
            }
        }
    }

    /**
     * Returns true if the current criteria settings meet the minimum requirements for a search.
     *
     * @return True if minimum search requirements have been met.
     */
    @Override
    protected boolean isValid() {
        return super.isValid() || ssn != null || dea != null || name != null;
    }

    /**
     * Returns the patient name criterion.
     *
     * @return Patient name criterion.
     */
    public HumanName getName() {
        return name;
    }

    /**
     * Sets the patient name criterion.
     *
     * @param name Patient name.
     */
    public void setName(String name) {
        this.name = FhirUtil.parseName(name);
    }

    /**
     * Sets the patient name criterion.
     *
     * @param name Patient name.
     */
    public void setName(HumanName name) {
        this.name = name;
    }

    /**
     * Returns the DEA criterion.
     *
     * @return DEA criterion.
     */
    public Identifier getDEA() {
        return dea;
    }

    /**
     * Sets the DEA criterion.
     *
     * @param dea DEA.
     */
    public void setDEA(String dea) {
        this.dea = new Identifier();
        this.dea.setType(Constants.IDENT_DEA);
        this.dea.setValue(dea);
    }

    /**
     * Returns the SSN criterion.
     *
     * @return SSN criterion.
     */
    public Identifier getSSN() {
        return ssn;
    }

    /**
     * Sets the SSN criterion.
     *
     * @param ssn SSN.
     */
    public void setSSN(String ssn) {
        this.ssn = new Identifier();
        this.ssn.setType(Constants.IDENT_SSN);
        this.ssn.setValue(ssn);
    }

    /**
     * Returns the gender criterion.
     *
     * @return Gender criterion.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender criterion.
     *
     * @param gender Gender.
     */
    public void setGender(String gender) {
        this.gender = StringUtils.trimToNull(gender);
    }

    /**
     * Returns true if no criteria have been set.
     *
     * @return True if no criteria have been set.
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && name == null && dea == null && ssn == null && gender == null;
    }
}
