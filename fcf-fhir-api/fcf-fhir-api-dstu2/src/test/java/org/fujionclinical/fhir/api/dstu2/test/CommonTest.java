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
import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import org.fujion.common.DateUtil;
import org.fujionclinical.api.person.IPersonName;
import org.fujionclinical.api.person.PersonNameParser;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;
import org.fujionclinical.fhir.api.dstu2.common.PersonNameWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;
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
        AddressDt a = new AddressDt();
        a.setCity("city");
        a.setCountry("country");
        a.setDistrict("district");
        a.addLine("line1");
        a.addLine("line2");
        a.setState("state");
        a.setPostalCode("postalcode");
        Patient patient = new Patient();
        patient.addAddress(a);
        Practitioner practitioner = new Practitioner();
        practitioner.addAddress(a);
        Encounter encounter = new Encounter();
        assertSame(a, FhirUtilDstu2.getFirst(FhirUtilDstu2.getAddresses(patient)));
        assertSame(a, FhirUtilDstu2.getFirst(FhirUtilDstu2.getAddresses(practitioner)));
        assertNull(FhirUtilDstu2.getFirst(FhirUtilDstu2.getAddresses(encounter)));
    }

    @Test
    public void testNameUtils() {
        HumanNameDt n = new HumanNameDt();
        IPersonName wrapper = PersonNameWrapper.wrap(n);
        PersonNameParser.instance.fromString("last, first middle", wrapper);
        assertEquals("last", n.getFamilyAsSingleString());
        assertEquals("first middle", n.getGivenAsSingleString());
        assertEquals("first", n.getGiven().get(0).getValue());
        assertEquals("middle", n.getGiven().get(1).getValue());
        assertEquals("last, first middle", FhirUtilDstu2.formatName(n));
        wrapper.setUse(IPersonName.PersonNameUse.USUAL);
        HumanNameDt n2 = new HumanNameDt();
        IPersonName wrapper2 = PersonNameWrapper.wrap(n2);
        PersonNameParser.instance.fromString(",nickname", wrapper2);
        wrapper2.setUse(IPersonName.PersonNameUse.NICKNAME);
        List<HumanNameDt> list = new ArrayList<>();
        list.add(n);
        list.add(n2);
        assertSame(n, FhirUtilDstu2.getName(list, NameUseEnum.USUAL));
        assertSame(n, FhirUtilDstu2.getName(list, NameUseEnum.USUAL, NameUseEnum.NICKNAME));
        assertSame(n2, FhirUtilDstu2.getName(list, NameUseEnum.NICKNAME));
        assertSame(n2, FhirUtilDstu2.getName(list, NameUseEnum.NICKNAME, NameUseEnum.USUAL));
        assertNull(FhirUtilDstu2.getName(list, NameUseEnum.OLD));
        Patient patient = new Patient();
        patient.setName(list);
        Practitioner practitioner = new Practitioner();
        practitioner.setName(n);
        Encounter encounter = new Encounter();
        assertSame(list, FhirUtilDstu2.getNames(patient));
        assertSame(n, FhirUtilDstu2.getNames(practitioner).get(0));
        assertNull(FhirUtilDstu2.getNames(encounter));
    }

    @Test
    public void testEquals() {
        IBaseResource res1v1 = new Patient();
        res1v1.setId(createId("Patient", "1234", "1"));
        IBaseResource res1v2 = new Patient();
        res1v2.setId(createId("Patient", "1234", "2"));
        assertTrue(FhirUtilDstu2.areEqual(res1v1, res1v2, true));
        assertFalse(FhirUtilDstu2.areEqual(res1v1, res1v2, false));
        IBaseResource res2v1 = new Encounter();
        res2v1.setId(createId("Resource", "1234", "1"));
        IBaseResource res2 = new Encounter();
        res2.setId(createId("Resource", "1234", null));
        assertFalse(FhirUtilDstu2.areEqual(res1v1, res2v1));
        assertFalse(FhirUtilDstu2.areEqual(res2v1, res2));
        assertTrue(FhirUtilDstu2.areEqual(res2v1, res2, true));
        ResourceReferenceDt ref1v1 = new ResourceReferenceDt(res1v1.getIdElement());
        ResourceReferenceDt ref1v2 = new ResourceReferenceDt(res1v2.getIdElement());
        assertFalse(FhirUtilDstu2.areEqual(ref1v1, ref1v2));
        assertTrue(FhirUtilDstu2.areEqual(ref1v1, ref1v2, true));
        assertTrue(FhirUtilDstu2.areEqual(res1v1, ref1v1));
        assertFalse(FhirUtilDstu2.areEqual(res1v1, ref1v2));
        ref1v1 = new ResourceReferenceDt((BaseResource) res1v1);
        assertFalse(FhirUtilDstu2.areEqual(ref1v1, ref1v2));
        assertTrue(FhirUtilDstu2.areEqual(ref1v1, ref1v2, true));
        assertTrue(FhirUtilDstu2.areEqual(res1v1, ref1v1));
        assertFalse(FhirUtilDstu2.areEqual(res2v1, ref1v1));
    }

    private IdDt createId(
            String resourceType,
            String id,
            String versionId) {
        return new IdDt(resourceType, id, versionId);
    }
}
