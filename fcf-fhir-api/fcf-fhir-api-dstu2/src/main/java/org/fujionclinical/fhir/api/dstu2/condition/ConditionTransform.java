package org.fujionclinical.fhir.api.dstu2.condition;

import ca.uhn.fhir.model.dstu2.resource.Condition;
import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.springframework.beans.BeanUtils;

public class ConditionTransform implements IWrapperTransform<ICondition, Condition> {

    public static final ConditionTransform instance = new ConditionTransform();

    @Override
    public Condition _unwrap(ICondition value) {
        Condition wrapped = new Condition();
        ICondition condition = wrap(wrapped);
        BeanUtils.copyProperties(value, condition);
        return wrapped;
    }

    @Override
    public ICondition _wrap(Condition value) {
        return new ConditionWrapper(value);
    }

}
