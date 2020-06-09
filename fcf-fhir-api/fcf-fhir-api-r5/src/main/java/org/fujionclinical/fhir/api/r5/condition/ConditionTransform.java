package org.fujionclinical.fhir.api.r5.condition;

import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.r5.model.Condition;

public class ConditionTransform implements IWrapperTransform<ICondition, Condition> {

    public static final ConditionTransform instance = new ConditionTransform();

    @Override
    public ICondition _wrap(Condition value) {
        return new ConditionWrapper(value);
    }

    @Override
    public Condition newWrapped() {
        return new Condition();
    }

}
