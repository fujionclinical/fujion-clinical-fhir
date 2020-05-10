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

import org.apache.commons.lang.math.NumberUtils;
import org.fujionclinical.api.property.PropertyUtil;

/**
 * Maintains a single list of most recently used patients that is updated dynamically as patients
 * are selected.
 */
public class MRUPatientList extends PropertyBasedPatientList {

    private static final String LIST_SIZE_MAX_PROPERTY = "FCF.PATIENT.LIST.SIZE";
    private int pplListSizeMax = -1;

    public MRUPatientList(
            String propertyName,
            IPatientAdapterFactory patientAdapterFactory) {
        super("Recent Selections", null, propertyName, patientAdapterFactory);
        registerListener();
    }

    public MRUPatientList(MRUPatientList list) {
        super(list);
        registerListener();
    }

    /**
     * Registers the patient context change listener.
     */
    private void registerListener() {
        getPatientAdapterFactory().registerListener(patientAdapter -> {
            if (patientAdapter != null) {
                addPatient(patientAdapter, true);
                saveList(false);
            }
        });
    }

    /**
     * Returns the setting for the maximum list size for the list. For a MRU list, this value is
     * retrieved from a property. For a personal list, there is no effective size limit.
     *
     * @return The maximum list size. Defaults to 5.
     */
    @Override
    protected int getListSizeMax() {
        if (this.pplListSizeMax >= 0) {
            return this.pplListSizeMax;
        }

        try {
            String val = PropertyUtil.getValue(LIST_SIZE_MAX_PROPERTY, null);
            this.pplListSizeMax = NumberUtils.toInt(val, 5);
        } catch (Exception e) {
            this.pplListSizeMax = 5;
        }

        return this.pplListSizeMax;
    }

    @Override
    public int getSequence() {
        return -100;
    }

}
