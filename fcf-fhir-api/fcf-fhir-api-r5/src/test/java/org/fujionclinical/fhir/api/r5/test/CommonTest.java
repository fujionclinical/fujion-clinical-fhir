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
import org.fujionclinical.api.model.person.IPersonName;
import org.fujionclinical.api.model.person.PersonNameParser;
import org.fujionclinical.fhir.api.r5.common.FhirUtilR5;
import org.fujionclinical.fhir.api.r5.common.PersonNameTransform;
import org.hl7.fhir.instance.model.api.IBaseResource;
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
        assertSame(a, FhirUtilR5.getFirst(FhirUtilR5.getAddresses(patient)));
        assertSame(a, FhirUtilR5.getFirst(FhirUtilR5.getAddresses(practitioner)));
        assertNull(FhirUtilR5.getFirst(FhirUtilR5.getAddresses(encounter)));
    }

    @Test
    public void testNameUtils() {
        HumanName n = new HumanName();
        IPersonName wrapper = PersonNameTransform.getInstance().wrap(n);
        PersonNameParser.instance.fromString("last, first middle", wrapper);
        assertEquals("last", n.getFamily());
        assertEquals("first middle", n.getGivenAsSingleString());
        assertEquals("first", n.getGiven().get(0).getValue());
        assertEquals("middle", n.getGiven().get(1).getValue());
        assertEquals("last, first middle", FhirUtilR5.formatName(n));
        n.setUse(HumanName.NameUse.USUAL);
        HumanName n2 = new HumanName();
        IPersonName wrapper2 = PersonNameTransform.getInstance().wrap(n);
        PersonNameParser.instance.fromString(",nickname", wrapper2);
        n2.setUse(HumanName.NameUse.NICKNAME);
        List<HumanName> list = new ArrayList<>();
        list.add(n);
        list.add(n2);
        assertSame(n, FhirUtilR5.getName(list, HumanName.NameUse.USUAL));
        assertSame(n, FhirUtilR5.getName(list, HumanName.NameUse.USUAL, HumanName.NameUse.NICKNAME));
        assertSame(n2, FhirUtilR5.getName(list, HumanName.NameUse.NICKNAME));
        assertSame(n2, FhirUtilR5.getName(list, HumanName.NameUse.NICKNAME, HumanName.NameUse.USUAL));
        assertNull(FhirUtilR5.getName(list, HumanName.NameUse.OLD));
        Patient patient = new Patient();
        patient.setName(list);
        Practitioner practitioner = new Practitioner();
        practitioner.setName(list);
        Encounter encounter = new Encounter();
        assertSame(list, FhirUtilR5.getNames(patient));
        assertSame(list, FhirUtilR5.getNames(practitioner));
        assertNull(FhirUtilR5.getNames(encounter));
    }

    @Test
    public void testEquals() {
        IBaseResource res1v1 = new Patient();
        res1v1.setId(createId("Patient", "1234", "1"));
        IBaseResource res1v2 = new Patient();
        res1v2.setId(createId("Patient", "1234", "2"));
        assertTrue(FhirUtilR5.areEqual(res1v1, res1v2, true));
        assertFalse(FhirUtilR5.areEqual(res1v1, res1v2, false));
        IBaseResource res2v1 = new Encounter();
        res2v1.setId(createId("Resource", "1234", "1"));
        IBaseResource res2 = new Encounter();
        res2.setId(createId("Resource", "1234", null));
        assertFalse(FhirUtilR5.areEqual(res1v1, res2v1));
        assertFalse(FhirUtilR5.areEqual(res2v1, res2));
        assertTrue(FhirUtilR5.areEqual(res2v1, res2, true));
        Reference ref1v1 = new Reference(res1v1.getIdElement());
        Reference ref1v2 = new Reference(res1v2.getIdElement());
        assertFalse(FhirUtilR5.areEqual(ref1v1, ref1v2));
        assertTrue(FhirUtilR5.areEqual(ref1v1, ref1v2, true));
        assertTrue(FhirUtilR5.areEqual(res1v1, ref1v1));
        assertFalse(FhirUtilR5.areEqual(res1v1, ref1v2));
        ref1v1 = new Reference((BaseResource) res1v1);
        assertFalse(FhirUtilR5.areEqual(ref1v1, ref1v2));
        assertTrue(FhirUtilR5.areEqual(ref1v1, ref1v2, true));
        assertTrue(FhirUtilR5.areEqual(res1v1, ref1v1));
        assertFalse(FhirUtilR5.areEqual(res2v1, ref1v1));
    }

    private IdType createId(
            String resourceType,
            String id,
            String versionId) {
        return new IdType(resourceType, id, versionId);
    }
}
