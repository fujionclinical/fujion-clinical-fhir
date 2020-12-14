import org.fujionclinical.fhir.api.stu3.common.Stu3Formatters;
import org.hl7.fhir.dstu3.model.HumanName;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Stu3Test {

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
        assertEquals("last, first middle", Stu3Formatters.formatName(humanName));
    }

}
