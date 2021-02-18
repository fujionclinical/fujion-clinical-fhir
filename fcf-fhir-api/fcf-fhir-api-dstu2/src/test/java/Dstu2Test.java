import org.fujionclinical.fhir.api.dstu2.common.Dstu2Formatters;
import org.hl7.fhir.dstu2.model.HumanName;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Dstu2Test {

    @Test
    public void testNameUtils() {
        HumanName humanName = new HumanName();
        humanName.addFamily("last");
        humanName.addGiven("first");
        humanName.addGiven("middle");
        assertEquals("last", humanName.getFamily().get(0).getValue());
        assertEquals("first", humanName.getGiven().get(0).getValue());
        assertEquals("middle", humanName.getGiven().get(1).getValue());
        assertEquals("last, first middle", Dstu2Formatters.formatName(humanName));
    }

}
