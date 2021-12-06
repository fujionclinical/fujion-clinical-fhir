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
package org.fujionclinical.fhir.scenario.common;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.BaseDateTimeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.parser.IParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.coolmodel.clinical.encounter.Encounter;
import org.coolmodel.foundation.core.Identifiable;
import org.coolmodel.foundation.entity.Person;
import org.coolmodel.mediator.fhir.common.FhirUtils;
import org.fujion.common.Assert;
import org.fujion.common.DateUtil;
import org.fujion.common.Logger;
import org.fujion.common.MiscUtil;
import org.fujionclinical.api.cool.encounter.EncounterContext;
import org.fujionclinical.api.cool.patient.PatientContext;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.patientlist.*;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseCoding;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * Abstract base class for FHIR-specific scenarios.
 *
 * @param <LIST> The List resource class.
 */
public abstract class ScenarioBase<LIST extends IBaseResource> {

    private static final Logger log = Logger.create(ScenarioBase.class);

    private final Map<String, Map<String, String>> scenarioConfig;

    private final String scenarioName;

    private final String activationResource;

    private final IBaseCoding scenarioTag;

    private final IIdType scenarioId;

    private final List<IBaseResource> resources = new ArrayList<>();

    private final FhirContext fhirContext;

    private final Resource root;

    private final IPatientList patientList;

    private final IPatientListFilterManager patientListFilterManager;

    private final String patientListFilterName;

    private boolean isLoaded;

    private LIST scenarioResources;

    protected ScenarioBase(ScenarioFactory<?> scenarioFactory) {
        this.fhirContext = scenarioFactory.dataSource.getClient().getFhirContext();
        this.scenarioName = scenarioFactory.scenarioName;
        this.scenarioTag = scenarioFactory.scenarioTag;
        this.scenarioId = scenarioFactory.scenarioId;
        this.scenarioConfig = scenarioFactory.scenarioConfig;
        this.activationResource = scenarioFactory.activationResource;
        this.root = scenarioFactory.scenarioYaml;
        this.patientList = PatientListRegistry.getInstance().findByName("Personal Lists");
        this.patientListFilterManager = this.patientList.getFilterManager();
        this.patientListFilterName = "scenario: " + getName();
    }

    /**
     * If the resource is a Patient resource, return a wrapped instance.  Otherwise, return null.
     *
     * @param resource The resource to check.
     * @return A wrapped instance of the resource, or null if it is not a Patient resource.
     */
    protected abstract Person _toPatient(IBaseResource resource);

    /**
     * Loads resources associated with the scenario.
     *
     * @param resources Consumer function for receiving resources.
     * @return The newly created List resource.
     */
    protected abstract LIST _loadResources(Consumer<IBaseResource> resources);

    /**
     * Packages scenario resources into a List resource.
     *
     * @param resources List of resources to package.
     * @return The newly created List resource.
     */
    protected abstract LIST _packageResources(Collection<IBaseResource> resources);

    /**
     * Deletes a resource.
     *
     * @param resource The resource to delete.
     */
    protected abstract void _deleteResource(IBaseResource resource);

    /**
     * Returns resources related to the reference resource (using $everything operation).
     *
     * @param resource The reference resource.
     * @param <T> The resource type.
     * @return All resources related to the reference resource.
     */
    protected abstract <T extends IBaseResource> List<T> _relatedResources(T resource);

    /**
     * Extracts entries from a bundle resource.
     *
     * @param bundle The bundle resource.
     * @param <T> The type of bundled resource.
     * @return List of resources extracted from the bundle resource.
     */
    protected abstract <T extends IBaseResource> List<T> _getEntries(IBaseBundle bundle);

    /**
     * Creates or updates the given resource.
     *
     * @param resource The resource.
     * @return The new or updated resource.
     */
    protected abstract IBaseResource _createOrUpdateResource(IBaseResource resource);

    /**
     * Converts a FHIR resource to a domain object.
     *
     * @param resource The FHIR resource.
     * @return The corresponding domain object (possibly null).
     */
    protected abstract Identifiable _toDomainObject(IBaseResource resource);

    /**
     * Called when the scenario is activated into the current context.
     */
    public final void activate() {
        IBaseResource resource = getNamedResource(activationResource);
        Identifiable target = resource == null ? null : _toDomainObject(resource);

        if (target instanceof Encounter) {
            EncounterContext.changeEncounter((Encounter) target);
        } else if (target instanceof Person) {
            PatientContext.changePatient((Person) target);
        }
    }

    /**
     * Returns the name of this scenario.
     *
     * @return The scenario name.
     */
    public final String getName() {
        return scenarioName;
    }

    /**
     * Returns the id for the list resource used to store scenario resources.
     *
     * @return The id for the list resource used to store scenario resources.
     */
    public final IIdType getId() {
        return scenarioId;
    }

    /**
     * Adds the general demo tag and a scenario tag to the resource.
     *
     * @param resource Resource to be tagged.
     */
    private void addTags(IBaseResource resource) {
        ScenarioUtil.addTag(resource);
        FhirUtil.addTag(scenarioTag, resource);
    }

    private IBaseResource getNamedResource(String name) {
        IBaseCoding tag = name == null ? null : ScenarioUtil.createNamedResourceTag(name);
        return tag == null ? null : resources.stream()
                .filter(resource -> FhirUtil.hasTag(tag, resource))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a read-only list of loaded resources.
     *
     * @return List of loaded resources.
     */
    public final Collection<IBaseResource> getResources() {
        return Collections.unmodifiableCollection(resources);
    }

    /**
     * Returns a count of loaded resources.
     *
     * @return Count of loaded resources.
     */
    public final int getResourceCount() {
        return resources.size();
    }

    /**
     * Returns true if the scenario has been loaded.
     *
     * @return True if the scenario has been loaded.
     */
    public final boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Initialize the scenario. Any existing resources belonging to the scenario will first be
     * deleted. Then creates all resources as defined in the scenario configuration.
     *
     * @return Count of resources in scenario.
     */
    public final int initialize() {
        destroy();

        for (String name : scenarioConfig.keySet()) {
            Map<String, String> params = scenarioConfig.get(name);
            String source = ScenarioUtil.getParam(params, "source");
            IBaseResource resource = parseResource(source, params);
            initialize(name, resource);
        }

        scenarioResources = _packageResources(resources);
        scenarioResources.setId(getId());
        addTags(scenarioResources);
        _createOrUpdateResource(scenarioResources);
        return resources.size();
    }

    /**
     * Tags and creates a resource.
     *
     * @param name     The unique name associated with the resource.
     * @param resource The resource to create
     */
    private void initialize(
            String name,
            IBaseResource resource) {
        FhirUtil.addTag(ScenarioUtil.createNamedResourceTag(name), resource);
        resource = createOrUpdateResource(resource);
        logAction(resource, "Created");
    }

    /**
     * Load all resources for this scenario.
     *
     * @return Count of resources loaded for this scenario.
     */
    public final int load() {
        isLoaded = true;
        resources.clear();
        deletePatientListFilter();
        scenarioResources = _loadResources(this::addResource);
        return resources.size();
    }

    /**
     * Destroy all resources belonging to this scenario.
     *
     * @return The number of resources successfully deleted.
     */
    public final int destroy() {
        load();
        int count = 0;
        boolean stop = false;

        while (!stop) {
            stop = true;
            Iterator<IBaseResource> iterator = resources.iterator();

            while (iterator.hasNext()) {
                IBaseResource resource = iterator.next();
                int deleted = deleteResources(resource);

                if (deleted > 0) {
                    count++;
                    stop = false;
                    iterator.remove();
                    logAction(resource, "Deleted");
                }
            }
        }

        if (scenarioResources != null) {
            deleteResource(scenarioResources);
            scenarioResources = null;
        }

        for (IBaseResource resource : resources) {
            logAction(resource, "Failed to delete");
        }

        return count;
    }

    /**
     * Deletes a resource and any related resources, returning a count of those successfully deleted.
     *
     * @param resource Resource to delete.
     * @return Count of resources actually deleted.
     */
    private int deleteResources(IBaseResource resource) {
        int count = 0;

        for (IBaseResource res : relatedResources(resource)) {
            if (deleteResource(res)) {
                count++;
            }
        }

        return count;
    }

    private boolean deleteResource(IBaseResource resource) {
        try {
            _deleteResource(resource);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns a list of all resources related to the reference resource.
     *
     * @param resource The reference resource.
     * @return A list of all resources related to the reference resource.
     */
    private <T extends IBaseResource> List<T> relatedResources(T resource) {
        List<T> resources;

        try {
            resources = _relatedResources(resource);
        } catch (Exception e) {
            resources = new ArrayList<>();
        }

        resources.add(resource);
        return resources;
    }

    /**
     * Creates or updates the specified resource, first tagging it as belonging to this scenario.
     *
     * @param resource The resource to create or update.
     * @return The resource, possibly modified.
     */
    private IBaseResource createOrUpdateResource(IBaseResource resource) {
        if (resource instanceof IBaseBundle) {
            List<? extends IBaseResource> resources = _getEntries((IBaseBundle) resource);

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

    /**
     * Adds a resource to the list of resources for this scenario.
     *
     * @param resource Scenario to add.
     */
    private void addResource(IBaseResource resource) {
        resources.add(resource);
        addToPatientList(resource);
    }

    private InputStream getResourceAsStream(String name) {
        try {
            return root.createRelative(name).getInputStream();
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    private IBaseResource parseResource(
            String source,
            Map<String, String> params) {
        source = addExtension(source);
        IParser parser = source.endsWith(".xml") ? fhirContext.newXmlParser() : fhirContext.newJsonParser();
        StringBuilder sb = new StringBuilder();

        try (InputStream is = getResourceAsStream(source)) {
            List<String> json = IOUtils.readLines(is, "UTF-8");

            for (String s : json) {
                int p1;

                while ((p1 = s.indexOf("${")) > -1) {
                    int p2 = s.indexOf("}", p1);
                    Assert.isTrue(p2 > -1, "No closing bracket for placeholder");
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

                    Assert.notNull(value, "Reference not found: " + key);
                    String r = eval(value);
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
     * @return File resource path with extension.
     */
    private String addExtension(String source) {
        return source.contains(".") ? source : source + ".json";
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
     *            <li>date - A date value; can be a relative date (LIST+n, for example)</li>
     *            <li>image - A file containing an image</li>
     *            <li>snippet - A file containing a snippet to be inserted</li>
     *            </ul>
     * @return The result of the evaluation.
     */
    private String eval(String exp) {
        int i = exp.indexOf('/');

        if (i == -1) {
            IBaseResource resource = getNamedResource(exp);
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
        value = addExtension(value);

        try (InputStream is = getResourceAsStream(value)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }

    private String doDate(
            final String value,
            boolean dateOnly) {
        Date date = DateUtil.parseDate(value);
        Assert.notNull(date, () -> "Bad date specification: " + value);
        BaseDateTimeDt dtt = dateOnly ? new DateDt(date) : new DateTimeDt(date);
        return dtt.getValueAsString();
    }

    private void activatePatientListFilter() {
        IPatientListFilter filter = patientListFilterManager.getFilterByName(patientListFilterName);

        if (filter == null) {
            filter = patientListFilterManager.addFilter(patientListFilterName);
        }

        patientList.setActiveFilter(filter);
    }

    private void deletePatientListFilter() {
        IPatientListFilter filter = patientListFilterManager.getFilterByName(patientListFilterName);

        if (filter != null) {
            patientListFilterManager.removeFilter(filter);
        }
    }

    private void addToPatientList(IBaseResource resource) {
        Person patient = _toPatient(resource);

        if (patient != null) {
            activatePatientListFilter();
            IPatientListItem item = PatientListUtil.findListItem(patient, patientList.getListItems());

            if (item == null) {
                item = new PatientListItem(patient);
                patientList.getItemManager().addItem(item);
                patientList.getItemManager().save();
            }
        }
    }

    private void logAction(
            IBaseResource resource,
            String operation) {
        FhirUtils.stripVersion(resource);
        log.info(operation + " resource: " + resource.getIdElement().getValue());
    }

}
