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
package org.fujionclinical.fhir.api.common.test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import org.fujionclinical.api.model.IDomainObject;
import org.fujionclinical.api.patient.IPatient;
import org.fujionclinical.api.query.QueryOperator;
import org.fujionclinical.fhir.api.common.core.ParameterMappings;
import org.junit.Assert;
import org.junit.Test;

public class CommonTest {

    @Test
    public void testParameterMappings() {
        ParameterMappings.getInstance().setFhirVersion(FhirVersionEnum.DSTU3);
        testParameterMapping("xxx-yyy-zzz", "xxx.YYY.zzz", QueryOperator.EQ, IPatient.class);
        testParameterMapping("death-date", "deceasedDate", QueryOperator.EQ, IPatient.class);
        testParameterMapping("name:exact", "fullName", QueryOperator.EQ, IPatient.class);
        testParameterMapping("name", "name", QueryOperator.SW, IPatient.class);
        testParameterMapping("_id", "id", QueryOperator.SW, IPatient.class);
    }

    private void testParameterMapping(
            String expected,
            String propertyPath,
            QueryOperator operator,
            Class<? extends IDomainObject> domainClass) {
        Assert.assertEquals(expected, ParameterMappings.getParameterName(propertyPath, operator, domainClass));
    }

}
