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
package org.fujionclinical.fhir.smart.common;

import org.fujion.common.Logger;
import org.fujionclinical.api.spring.SpringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

/**
 * Registry for SMART contexts.
 */
public class SmartContextRegistry implements DestructionAwareBeanPostProcessor {

    private static final Logger log = Logger.create(SmartContextRegistry.class);

    private final MultiValueMap<String, ISmartContext> registry = new LinkedMultiValueMap<>();

    public static SmartContextRegistry getInstance() {
        return SpringUtil.getBean("smartContextRegistry", SmartContextRegistry.class);
    }

    /**
     * Returns the SMART context implementation corresponding to the specified context name.
     *
     * @param contextName The name of the SMART context.
     * @return The context implementation.
     */
    public List<ISmartContext> get(String contextName) {
        List<ISmartContext> contexts = registry.get(contextName);
        return contexts == null ? Collections.emptyList() : Collections.unmodifiableList(contexts);
    }

    private void register(ISmartContext smartContext) {
        registry.add(smartContext.getContextName(), smartContext);
        log.info(() -> "Registered SMART context '" + smartContext.getContextName() + "'.");
    }

    private void unregister(ISmartContext smartContext) {
        registry.get(smartContext.getContextName()).remove(smartContext);
        log.info(() -> "Registered SMART context '" + smartContext.getContextName() + "'.");
    }

    /**
     * If the managed bean is of the desired type, add it to the registry.
     */
    @Override
    public Object postProcessAfterInitialization(
            Object bean,
            String beanName) throws BeansException {
        if (bean instanceof ISmartContext) {
            register((ISmartContext) bean);
        }

        return bean;
    }

    /**
     * If the managed bean is of the desired type, remove it from the registry.
     */
    @Override
    public void postProcessBeforeDestruction(
            Object bean,
            String beanName) throws BeansException {
        if (bean instanceof ISmartContext) {
            unregister((ISmartContext) bean);
        }
    }

    /**
     * Flag to unregister if the managed bean is of the desired type.
     */
    @Override
    public boolean requiresDestruction(Object bean) {
        return bean instanceof ISmartContext;
    }

}
