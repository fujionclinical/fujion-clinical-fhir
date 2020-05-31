package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import org.fujionclinical.api.model.IContactPoint;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IWrapper;
import org.fujionclinical.fhir.api.common.core.FhirUtil;

public class ContactPointWrapper implements IContactPoint, IWrapper<ContactPointDt> {

    private final ContactPointDt contactPoint;

    private PeriodWrapper period;

    public static ContactPointWrapper wrap(ContactPointDt contactPoint) {
        return contactPoint == null ? null : new ContactPointWrapper(contactPoint);
    }

    private ContactPointWrapper(ContactPointDt contactPoint) {
        this.contactPoint = contactPoint;
        period = PeriodWrapper.wrap(contactPoint.getPeriod());
    }

    @Override
    public ContactPointSystem getSystem() {
        return FhirUtil.convertEnum(contactPoint.getSystemElement().getValueAsEnum(), ContactPointSystem.class);
    }

    @Override
    public void setSystem(ContactPointSystem system) {
        contactPoint.setSystem(FhirUtil.convertEnum(system, ContactPointSystemEnum.class));
    }

    @Override
    public String getValue() {
        return contactPoint.getValue();
    }

    @Override
    public void setValue(String value) {
        contactPoint.setValue(value);
    }

    @Override
    public ContactPointUse getUse() {
        return FhirUtil.convertEnum(contactPoint.getUseElement().getValueAsEnum(), ContactPointUse.class);
    }

    @Override
    public void setUse(ContactPointUse use) {
        contactPoint.setUse(FhirUtil.convertEnum(use, ContactPointUseEnum.class));
    }

    @Override
    public Integer getRank() {
        return contactPoint.getRank();
    }

    @Override
    public void setRank(Integer rank) {
        contactPoint.setRank(rank);
    }

    @Override
    public IPeriod getPeriod() {
        return period;
    }

    @Override
    public void setPeriod(IPeriod period) {
        this.period = PeriodWrapper.wrap(PeriodWrapper.unwrap(period));
        contactPoint.setPeriod(this.period.getWrapped());
    }

    @Override
    public ContactPointDt getWrapped() {
        return contactPoint;
    }

}
