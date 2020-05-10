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
package org.fujionclinical.fhir.api.common.patientlist;

/**
 * A list item that is associates a patient object with some additional arbitrary displayable
 * information.
 */
public class PatientListItem implements IPatientListItem {

    private final IPatientAdapter patient;

    private final String info;

    /**
     * Creates a patient list item with no additional information.
     *
     * @param patient A patient object.
     */
    public PatientListItem(IPatientAdapter patient) {
        this(patient, null);
    }

    /**
     * Creates a patient list item with the specified displayable information.
     *
     * @param patient A patient object.
     * @param info    Displayable information to be associated with the patient.
     */
    public PatientListItem(
            IPatientAdapter patient,
            String info) {
        this.patient = patient;
        this.info = info;
    }

    /**
     * Returns the patient associated with this item.
     *
     * @return The associated patient.
     */
    public IPatientAdapter getPatient() {
        return patient;
    }

    /**
     * Returns the displayable information associated with this item. May be null.
     *
     * @return Displayable information.
     */
    public String getInfo() {
        return info;
    }

    /**
     * Two list items are considered equal if their associated patients are equal.
     */
    @Override
    public boolean equals(Object object) {
        return equalTo(object);
    }

}
