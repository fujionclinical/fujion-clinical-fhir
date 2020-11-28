import org.fujion.common.CollectionUtil;
import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.api.r4.common.Formatting;
import org.fujionclinical.fhir.api.r4.common.R4Util;
import org.hl7.fhir.r4.model.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class R4Test {

    @Test
    public void testCreateCodeableConcept() {
        CodeableConcept cc = R4Util.createCodeableConcept("system", "code", "display");
        Coding coding = CollectionUtil.getFirst(cc.getCoding());
        assertEquals("system", coding.getSystem());
        assertEquals("code", coding.getCode());
        assertEquals("display", coding.getDisplay());
    }

    @Test
    public void testCreatePeriod() {
        Date startDate = DateUtil.parseDate("2007-05-06");
        Date endDate = DateUtil.parseDate("2010-08-08");
        Period p = R4Util.createPeriod(startDate, endDate);
        assertEquals(startDate, p.getStart());
        assertEquals(endDate, p.getEnd());
    }

    @Test
    public void testConvertTimeUnitToEnum() {
        assertEquals(Timing.UnitsOfTime.A, R4Util.convertTimeUnitToEnum("a"));
        assertEquals(Timing.UnitsOfTime.S, R4Util.convertTimeUnitToEnum("s"));
        assertEquals(Timing.UnitsOfTime.MIN, R4Util.convertTimeUnitToEnum("min"));
        assertEquals(Timing.UnitsOfTime.H, R4Util.convertTimeUnitToEnum("h"));
        assertEquals(Timing.UnitsOfTime.D, R4Util.convertTimeUnitToEnum("d"));
        assertEquals(Timing.UnitsOfTime.WK, R4Util.convertTimeUnitToEnum("wk"));
        assertEquals(Timing.UnitsOfTime.MO, R4Util.convertTimeUnitToEnum("mo"));

        try {
            assertNull(R4Util.convertTimeUnitToEnum("bad"));
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
        assertEquals("last, first middle", Formatting.formatName(humanName));
    }

}
