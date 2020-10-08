/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.api.common.transform;

import edu.utah.kmm.model.cool.terminology.ConceptReference;
import edu.utah.kmm.model.cool.terminology.ConceptReferenceSetImpl;
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
        src.getTags().forEach(value -> {
            ConceptReference tag = value.getFirstConcept();
            IBaseCoding tg = dest.getMeta().addTag();
            tg.setSystem(tag.getSystem().toString());
            tg.setCode(tag.getCode());
            tg.setDisplay(tag.getPreferredName());
        });
        return dest;
    }

    @Override
    public L _toLogicalModel(N src) {
        L dest = newLogical();
        dest.setId(src.getIdElement().getIdPart());
        src.getMeta().getTag().forEach(tag ->
                dest.addTags(new ConceptReferenceSetImpl(TagTransform.getInstance().toLogicalModel(tag))));
        return dest;
    }

}
