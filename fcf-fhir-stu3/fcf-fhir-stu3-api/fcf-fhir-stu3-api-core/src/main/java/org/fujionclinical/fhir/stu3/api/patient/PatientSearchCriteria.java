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
package org.fujionclinical.fhir.stu3.api.patient;

import org.apache.commons.lang.StringUtils;
import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.common.api.query.SearchCriteria;
import org.fujionclinical.fhir.stu3.api.common.FhirUtil;
import org.fujionclinical.fhir.stu3.api.terminology.Constants;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;

import java.util.Date;

/**
 * Represents search criteria supported by FHIR.
 */
public class PatientSearchCriteria extends SearchCriteria {
    
    
    private static final String ERROR_MISSING_REQUIRED = "@patientsearch.error.missing.required";
    
    private HumanName name;
    
    private Identifier mrn;
    
    private Identifier ssn;
    
    private String gender;
    
    private Date birth;
    
    public PatientSearchCriteria() {
        super(ERROR_MISSING_REQUIRED);
    }
    
    /**
     * Creates a criteria instance with settings parsed from search text.
     *
     * @param searchText Search text to parse. Uses pattern matching to determine which criterion is
     *            associated with a given input component. Separate multiple input components with
     *            semicolons.
     */
    public PatientSearchCriteria(String searchText) {
        this();
        searchText = searchText == null ? null : searchText.trim();
        
        if (!StringUtils.isEmpty(searchText)) {
            String[] pcs = searchText.split(";");
            
            for (String pc : pcs) {
                pc = pc.trim();
                Date tempDate;
                
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
                } else if ((tempDate = parseDate(pc)) != null) {
                    setBirth(tempDate);
                } else {
                    setMRN(pc);
                }
            }
        }
    }
    
    /**
     * Returns a date value if the input is a valid date. Otherwise, returns null. Explicitly
     * excludes some patterns that may successfully parse as a date.
     *
     * @param value Input to parse.
     * @return Result of parsed input, or null if parsing unsuccessful.
     */
    private Date parseDate(String value) {
        if (StringUtils.isNumeric(value)) {
            return null;
        }
        
        if (value.matches("^\\d+-\\d+$")) {
            return null;
        }
        
        return DateUtil.parseDate(value);
    }
    
    /**
     * Returns true if the current criteria settings meet the minimum requirements for a search.
     *
     * @return True if minimum search requirements have been met.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || ssn != null || mrn != null || name != null;
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
     * Returns the MRN criterion.
     *
     * @return MRN criterion.
     */
    public Identifier getMRN() {
        return mrn;
    }
    
    /**
     * Sets the MRN criterion.
     *
     * @param mrn MRN.
     */
    public void setMRN(String mrn) {
        this.mrn = new Identifier();
        this.mrn.setType(Constants.IDENT_MRN);
        this.mrn.setValue(mrn);
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
     * Returns the date of birth criterion.
     *
     * @return DOB criterion.
     */
    public Date getBirth() {
        return birth;
    }
    
    /**
     * Sets the date of birth criterion.
     *
     * @param birth Date of birth.
     */
    public void setBirth(Date birth) {
        this.birth = birth;
    }
    
    /**
     * Returns true if no criteria have been set.
     *
     * @return True if no criteria have been set.
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && name == null && mrn == null && ssn == null && gender == null && birth == null;
    }
}
