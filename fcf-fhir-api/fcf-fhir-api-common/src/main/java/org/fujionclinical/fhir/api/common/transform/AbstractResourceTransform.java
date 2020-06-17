package org.fujionclinical.fhir.api.common.transform;

import org.fujionclinical.api.model.core.IDomainType;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;

public abstract class AbstractResourceTransform<L extends IDomainType, N extends IBaseResource> extends AbstractModelTransform<L, N> {

    protected AbstractResourceTransform(
            Class<L> logicalModelType,
            Class<N> nativeModelType) {
        super(logicalModelType, nativeModelType);
    }

    protected abstract L newLogical();

    protected abstract N newNative();

    @Override
    public N _fromLogicalModel(L src) {
        N dest = newNative();
        String resourceType = FhirUtil.getResourceName(dest);
        dest.setId(resourceType + "/" + src.getId());
        src.getTags().forEach(tag -> {
            IBaseCoding tg = dest.getMeta().addTag();
            tg.setSystem(tag.getSystem());
            tg.setCode(tag.getCode());
            tg.setDisplay(tag.getText());
        });
        return dest;
    }

    @Override
    public L _toLogicalModel(N src) {
        L dest = newLogical();
        dest.setId(src.getIdElement().getIdPart());
        src.getMeta().getTag().forEach(tag -> dest.addTags(TagTransform.getInstance().toLogicalModel(tag)));
        return dest;
    }

}
