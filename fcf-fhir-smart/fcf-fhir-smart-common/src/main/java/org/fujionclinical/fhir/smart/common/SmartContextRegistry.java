/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2018 fujionclinical.org
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
package org.fujionclinical.fhir.smart.common;

import org.fujion.common.Logger;
import org.fujionclinical.api.spring.BeanRegistry;
import org.fujionclinical.api.spring.SpringUtil;
import org.springframework.util.Assert;

/**
 * Registry for SMART contexts.
 */
public class SmartContextRegistry extends BeanRegistry<String, ISmartContext> {

    private static Logger log = Logger.create(SmartContextRegistry.class);

    public static SmartContextRegistry getInstance() {
        return SpringUtil.getBean("smartContextRegistry", SmartContextRegistry.class);
    }
    public SmartContextRegistry() {
        super(ISmartContext.class);
    }

    @Override
    protected String getKey(ISmartContext item) {
        return item.getContextScope();
    }

    /**
     * Returns the SMART context implementation corresponding to the specified scope.
     *
     * @param contextScope The name of the SMART context scope.
     * @return The context implementation.
     */
    @Override
    public ISmartContext get(String contextScope) {
        ISmartContext context = super.get(contextScope);
        Assert.notNull(context, "Unknown SMART context type: " + contextScope);
        return context;
    }

    @Override
    protected void onRegister(String contextScope, ISmartContext iSmartContext) {
        log.info("Registered SMART context type '" + contextScope + "'.");
    }
}
