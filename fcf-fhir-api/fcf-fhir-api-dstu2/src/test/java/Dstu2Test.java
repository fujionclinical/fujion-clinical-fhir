import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import org.fujionclinical.fhir.api.dstu2.common.Dstu2Formatters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Dstu2Test {

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
        assertEquals("last, first middle", Dstu2Formatters.formatName(humanName));
    }

}
