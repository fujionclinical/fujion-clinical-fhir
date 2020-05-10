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
package org.fujionclinical.fhir.api.dstu2.condition;

import ca.uhn.fhir.model.dstu2.resource.Condition;

import java.util.Comparator;
import java.util.Date;

/**
 * Comparators for sorting lists of condition-related resources.
 */
public class Comparators {

    public static final Comparator<Condition> CONDITION_DATE_RECORDED = (o1, o2) -> {
        Date d1 = o1 == null ? null : o1.getDateRecorded();
        Date d2 = o2 == null ? null : o2.getDateRecorded();
        return d1 == d2 ? 0 : d1 == null ? -1 : d2 == null ? 1 : d1.compareTo(d2);
    };

    private Comparators() {
    }
}
