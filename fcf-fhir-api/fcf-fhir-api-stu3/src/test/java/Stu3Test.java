import org.fujion.common.CollectionUtil;
import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.api.stu3.common.Formatting;
import org.fujionclinical.fhir.api.stu3.common.Stu3Util;
import org.hl7.fhir.dstu3.model.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class Stu3Test {

    @Test
    public void testCreateCodeableConcept() {
        CodeableConcept cc = Stu3Util.createCodeableConcept("system", "code", "display");
        Coding coding = CollectionUtil.getFirst(cc.getCoding());
        assertEquals("system", coding.getSystem());
        assertEquals("code", coding.getCode());
        assertEquals("display", coding.getDisplay());
    }

    @Test
    public void testCreatePeriod() {
        Date startDate = DateUtil.parseDate("2007-05-06");
        Date endDate = DateUtil.parseDate("2010-08-08");
        Period p = Stu3Util.createPeriod(startDate, endDate);
        assertEquals(startDate, p.getStart());
        assertEquals(endDate, p.getEnd());
    }

    @Test
    public void testConvertTimeUnitToEnum() {
        assertEquals(Timing.UnitsOfTime.A, Stu3Util.convertTimeUnitToEnum("a"));
        assertEquals(Timing.UnitsOfTime.S, Stu3Util.convertTimeUnitToEnum("s"));
        assertEquals(Timing.UnitsOfTime.MIN, Stu3Util.convertTimeUnitToEnum("min"));
        assertEquals(Timing.UnitsOfTime.H, Stu3Util.convertTimeUnitToEnum("h"));
        assertEquals(Timing.UnitsOfTime.D, Stu3Util.convertTimeUnitToEnum("d"));
        assertEquals(Timing.UnitsOfTime.WK, Stu3Util.convertTimeUnitToEnum("wk"));
        assertEquals(Timing.UnitsOfTime.MO, Stu3Util.convertTimeUnitToEnum("mo"));

        try {
            assertNull(Stu3Util.convertTimeUnitToEnum("bad"));
            fail("Should throw illegal argument exception.");
        } catch (IllegalArgumentException e) {
            // NOP
        }
    }

    @Test
    public void testNameUtils() {
        HumanName humanName = new HumanName();
        humanName.setFamily("last");
        humanName.addGiven("first");
        humanName.addGiven("middle");
        assertEquals("last", humanName.getFamily());
        assertEquals("first middle", humanName.getGivenAsSingleString());
        assertEquals("first", humanName.getGiven().get(0).getValue());
        assertEquals("middle", humanName.getGiven().get(1).getValue());
        assertEquals("last, first middle", Formatting.format(humanName));
    }

}
