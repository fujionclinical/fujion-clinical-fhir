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

import ca.uhn.fhir.context.FhirVersionEnum;
import org.fujionclinical.api.model.core.ConceptCode;
import org.fujionclinical.api.model.core.IConceptCode;
import org.fujionclinical.api.model.core.IDomainObject;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.query.QueryOperator;
import org.fujionclinical.fhir.api.common.core.ParameterMappings;
import org.fujionclinical.fhir.api.common.core.TagTransform;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void testTransforms() {
        IConceptCode code1 = new ConceptCode("system1", "code1", "text1");
        IConceptCode code2 = new ConceptCode("system2", "code2", "text2");
        IBaseCoding coding = TagTransform.instance.unwrap(code1);
        IConceptCode code3 = TagTransform.instance.wrap(coding);
        Assert.assertTrue(code1.isSame(code3));
        List<IConceptCode> codes1 = new ArrayList<>();
        Collections.addAll(codes1, code1, code2);
        List<IBaseCoding> codings = TagTransform.instance.unwrap(codes1);
        List<IConceptCode> codes2 = TagTransform.instance.wrap(codings);
        Assert.assertTrue(code1.isSame(codes2.get(0)));
        Assert.assertTrue(code2.isSame(codes2.get(1)));
    }
}
