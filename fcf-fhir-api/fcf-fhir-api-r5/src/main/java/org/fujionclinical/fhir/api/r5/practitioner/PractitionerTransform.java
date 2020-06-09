package org.fujionclinical.fhir.api.r5.practitioner;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.practitioner.IPractitioner;
import org.hl7.fhir.r5.model.Practitioner;

public class PractitionerTransform implements IWrapperTransform<IPractitioner, Practitioner> {

    public static final PractitionerTransform instance = new PractitionerTransform();

    @Override
    public IPractitioner _wrap(Practitioner value) {
        return new PractitionerWrapper(value);
    }

    @Override
    public Practitioner newWrapped() {
        return new Practitioner();
    }

}
