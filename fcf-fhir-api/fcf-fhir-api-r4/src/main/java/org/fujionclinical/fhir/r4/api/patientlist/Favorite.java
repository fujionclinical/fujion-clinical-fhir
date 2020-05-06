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
package org.fujionclinical.fhir.r4.api.patientlist;

import org.apache.commons.lang.ObjectUtils;

/**
 * Provides serialization support for a list.
 */
public class Favorite {

    private String name;

    private final IPatientList patientList;

    public Favorite(String data) {
        String[] pcs = PatientListUtil.split(data, 2);
        this.name = pcs[0];
        this.patientList = PatientListUtil.deserializePatientList(pcs[1]);
    }

    public Favorite(IPatientList list) {
        this.patientList = list.copy();
        this.name = list.getDisplayName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        PatientListUtil.append(sb, patientList.serialize());
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public IPatientList getPatientList() {
        return patientList;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Favorite)) {
            return false;
        }

        Favorite favorite = (Favorite) object;
        return ObjectUtils.equals(this.name, favorite.name) && ObjectUtils.equals(this.patientList, favorite.patientList);
    }

}
