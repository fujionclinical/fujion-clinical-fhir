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
package org.fujionclinical.fhir.api.dstu2.common;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.person.IPersonName;

public class PersonNameTransform implements IWrapperTransform<IPersonName, HumanNameDt> {

    private static final PersonNameTransform instance = new PersonNameTransform();

    public static PersonNameTransform getInstance() {
        return instance;
    }

    @Override
    public IPersonName _wrap(HumanNameDt value) {
        return new PersonNameWrapper(value);
    }

    @Override
    public HumanNameDt newWrapped() {
        return new HumanNameDt();
    }

}
