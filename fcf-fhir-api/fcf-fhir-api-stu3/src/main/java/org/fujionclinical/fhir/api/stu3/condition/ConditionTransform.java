package org.fujionclinical.fhir.api.stu3.condition;

import org.fujionclinical.api.model.condition.ICondition;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.hl7.fhir.dstu3.model.Condition;
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
