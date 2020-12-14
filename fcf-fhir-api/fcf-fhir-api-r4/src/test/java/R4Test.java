import org.fujionclinical.fhir.api.r4.common.R4Formatters;
import org.hl7.fhir.r4.model.HumanName;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class R4Test {

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
        assertEquals("last, first middle", R4Formatters.formatName(humanName));
    }

}
