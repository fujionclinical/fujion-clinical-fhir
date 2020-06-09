package org.fujionclinical.fhir.api.r5.practitioner;

import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.practitioner.IPractitioner;
import org.hl7.fhir.r5.model.Practitioner;
import org.springframework.beans.BeanUtils;

public class PractitionerTransform implements IWrapperTransform<IPractitioner, Practitioner> {

    public static final PractitionerTransform instance = new PractitionerTransform();

    @Override
    public Practitioner _unwrap(IPractitioner value) {
        Practitioner wrapped = new Practitioner();
        IPractitioner practitioner = wrap(wrapped);
        BeanUtils.copyProperties(value, practitioner);
        return wrapped;
    }

    @Override
    public IPractitioner _wrap(Practitioner value) {
        return new PractitionerWrapper(value);
    }

}
