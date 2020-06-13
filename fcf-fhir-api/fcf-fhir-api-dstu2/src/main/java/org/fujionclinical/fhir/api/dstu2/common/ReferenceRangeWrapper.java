package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IQuantity;
import org.fujionclinical.api.model.core.IReferenceRange;

public class ReferenceRangeWrapper extends AbstractWrapper<Observation.ReferenceRange> implements IReferenceRange<Double> {

    private IConcept type;

    private IQuantity<Double> low;

    private IQuantity<Double> high;

    protected ReferenceRangeWrapper(Observation.ReferenceRange wrapped) {
        super(wrapped);
        this.type = ConceptTransform.getInstance().wrap(wrapped.getMeaning());
        this.low = SimpleQuantityTransform.getInstance().wrap(wrapped.getLow());
        this.high = SimpleQuantityTransform.getInstance().wrap(wrapped.getHigh());
    }

    @Override
    public IConcept getType() {
        return type;
    }

    @Override
    public void setType(IConcept value) {
        type = value;
        getWrapped().setMeaning(ConceptTransform.getInstance().unwrap(value));
    }

    @Override
    public String getDescription() {
        return getWrapped().getText();
    }

    @Override
    public void setDescription(String value) {
        getWrapped().setText(value);
    }

    @Override
    public IQuantity<Double> getLow() {
        return low;
    }

    @Override
    public void setLow(IQuantity<Double> value) {
        getWrapped().setLow(value == null ? null : SimpleQuantityTransform.getInstance().unwrap(value));
    }

    @Override
    public IQuantity<Double> getHigh() {
        return high;
    }

    @Override
    public void setHigh(IQuantity<Double> value) {
        getWrapped().setHigh(value == null ? null : SimpleQuantityTransform.getInstance().unwrap(value));
    }

}
