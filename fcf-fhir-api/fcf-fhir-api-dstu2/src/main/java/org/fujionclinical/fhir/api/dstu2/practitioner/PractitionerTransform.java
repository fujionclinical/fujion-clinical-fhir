package org.fujionclinical.fhir.api.dstu2.practitioner;

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.practitioner.IPractitioner;

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
