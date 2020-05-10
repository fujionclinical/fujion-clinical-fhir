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

import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.api.r5.common.FhirUtil;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.model.HumanName.NameUse;
import org.hl7.fhir.r5.model.Timing.UnitsOfTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class CommonTest {

    @Test
    public void testCreateCodeableConcept() {
        CodeableConcept cc = FhirUtil.createCodeableConcept("system", "code", "display");
        Coding coding = FhirUtil.getFirst(cc.getCoding());
        assertEquals("system", coding.getSystem());
        assertEquals("code", coding.getCode());
        assertEquals("display", coding.getDisplay());
    }

    @Test
    public void testCreatePeriod() {
        Date startDate = DateUtil.toDate(5, 6, 2007);
        Date endDate = DateUtil.toDate(8, 9, 2010);
        Period p = FhirUtil.createPeriod(startDate, endDate);
        assertEquals(startDate, p.getStart());
        assertEquals(endDate, p.getEnd());
    }

    @Test
    public void testCreateIdentifier() {
        Identifier id = FhirUtil.createIdentifier("system", "value");
        assertEquals("system", id.getSystem());
        assertEquals("value", id.getValue());
    }

    @Test
    public void testConvertTimeUnitToEnum() {
        assertEquals(UnitsOfTime.A, FhirUtil.convertTimeUnitToEnum("a"));
        assertEquals(UnitsOfTime.S, FhirUtil.convertTimeUnitToEnum("s"));
        assertEquals(UnitsOfTime.MIN, FhirUtil.convertTimeUnitToEnum("min"));
        assertEquals(UnitsOfTime.H, FhirUtil.convertTimeUnitToEnum("h"));
        assertEquals(UnitsOfTime.D, FhirUtil.convertTimeUnitToEnum("d"));
        assertEquals(UnitsOfTime.WK, FhirUtil.convertTimeUnitToEnum("wk"));
        assertEquals(UnitsOfTime.MO, FhirUtil.convertTimeUnitToEnum("mo"));

        try {
            assertEquals(UnitsOfTime.NULL, FhirUtil.convertTimeUnitToEnum("bad"));
            fail("Should throw illegal argument exception.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFirstLast() {
        List<String> list = null;
        assertNull(FhirUtil.getFirst(list));
        assertNull(FhirUtil.getLast(list));
        list = new ArrayList<>();
        assertNull(FhirUtil.getFirst(list));
        assertNull(FhirUtil.getLast(list));
        list.add("first");
        list.add("second");
        list.add("last");
        assertEquals("first", FhirUtil.getFirst(list));
        assertEquals("last", FhirUtil.getLast(list));
    }

    @Test
    public void testAddressUtils() {
        Address a = new Address();
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
        assertSame(a, FhirUtil.getFirst(FhirUtil.getAddresses(patient)));
        assertSame(a, FhirUtil.getFirst(FhirUtil.getAddresses(practitioner)));
        assertNull(FhirUtil.getFirst(FhirUtil.getAddresses(encounter)));
    }

    @Test
    public void testNameUtils() {
        HumanName n = FhirUtil.parseName("last, first middle");
        assertEquals("last", n.getFamily());
        assertEquals("first middle", n.getGivenAsSingleString());
        assertEquals("first", n.getGiven().get(0).getValue());
        assertEquals("middle", n.getGiven().get(1).getValue());
        assertEquals("last, first middle", FhirUtil.formatName(n));
        n.setUse(NameUse.USUAL);
        HumanName n2 = FhirUtil.parseName(",nickname");
        n2.setUse(NameUse.NICKNAME);
        List<HumanName> list = new ArrayList<>();
        list.add(n);
        list.add(n2);
        assertSame(n, FhirUtil.getName(list, NameUse.USUAL));
        assertSame(n, FhirUtil.getName(list, NameUse.USUAL, NameUse.NICKNAME));
        assertSame(n2, FhirUtil.getName(list, NameUse.NICKNAME));
        assertSame(n2, FhirUtil.getName(list, NameUse.NICKNAME, NameUse.USUAL));
        assertNull(FhirUtil.getName(list, NameUse.OLD));
        Patient patient = new Patient();
        patient.setName(list);
        Practitioner practitioner = new Practitioner();
        practitioner.setName(list);
        Encounter encounter = new Encounter();
        assertSame(list, FhirUtil.getNames(patient));
        assertSame(list, FhirUtil.getNames(practitioner));
        assertNull(FhirUtil.getNames(encounter));
    }

    @Test
    public void testEquals() {
        IBaseResource res1v1 = new Patient();
        res1v1.setId(createId("Patient", "1234", "1"));
        IBaseResource res1v2 = new Patient();
        res1v2.setId(createId("Patient", "1234", "2"));
        assertTrue(FhirUtil.areEqual(res1v1, res1v2, true));
        assertFalse(FhirUtil.areEqual(res1v1, res1v2, false));
        IBaseResource res2v1 = new Encounter();
        res2v1.setId(createId("Resource", "1234", "1"));
        IBaseResource res2 = new Encounter();
        res2.setId(createId("Resource", "1234", null));
        assertFalse(FhirUtil.areEqual(res1v1, res2v1));
        assertFalse(FhirUtil.areEqual(res2v1, res2));
        assertTrue(FhirUtil.areEqual(res2v1, res2, true));
        Reference ref1v1 = new Reference(res1v1.getIdElement());
        Reference ref1v2 = new Reference(res1v2.getIdElement());
        assertFalse(FhirUtil.areEqual(ref1v1, ref1v2));
        assertTrue(FhirUtil.areEqual(ref1v1, ref1v2, true));
        assertTrue(FhirUtil.areEqual(res1v1, ref1v1));
        assertFalse(FhirUtil.areEqual(res1v1, ref1v2));
        ref1v1 = new Reference((IAnyResource) res1v1);
        assertFalse(FhirUtil.areEqual(ref1v1, ref1v2));
        assertTrue(FhirUtil.areEqual(ref1v1, ref1v2, true));
        assertTrue(FhirUtil.areEqual(res1v1, ref1v1));
        assertFalse(FhirUtil.areEqual(res2v1, ref1v1));
    }

    private IdType createId(
            String resourceType,
            String id,
            String versionId) {
        return new IdType(resourceType, id, versionId);
    }
}
