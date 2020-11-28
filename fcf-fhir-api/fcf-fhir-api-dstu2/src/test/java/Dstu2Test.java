import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import org.fujion.common.CollectionUtil;
import org.fujion.common.DateUtil;
import org.fujionclinical.fhir.api.dstu2.common.Dstu2Util;
import org.fujionclinical.fhir.api.dstu2.common.Formatting;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class Dstu2Test {

    @Test
    public void testCreateCodeableConcept() {
        CodeableConceptDt cc = Dstu2Util.createCodeableConcept("system", "code", "display");
        CodingDt coding = CollectionUtil.getFirst(cc.getCoding());
        assertEquals("system", coding.getSystem());
        assertEquals("code", coding.getCode());
        assertEquals("display", coding.getDisplay());
    }

    @Test
    public void testCreatePeriod() {
        Date startDate = DateUtil.parseDate("2007-05-06");
        Date endDate = DateUtil.parseDate("2010-08-08");
        PeriodDt p = Dstu2Util.createPeriod(startDate, endDate);
        assertEquals(startDate, p.getStart());
        assertEquals(endDate, p.getEnd());
    }

    @Test
    public void testConvertTimeUnitToEnum() {
        assertEquals(UnitsOfTimeEnum.A, Dstu2Util.convertTimeUnitToEnum("a"));
        assertEquals(UnitsOfTimeEnum.S, Dstu2Util.convertTimeUnitToEnum("s"));
        assertEquals(UnitsOfTimeEnum.MIN, Dstu2Util.convertTimeUnitToEnum("min"));
        assertEquals(UnitsOfTimeEnum.H, Dstu2Util.convertTimeUnitToEnum("h"));
        assertEquals(UnitsOfTimeEnum.D, Dstu2Util.convertTimeUnitToEnum("d"));
        assertEquals(UnitsOfTimeEnum.WK, Dstu2Util.convertTimeUnitToEnum("wk"));
        assertEquals(UnitsOfTimeEnum.MO, Dstu2Util.convertTimeUnitToEnum("mo"));

        try {
            assertNull(Dstu2Util.convertTimeUnitToEnum("bad"));
            fail("Should throw illegal argument exception.");
        } catch (IllegalArgumentException e) {
            // NOP
        }
    }

    @Test
    public void testNameUtils() {
        HumanNameDt humanName = new HumanNameDt();
        humanName.addFamily("last");
        humanName.addGiven("first");
        humanName.addGiven("middle");
        assertEquals("last", humanName.getFamilyAsSingleString());
        assertEquals("first middle", humanName.getGivenAsSingleString());
        assertEquals("first", humanName.getGiven().get(0).getValue());
        assertEquals("middle", humanName.getGiven().get(1).getValue());
        assertEquals("last, first middle", Formatting.formatName(humanName));
    }

}
