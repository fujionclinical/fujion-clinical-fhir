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
package org.fujionclinical.fhir.api.common.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.narrative.CustomThymeleafNarrativeGenerator;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.INarrative;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * Wraps hapi-fhir's narrative generator as a service.
 */
public abstract class BaseNarrativeService implements ApplicationContextAware {
    
    private final CustomThymeleafNarrativeGenerator generator;

    private final FhirContext fhirContext;

    public BaseNarrativeService(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
        generator = new CustomThymeleafNarrativeGenerator();
        fhirContext.setNarrativeGenerator(generator);
    }
    
    /**
     * Generate a narrative for the resource.
     *
     * @param resource Resource for which to generate a narrative.
     * @return The generated narrative, or null if narrative generation is not supported for this
     *         resource.
     */
    public boolean generateNarrative(IBaseResource resource) {
        return generator.populateResourceNarrative(fhirContext, resource);
    }

    /**
     * Returns a narrative from the resource, if one is available, or constructs one if not.
     *
     * @param resource Resource whose narrative is sought.
     * @param autoCreate If true, and a narrative does not exist on the resource, generate one if
     *            possible.
     * @return The narrative, or null if one is not available.
     */
    public INarrative extractNarrative(IBaseResource resource, boolean autoCreate) {
        INarrative narrative = extractNarrative(resource);
        
        if (autoCreate && isNarrativeEmpty(narrative)) {
            narrative = generateNarrative(resource) ? extractNarrative(resource) : null;
        }

        return isNarrativeEmpty(narrative) ? null : narrative;
    }

    protected abstract INarrative extractNarrative(IBaseResource resource);

    /**
     * Returns true if the narrative is null or effectively empty.
     *
     * @param narrative The narrative.
     * @return True if the narrative is null or effectively empty.
     */
    public boolean isNarrativeEmpty(INarrative narrative) {
        return narrative == null || narrative.isEmpty();
    }

    /**
     * Discovers all narrative property files, copying them into a single temporary file which is
     * then passed to the narrative generator.
     *
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            File file = File.createTempFile("fcf", ".properties");

            try (FileOutputStream out = new FileOutputStream(file)) {
                findPropertyFiles(applicationContext, "classpath*:META-INF/narratives.properties", out);
                findPropertyFiles(applicationContext, "classpath*:WEB-INF/narratives.properties", out);
            }
            
            generator.setPropertyFile("file:" + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
                
    private void findPropertyFiles(ApplicationContext applicationContext, String path, OutputStream out) throws IOException {
        for (Resource resource : applicationContext.getResources(path)) {
                    try (InputStream in = resource.getInputStream()) {
                        IOUtils.copy(in, out);
                    }
                }
            }

}
