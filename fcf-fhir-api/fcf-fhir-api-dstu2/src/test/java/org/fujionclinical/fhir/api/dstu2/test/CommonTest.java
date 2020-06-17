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
package org.fujionclinical.fhir.api.dstu2.test;

import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import org.fujion.common.DateUtil;
import org.fujionclinical.api.model.core.IPostalAddress;
import org.fujionclinical.api.model.encounter.Encounter;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.impl.PersonName;
import org.fujionclinical.api.model.impl.PostalAddress;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.patient.Patient;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.person.PersonNameParser;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;
import org.fujionclinical.fhir.api.dstu2.transform.PersonNameTransform;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class CommonTest {

    @Test
    public void testCreateCodeableConcept() {
        CodeableConceptDt cc = FhirUtilDstu2.createCodeableConcept("system", "code", "display");
        CodingDt coding = FhirUtilDstu2.getFirst(cc.getCoding());
        assertEquals("system", coding.getSystem());
        assertEquals("code", coding.getCode());
        assertEquals("display", coding.getDisplay());
    }

    @Test
    public void testCreatePeriod() {
        Date startDate = DateUtil.toDate(5, 6, 2007);
        Date endDate = DateUtil.toDate(8, 9, 2010);
        PeriodDt p = FhirUtilDstu2.createPeriod(startDate, endDate);
        assertEquals(startDate, p.getStart());
        assertEquals(endDate, p.getEnd());
    }

    @Test
    public void testCreateIdentifier() {
        IdentifierDt id = FhirUtilDstu2.createIdentifier("system", "value");
        assertEquals("system", id.getSystem());
        assertEquals("value", id.getValue());
    }

    @Test
    public void testConvertTimeUnitToEnum() {
        assertEquals(UnitsOfTimeEnum.A, FhirUtilDstu2.convertTimeUnitToEnum("a"));
        assertEquals(UnitsOfTimeEnum.S, FhirUtilDstu2.convertTimeUnitToEnum("s"));
        assertEquals(UnitsOfTimeEnum.MIN, FhirUtilDstu2.convertTimeUnitToEnum("min"));
        assertEquals(UnitsOfTimeEnum.H, FhirUtilDstu2.convertTimeUnitToEnum("h"));
        assertEquals(UnitsOfTimeEnum.D, FhirUtilDstu2.convertTimeUnitToEnum("d"));
        assertEquals(UnitsOfTimeEnum.WK, FhirUtilDstu2.convertTimeUnitToEnum("wk"));
        assertEquals(UnitsOfTimeEnum.MO, FhirUtilDstu2.convertTimeUnitToEnum("mo"));

        try {
            assertNull(FhirUtilDstu2.convertTimeUnitToEnum("bad"));
            fail("Should throw illegal argument exception.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFirstLast() {
        List<String> list = null;
        assertNull(FhirUtilDstu2.getFirst(list));
        assertNull(FhirUtilDstu2.getLast(list));
        list = new ArrayList<>();
        assertNull(FhirUtilDstu2.getFirst(list));
        assertNull(FhirUtilDstu2.getLast(list));
        list.add("first");
        list.add("second");
        list.add("last");
        assertEquals("first", FhirUtilDstu2.getFirst(list));
        assertEquals("last", FhirUtilDstu2.getLast(list));
    }

    @Test
    public void testAddressUtils() {
        PostalAddress a = new PostalAddress();
        a.setCity("city");
        a.setCountry("country");
        a.setDistrict("district");
        a.addLines("line1", "line2");
        a.setState("state");
        a.setPostalCode("postalcode");
        a.setUse(IPostalAddress.PostalAddressUse.WORK);
        Patient patient = new Patient();
        patient.addAddresses(a);
        assertSame(a, patient.getAddress(IPostalAddress.PostalAddressUse.WORK));
        assertNull(patient.getAddress(IPostalAddress.PostalAddressUse.HOME));
    }

    @Test
    public void testNameUtils() {
        IPersonName personName = new PersonName();
        PersonNameParser.instance.fromString("last, first middle", personName);
        HumanNameDt humanName = PersonNameTransform.getInstance().fromLogicalModel(personName);
        assertEquals("last", humanName.getFamilyAsSingleString());
        assertEquals("first middle", humanName.getGivenAsSingleString());
        assertEquals("first", humanName.getGiven().get(0).getValue());
        assertEquals("middle", humanName.getGiven().get(1).getValue());
        assertEquals("last, first middle", FhirUtilDstu2.formatName(humanName));
        IPersonName personName2 = new PersonName();
        PersonNameParser.instance.fromString(",nickname", personName2);
        personName2.setUse(IPersonName.PersonNameUse.NICKNAME);
        HumanNameDt humanName2 = PersonNameTransform.getInstance().fromLogicalModel(personName2);
        assertEquals(NameUseEnum.NICKNAME, humanName2.getUseElement().getValueAsEnum());
        assertTrue(humanName2.getFamily().isEmpty());
        assertEquals("nickname", humanName2.getGivenAsSingleString());
    }

    @Test
    public void testEquals() {
        IPatient patient1 = new Patient();
        patient1.setId("1234");
        IPatient patient2 = new Patient();
        patient2.setId("1234");
        IPatient patient3 = new Patient();
        patient3.setId("4321");
        assertTrue(patient1.isSame(patient2));
        assertFalse(patient1.isSame(patient3));
        IEncounter encounter1 = new Encounter();
        encounter1.setId("1234");
        IEncounter encounter2 = new Encounter();
        encounter2.setId("1234");
        assertFalse(patient1.isSame(encounter1));
        assertTrue(encounter1.isSame(encounter2));
    }

}
