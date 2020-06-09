package org.fujionclinical.fhir.api.dstu2.condition;

import ca.uhn.fhir.model.dstu2.resource.Condition;
import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IWrapperTransform;

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
