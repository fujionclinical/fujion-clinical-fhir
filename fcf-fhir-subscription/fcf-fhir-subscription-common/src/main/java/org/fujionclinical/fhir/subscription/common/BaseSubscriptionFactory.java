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
package org.fujionclinical.fhir.subscription.common;

import edu.utah.kmm.model.cool.mediator.datasource.DataSources;
import edu.utah.kmm.model.cool.mediator.fhir.core.AbstractFhirDataSource;
import edu.utah.kmm.model.cool.terminology.ConceptReferenceImpl;

public abstract class BaseSubscriptionFactory {

    private final String dataSourceId;

    public BaseSubscriptionFactory(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    protected String getDataSourceId() {
        return dataSourceId;
    }

    protected AbstractFhirDataSource getDataSource() {
        return (AbstractFhirDataSource) DataSources.get(dataSourceId);
    }

    protected abstract BaseSubscriptionWrapper create(
            String paramIndex,
            String callbackUrl,
            ResourceSubscriptionService.PayloadType payloadType,
            String criteria,
            ConceptReferenceImpl tag);

}
