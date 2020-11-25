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
package org.fujionclinical.fhir.lib.sharedforms.stu3.controller;

import edu.utah.kmm.model.cool.mediator.fhir.stu3.common.FhirDataSource;
import edu.utah.kmm.model.cool.mediator.fhir.stu3.common.Stu3Utils;
import org.fujionclinical.fhir.lib.sharedforms.common.BaseResourceListView;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

/**
 * Controller for displaying FHIR resources in a columnar format.
 *
 * @param <R> Type of resource object.
 * @param <M> Type of model object.
 */
public abstract class ResourceListView<R extends IBaseResource, M> extends BaseResourceListView<R, M, FhirDataSource, Bundle> {

    @Override
    protected String transformData(Object data) {
        return Stu3Utils.getDisplayValueForType(data);
    }

    /**
     * Extracts results from the returned bundle. Override for special processing.
     *
     * @param bundle The bundle.
     * @return List of extracted resources.
     */
    protected List<R> processBundle(Bundle bundle) {
        return Stu3Utils.getEntries(bundle, getResourceClass());
    }

}
