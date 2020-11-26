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
package org.fujionclinical.fhir.api.common.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.narrative.CustomThymeleafNarrativeGenerator;
import ca.uhn.fhir.narrative.INarrativeGenerator;
import org.apache.commons.io.IOUtils;
import org.fujion.common.Logger;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.instance.model.api.INarrative;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * Wraps hapi-fhir's narrative generator as a service.
 */
public class NarrativeService implements ApplicationContextAware {

    private static final Logger log = Logger.create(NarrativeService.class);

    private ApplicationContext applicationContext;

    /**
     * Generate a narrative for the resource.
     *
     * @param resource    Resource for which to generate a narrative.
     * @param fhirContext The FHIR context.
     * @return The generated narrative, or null if narrative generation is not supported for this
     *         resource.
     */
    public boolean generateNarrative(
            IDomainResource resource,
            FhirContext fhirContext) {

        INarrativeGenerator generator = fhirContext.getNarrativeGenerator();

        if (generator == null) {
            generator = createNarrativeGenerator(fhirContext);
        }

        return generator.populateResourceNarrative(fhirContext, resource);
    }

    private INarrativeGenerator createNarrativeGenerator(FhirContext fhirContext) {
        synchronized (fhirContext) {
            if (fhirContext.getNarrativeGenerator() != null) {
                return fhirContext.getNarrativeGenerator();
            }

            CustomThymeleafNarrativeGenerator generator = new CustomThymeleafNarrativeGenerator();
            fhirContext.setNarrativeGenerator(generator);

            try {
                File file = File.createTempFile("fcf", ".properties");
                file.deleteOnExit();
                String fhirVersion = fhirContext.getVersion().getVersion().toString().toLowerCase().replace("dstu3", "stu3");
                String propFile = "fhir-narratives-" + fhirVersion + ".properties";
                boolean found = false;

                try (FileOutputStream out = new FileOutputStream(file)) {
                    found |= findPropertyFiles(applicationContext, "classpath*:META-INF/" + propFile, out);
                    found |= findPropertyFiles(applicationContext, "classpath*:WEB-INF/" + propFile, out);
                }

                generator.setPropertyFile("file:" + file.getAbsolutePath());

                if (!found) {
                    log.warn("No FHIR narrative templates found.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return generator;
        }
    }

    /**
     * Returns a narrative from the resource, if one is available, or constructs one if not.
     *
     * @param resource    Resource whose narrative is sought.
     * @param fhirContext The FHIR context.
     * @param autoCreate  If true, and a narrative does not exist on the resource, generate one if
     *                    possible.
     * @return The narrative, or null if one is not available.
     */
    public INarrative extractNarrative(
            FhirContext fhirContext,
            IDomainResource resource,
            boolean autoCreate) {
        INarrative narrative = extractNarrative(resource);

        if (autoCreate && isNarrativeEmpty(narrative)) {
            narrative = generateNarrative(resource, fhirContext) ? extractNarrative(resource) : null;
        }

        return isNarrativeEmpty(narrative) ? null : narrative;
    }

    public INarrative extractNarrative(IDomainResource resource) {
        return resource.getText();
    }

    /**
     * Returns true if the narrative is null or effectively empty.
     *
     * @param narrative The narrative.
     * @return True if the narrative is null or effectively empty.
     */
    public boolean isNarrativeEmpty(INarrative narrative) {
        return narrative == null || narrative.isEmpty();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private boolean findPropertyFiles(
            ApplicationContext applicationContext,
            String path,
            OutputStream out) throws IOException {
        boolean found = false;

        for (Resource resource : applicationContext.getResources(path)) {
            log.info("Found FHIR narrative template at " + resource);
            found = true;

            try (InputStream in = resource.getInputStream()) {
                IOUtils.copy(in, out);
            }
        }

        return found;
    }

}
