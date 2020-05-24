package org.fujionclinical.fhir.api.r5.common;

import org.fujionclinical.api.model.IContactPoint;
import org.fujionclinical.api.model.IPeriod;
import org.fujionclinical.api.model.IWrapper;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.r5.model.ContactPoint;

public class ContactPointWrapper implements IContactPoint, IWrapper<ContactPoint> {

    public static ContactPointWrapper wrap(ContactPoint contactPoint) {
        return contactPoint == null ? null : new ContactPointWrapper(contactPoint);
    }

    private final ContactPoint contactPoint;

    private PeriodWrapper period;

    private ContactPointWrapper(ContactPoint contactPoint) {
        this.contactPoint = contactPoint;
        period = PeriodWrapper.wrap(contactPoint.getPeriod());
    }

    @Override
    public ContactPointSystem getSystem() {
        return FhirUtil.convertEnum(contactPoint.getSystem(), ContactPointSystem.class);
    }

    @Override
    public IContactPoint setSystem(ContactPointSystem system) {
        contactPoint.setSystem(FhirUtil.convertEnum(system, ContactPoint.ContactPointSystem.class));
        return this;
    }

    @Override
    public String getValue() {
        return contactPoint.getValue();
    }

    @Override
    public IContactPoint setValue(String value) {
        contactPoint.setValue(value);
        return this;
    }

    @Override
    public ContactPointUse getUse() {
        return FhirUtil.convertEnum(contactPoint.getUse(), ContactPointUse.class);
    }

    @Override
    public IContactPoint setUse(ContactPointUse use) {
        contactPoint.setUse(FhirUtil.convertEnum(use, ContactPoint.ContactPointUse.class));
        return this;
    }

    @Override
    public Integer getRank() {
        return contactPoint.getRank();
    }

    @Override
    public IContactPoint setRank(Integer rank) {
        contactPoint.setRank(rank);
        return this;
    }

    @Override
    public IPeriod getPeriod() {
        return period;
    }

    @Override
    public IContactPoint setPeriod(IPeriod period) {
        this.period = PeriodWrapper.wrap(PeriodWrapper.unwrap(period));
        contactPoint.setPeriod(this.period.getWrapped());
        return this;
    }

    @Override
    public ContactPoint getWrapped() {
        return contactPoint;
    }
}
