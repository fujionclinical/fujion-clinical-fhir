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
package org.fujionclinical.fhir.plugin.scenario.common;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.BaseDateTimeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.parser.IParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.fujion.common.DateUtil;
import org.fujion.common.Logger;
import org.fujion.common.MiscUtil;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class ScenarioBase {

    private static final Logger log = Logger.create(ScenarioBase.class);

    private final Map<String, Map<String, String>> scenarioConfig;

    private final String scenarioName;

    private final Resource scenarioBase;

    private final IBaseCoding scenarioTag;

    private final Map<String, IBaseResource> resourcesById = new HashMap<>();

    private final Map<String, IBaseResource> resourcesByName = new HashMap<>();
    
    private final FhirContext fhirContext;

    private boolean isLoaded;

    protected ScenarioBase(Resource scenarioYaml, FhirContext fhirContext) {
        this.fhirContext = fhirContext;
        this.scenarioName = FilenameUtils.getBaseName(scenarioYaml.getFilename());
        this.scenarioBase = scenarioYaml;
        this.scenarioTag = ScenarioUtil.createScenarioTag(scenarioName);

        try (InputStream in = scenarioYaml.getInputStream()) {
            scenarioConfig = new Yaml().load(in);
        } catch (Exception e) {
            log.error("Failed to load scenario: " + scenarioName, e);
            throw MiscUtil.toUnchecked(e);
        }

        log.info(() -> "Loaded demo scenario: " + scenarioName);
    }

    /**
     * Return the name of this scenario.
     *
     * @return The scenario name.
     */
    public final String getName() {
        return scenarioName;
    }

    /**
     * Return the tag used to mark a resource as belonging to this scenario.
     *
     * @return The scenario tag.
     */
    public final IBaseCoding getTag() {
        return scenarioTag;
    }

    /**
     * Adds the general demo tag and a scenario tag to the resource.
     *
     * @param resource Resource to be tagged.
     */
    public final void addTags(IBaseResource resource) {
        ScenarioUtil.addDemoTag(resource);
        FhirUtil.addTag(scenarioTag, resource);
    }

    /**
     * Returns a read-only list of loaded resources.
     *
     * @return List of loaded resources.
     */
    public final Collection<IBaseResource> getResources() {
        return Collections.unmodifiableCollection(resourcesById.values());
    }

    /**
     * Returns a count of loaded resources.
     *
     * @return Count of loaded resources.
     */
    public final int getResourceCount() {
        return resourcesById.size();
    }

    public final boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Initialize the scenario. Any existing resources belonging to the scenario will first be
     * deleted. Then creates all resources as defined in the scenario configuration.
     *
     * @return Count of resources in scenario.
     */
    public final synchronized int initialize() {
        destroy();

        for (String name : scenarioConfig.keySet()) {
            Map<String, String> params = scenarioConfig.get(name);
            IBaseResource resource = initialize(params.get("source"), params);
            resourcesByName.put(name, resource);
        }

        return resourcesById.size();
    }

    /**
     * Creates a resource based on config data.
     *
     * @param source The source file for the resource to be created.
     * @param params Optional parameters to use to resolve placeholders.
     * @return The created resource.
     */
    public final synchronized IBaseResource initialize(String source, Map<String, String> params) {
        Assert.notNull(source, "A source must be specified.");
        return initialize(parseResource(source, params));
    }

    /**
     * Tags and creates a resource.
     *
     * @param resource The resource to create
     * @return The created resource.
     */
    public final synchronized IBaseResource initialize(IBaseResource resource) {
        resource = createOrUpdateResource(resource);
        logAction(resource, "Created");
        return resource;
    }

    /**
     * Load all resources for this scenario.
     *
     * @return Count of resources loaded for this scenario.
     */
    @SuppressWarnings("unchecked")
    public final synchronized int load() {
        isLoaded = true;
        resourcesById.clear();

        for (Class<? extends IBaseResource> clazz : _getResourceClasses()) {
            try {
                for (IBaseResource resource : _searchResourcesByTag(scenarioTag, (Class<IBaseResource>) clazz)) {
                    addResource(resource);
                    logAction(resource, "Retrieved");
                }
            } catch (Exception e) {
                log.warn("Search by tag error on resource " + clazz.getName());
            }
        }

        return resourcesById.size();
    }

    protected abstract List<? extends IBaseResource> _searchResourcesByTag(IBaseCoding tag, Class<? extends IBaseResource> clazz);
    
    protected abstract Set<Class<? extends IBaseResource>> _getResourceClasses();
    
    /**
     * Destroy all resources belonging to this scenario.
     *
     * @return The number of resources successfully deleted.
     */
    public final synchronized int destroy() {
        load();
        int count = 0;
        boolean deleted = true;

        while (deleted) {
            deleted = false;
            Iterator<IBaseResource> iterator = resourcesById.values().iterator();

            while (iterator.hasNext()) {
                IBaseResource resource = iterator.next();

                try {
                    _deleteResource(resource);
                    resourcesByName.values().remove(resource);
                    iterator.remove();
                    deleted = true;
                    count++;
                    logAction(resource, "Deleted");
                } catch (Exception e) {}
            }
        }

        for (IBaseResource resource : resourcesById.values()) {
            logAction(resource, "Failed to delete");
        }

        resourcesByName.clear();
        return count;
    }

    protected abstract void _deleteResource(IBaseResource resource);
    
    /**
     * Creates or updates the specified resource, first tagging it as belonging to this scenario.
     *
     * @param resource The resource to create or update.
     * @return The resource, possibly modified.
     */
    public final IBaseResource createOrUpdateResource(IBaseResource resource) {
        if (resource instanceof IBaseBundle) {
            List<IBaseResource> resources = _getEntries((IBaseBundle) resource);

            for (IBaseResource res : resources) {
                createOrUpdateResource(res);
            }

            return resource;
        }

        addTags(resource);
        resource = _createOrUpdateResource(resource);
        addResource(resource);
        return resource;
    }

    protected abstract List<IBaseResource> _getEntries(IBaseBundle bundle);
    
    protected abstract IBaseResource _createOrUpdateResource(IBaseResource resource);
    
    /**
     * Adds a resource to the list of resources for this scenario.
     *
     * @param resource Scenario to add.
     */
    public final synchronized void addResource(IBaseResource resource) {
        resourcesById.put(resource.getIdElement().getValue(), resource);
    }

    private InputStream getResourceAsStream(String name) {
        try {
            return scenarioBase.createRelative(name).getInputStream();
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    private void logAction(IBaseResource resource, String operation) {
        FhirUtil.stripVersion(resource);
        log.info(operation + " resource: " + resource.getIdElement().getValue());
    }

    public final IBaseResource parseResource(String source, Map<String, String> params) {
        source = addExtension(source, "json");
        IParser parser = source.endsWith(".xml") ? fhirContext.newXmlParser() : fhirContext.newJsonParser();
        StringBuilder sb = new StringBuilder();

        try (InputStream is = getResourceAsStream(source)) {
            List<String> json = IOUtils.readLines(is, "UTF-8");

            for (String s : json) {
                int p1;

                while ((p1 = s.indexOf("${")) > -1) {
                    int p2 = s.indexOf("}", p1);
                    String key = s.substring(p1 + 2, p2);
                    int p3 = key.indexOf(":");
                    String dflt = "";

                    if (p3 > 0) {
                        dflt = key.substring(p3 + 1);
                        key = key.substring(0, p3);
                    }

                    String value = params.get(key);

                    if (value == null && !dflt.isEmpty()) {
                        value = dflt;
                    }

                    if (value == null) {
                        throw new RuntimeException("Reference not found: " + key);
                    }

                    String r = eval(value, resourcesByName);
                    s = s.substring(0, p1) + r + s.substring(p2 + 1);
                }

                sb.append(s).append('\n');
            }
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }

        return parser.parseResource(sb.toString());
    }

    /**
     * Add default extension if one is not present.
     *
     * @param source File resource path.
     * @param dflt The default extension.
     * @return File resource path with extension.
     */
    private String addExtension(String source, String dflt) {
        return source.contains(".") ? source : source + "." + dflt;
    }

    /**
     * Evaluate an expression.
     *
     * @param exp The expression. The general format is
     *            <p>
     *            <code>type/value</code>
     *            </p>
     *            If <code>type</code> is omitted, it is assumed to be a placeholder for a resource
     *            previously defined. Possible values for <code>type</code> are:
     *            <ul>
     *            <li>value - A literal value; inserted as is</li>
     *            <li>date - A date value; can be a relative date (T+n, for example)</li>
     *            <li>image - A file containing an image</li>
     *            <li>snippet - A file containing a snippet to be inserted</li>
     *            </ul>
     * @param resourceMap Map of resolved resources.
     * @return The result of the evaluation.
     */
    private String eval(String exp, Map<String, IBaseResource> resourceMap) {
        int i = exp.indexOf('/');

        if (i == -1) {
            IBaseResource resource = resourceMap.get(exp);
            Assert.notNull(resource, () -> "Resource not defined: " + exp);
            return resource.getIdElement().getResourceType() + "/" + resource.getIdElement().getIdPart();
        }

        String type = exp.substring(0, i);
        String value = exp.substring(i + 1);

        if ("value".equals(type)) {
            return value;
        }

        if ("date".equals(type)) {
            return doDate(value, true);
        }

        if ("datetime".equals(type)) {
            return doDate(value, false);
        }

        if ("image".equals(type)) {
            return doBinary(exp);
        }

        if ("snippet".equals(type)) {
            return doSnippet(exp);
        }

        throw new RuntimeException("Unknown type: " + type);
    }

    private String doBinary(String value) {
        try (InputStream is = getResourceAsStream(value)) {
            return Base64.encodeBase64String(IOUtils.toByteArray(is));
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    private String doSnippet(String value) {
        value = addExtension(value, "json");

        try (InputStream is = getResourceAsStream(value)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    private String doDate(final String value, boolean dateOnly) {
        Date date = DateUtil.parseDate(value);
        Assert.notNull(date, () -> "Bad date specification: " + value);
        BaseDateTimeDt dtt = dateOnly ? new DateDt(date) : new DateTimeDt(date);
        return dtt.getValueAsString();
    }

}