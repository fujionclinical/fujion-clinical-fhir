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
package org.fujionclinical.fhir.api.r5.test;

import edu.utah.kmm.model.cool.foundation.datatype.PersonNameUse;
import org.fujion.common.DateUtil;
import org.fujionclinical.api.model.core.Address;
import org.fujionclinical.api.model.encounter.Encounter;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.impl.AddressImpl;
import org.fujionclinical.api.model.patient.IPatient;
import org.fujionclinical.api.model.patient.Patient;
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.person.PersonNameImpl;
import org.fujionclinical.api.model.person.PersonNameParser;
import org.fujionclinical.fhir.api.r5.common.FhirUtilR5;
import org.fujionclinical.fhir.api.r5.transform.PersonNameTransform;
import org.hl7.fhir.r5.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class CommonTest {

    @Test
    public void testCreateCodeableConcept() {
        CodeableConcept cc = FhirUtilR5.createCodeableConcept("system", "code", "display");
        Coding coding = FhirUtilR5.getFirst(cc.getCoding());
        assertEquals("system", coding.getSystem());
        assertEquals("code", coding.getCode());
        assertEquals("display", coding.getDisplay());
    }

    @Test
    public void testCreatePeriod() {
        Date startDate = DateUtil.toDate(5, 6, 2007);
        Date endDate = DateUtil.toDate(8, 9, 2010);
        Period p = FhirUtilR5.createPeriod(startDate, endDate);
        assertEquals(startDate, p.getStart());
        assertEquals(endDate, p.getEnd());
    }

    @Test
    public void testCreateIdentifier() {
        Identifier id = FhirUtilR5.createIdentifier("system", "value");
        assertEquals("system", id.getSystem());
        assertEquals("value", id.getValue());
    }

    @Test
    public void testConvertTimeUnitToEnum() {
        assertEquals(Timing.UnitsOfTime.A, FhirUtilR5.convertTimeUnitToEnum("a"));
        assertEquals(Timing.UnitsOfTime.S, FhirUtilR5.convertTimeUnitToEnum("s"));
        assertEquals(Timing.UnitsOfTime.MIN, FhirUtilR5.convertTimeUnitToEnum("min"));
        assertEquals(Timing.UnitsOfTime.H, FhirUtilR5.convertTimeUnitToEnum("h"));
        assertEquals(Timing.UnitsOfTime.D, FhirUtilR5.convertTimeUnitToEnum("d"));
        assertEquals(Timing.UnitsOfTime.WK, FhirUtilR5.convertTimeUnitToEnum("wk"));
        assertEquals(Timing.UnitsOfTime.MO, FhirUtilR5.convertTimeUnitToEnum("mo"));

        try {
            assertNull(FhirUtilR5.convertTimeUnitToEnum("bad"));
            fail("Should throw illegal argument exception.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFirstLast() {
        List<String> list = null;
        assertNull(FhirUtilR5.getFirst(list));
        assertNull(FhirUtilR5.getLast(list));
        list = new ArrayList<>();
        assertNull(FhirUtilR5.getFirst(list));
        assertNull(FhirUtilR5.getLast(list));
        list.add("first");
        list.add("second");
        list.add("last");
        assertEquals("first", FhirUtilR5.getFirst(list));
        assertEquals("last", FhirUtilR5.getLast(list));
    }

    @Test
    public void testAddressUtils() {
        AddressImpl a = new AddressImpl();
        a.setCity("city");
        a.setCountry("country");
        a.setDistrict("district");
        a.addLines("line1", "line2");
        a.setState("state");
        a.setPostalCode("postalcode");
        a.setUse(Address.PostalAddressUse.WORK);
        Patient patient = new Patient();
        patient.addAddresses(a);
        assertSame(a, patient.getAddress(Address.PostalAddressUse.WORK));
        assertNull(patient.getAddress(Address.PostalAddressUse.HOME));
    }

    @Test
    public void testNameUtils() {
        IPersonName personName = new PersonNameImpl();
        PersonNameParser.instance.fromString("last, first middle", personName);
        HumanName humanName = PersonNameTransform.getInstance().fromLogicalModel(personName);
        assertEquals("last", humanName.getFamily());
        assertEquals("first middle", humanName.getGivenAsSingleString());
        assertEquals("first", humanName.getGiven().get(0).getValue());
        assertEquals("middle", humanName.getGiven().get(1).getValue());
        assertEquals("last, first middle", FhirUtilR5.formatName(humanName));
        IPersonName personName2 = new PersonNameImpl();
        PersonNameParser.instance.fromString(",nickname", personName2);
        personName2.setUse(PersonNameUse.NICKNAME);
        HumanName humanName2 = PersonNameTransform.getInstance().fromLogicalModel(personName2);
        assertEquals(HumanName.NameUse.NICKNAME, humanName2.getUse());
        assertTrue(!humanName2.hasFamily());
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
