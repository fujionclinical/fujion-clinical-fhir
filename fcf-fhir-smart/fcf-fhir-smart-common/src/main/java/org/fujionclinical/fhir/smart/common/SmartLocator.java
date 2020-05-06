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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.common.StrUtil;
import org.fujionclinical.shell.plugins.PluginDefinition;
import org.fujionclinical.shell.plugins.PluginRegistry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Locates SMART app manifests and registers them as Fujion plugins. Manifests must end in the
 * extension ".smart" and be a valid JSON-ized manifest format.
 */
public class SmartLocator implements ApplicationContextAware {


    private static final Log log = LogFactory.getLog(SmartLocator.class);

    private static final String SMART_MANIFEST_PATTERN = "*.smart";

    private final PluginRegistry registry;

    public SmartLocator(PluginRegistry registry) {
        super();
        this.registry = registry;
    }

    /**
     * Called by the application context, allowing the enumeration of SMART application manifests in
     * the environment.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("Searching for SMART app manifests...");
        findSmartManifests(applicationContext, "classpath*:/META-INF/");
        findSmartManifests(applicationContext, "/WEB-INF/");
    }

    /**
     * Search for SMART manifests within the specified path root. Each discovered manifest will
     * result in the creation of a Fujion plugin definition for the associated SMART app.
     *
     * @param appContext The application context.
     * @param root Root path for search.
     */
    private void findSmartManifests(ApplicationContext appContext, String root) {
        try {
            Resource[] resources = appContext.getResources(root + SMART_MANIFEST_PATTERN);

            for (Resource resource : resources) {
                PluginDefinition def = toDefinition(resource);

                if (def != null) {
                    registry.register(def);
                }
            }
        } catch (IOException e) {
            log.error("Error searching for SMART manifests.", e);
        }

    }

    /**
     * Synthesizes a plugin definition from a SMART manifest resource.
     *
     * @param resource A resource that represents a SMART manifest in JSON format.
     * @return A plugin definition.
     */
    private PluginDefinition toDefinition(Resource resource) {
        try {
            SmartManifest manifest = new SmartManifest(resource.getInputStream());

            if (!"ui".equals(manifest.getValue("mode"))) {
                return null;
            }

            PluginDefinition definition = new PluginDefinition();
            String name = manifest.getValue("client_name");
            definition.setId("smart_" + StrUtil.xlate(manifest.getValue("client_id"), " @", "__"));
            definition.setName(name);
            definition.setUrl(manifest.getValue("launch_uri"));
            definition.setClazz(SmartPlugin.class);
            definition.setDescription(manifest.getValue("description"));
            definition.setSource("SMART Platform");
            definition.setCreator(manifest.getValue("author"));
            definition.setVersion(manifest.getValue("version"));
            definition.setIcon(manifest.getValue("logo_uri"));
            definition.setCategory("SMART apps");
            definition.getResources().add(new SmartResource(manifest));
            log.info("Found SMART Manifest for " + name);
            return definition;
        } catch (Exception e) {
            log.error("Error loading SMART manifest: " + resource, e);
        }

        return null;
    }

}
