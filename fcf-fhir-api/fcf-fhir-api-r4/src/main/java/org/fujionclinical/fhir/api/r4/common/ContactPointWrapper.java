package org.fujionclinical.fhir.api.r4.common;

import org.fujionclinical.api.model.IContactPoint;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IWrapper;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.r4.model.ContactPoint;

public class ContactPointWrapper implements IContactPoint, IWrapper<ContactPoint> {

    private final ContactPoint contactPoint;

    private PeriodWrapper period;

    public static ContactPointWrapper wrap(ContactPoint contactPoint) {
        return contactPoint == null ? null : new ContactPointWrapper(contactPoint);
    }

    private ContactPointWrapper(ContactPoint contactPoint) {
        this.contactPoint = contactPoint;
        period = PeriodWrapper.wrap(contactPoint.getPeriod());
    }

    @Override
    public ContactPointSystem getSystem() {
        return FhirUtil.convertEnum(contactPoint.getSystem(), ContactPointSystem.class);
    }

    @Override
    public void setSystem(ContactPointSystem system) {
        contactPoint.setSystem(FhirUtil.convertEnum(system, ContactPoint.ContactPointSystem.class));
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
        return FhirUtil.convertEnum(contactPoint.getUse(), ContactPointUse.class);
    }

    @Override
    public void setUse(ContactPointUse use) {
        contactPoint.setUse(FhirUtil.convertEnum(use, ContactPoint.ContactPointUse.class));
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
    public ContactPoint getWrapped() {
        return contactPoint;
    }

}
