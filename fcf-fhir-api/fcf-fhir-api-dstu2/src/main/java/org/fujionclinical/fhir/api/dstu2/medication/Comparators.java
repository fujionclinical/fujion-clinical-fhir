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
package org.fujionclinical.fhir.api.dstu2.medication;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.fujion.common.DateUtil;

import java.util.Comparator;
import java.util.Date;

/**
 * Comparators for sorting lists of medication-related resources.
 */
public class Comparators {
    
    public static final Comparator<MedicationAdministration> MED_ADMIN_EFFECTIVE_TIME = (o1, o2) -> o1 == o2 ? 0
            : o1 == null ? -1 : o2 == null ? 1 : DateUtil.compare(getEffectiveTime(o1), getEffectiveTime(o2));
    
    public static final Comparator<MedicationOrder> MED_ORDER_DATE_WRITTEN = (o1, o2) -> o1 == o2 ? 0 : o1 == null ? -1 : o2 == null ? 1 : o1.getDateWritten().compareTo(o2.getDateWritten());
    
    private static Date getEffectiveTime(MedicationAdministration ma) {
        try {
            if (ma.getEffectiveTime() instanceof DateTimeDt) {
                return ((DateTimeDt)ma.getEffectiveTime()).getValue();
            }
            
            if (ma.getEffectiveTime() instanceof PeriodDt) {
                return ((PeriodDt) ma.getEffectiveTime()).getStart();
            }
        } catch (Exception e) {}
        
        return null;
    }
    
    private Comparators() {
    }
}
