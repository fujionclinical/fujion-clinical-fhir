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

import org.apache.commons.lang3.StringUtils;
import org.fujion.annotation.Component;
import org.fujion.common.Assert;
import org.fujion.component.BaseUIComponent;
import org.fujion.component.Page;
import org.fujionclinical.fhir.smart.common.SmartContextBase.ContextMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.*;

/**
 * SMART Container Implementation
 */
@Component(
        tag = "#smart",
        widgetModule = "fcf-smart",
        widgetClass = "SmartContainer",
        parentTag = "*",
        description = "A container for a SMART app.")
public class SmartContainer extends BaseUIComponent implements ISmartContextSubscriber {

    private final SmartManifest _manifest = new SmartManifest();

    private final Set<String> _contexts = new HashSet<>();

    private final Map<String, ContextMap> _context = new HashMap<>();

    private final SmartContextRegistry contextRegistry;

    private final SmartMessageBroker messagingService;

    private boolean initialized;

    private String aboutSrc;

    private String src;

    private boolean _active;

    public SmartContainer() {
        contextRegistry = SmartContextRegistry.getInstance();
        messagingService = SmartMessageBroker.getInstance();
        messagingService.registerContainer(this);
    }

    /**
     * Sets the URL of the SMART app to be loaded.
     *
     * @param src URL of the SMART app to be loaded.
     */
    private void setSrc(String src) {
        src = nullify(src);

        if (src != null && !src.startsWith("http")) {
            String requestUrl = StringUtils.substringBeforeLast(getPage().getRequestUrl(), "/");
            src = requestUrl + "/" + src;
        }

        propertyChange("src", this.src, this.src = src, true);
    }

    @Override
    protected void _initProps(Map<String, Object> props) {
        super._initProps(props);
        props.put("wclazz", "fcf_smart_container");
    }

    @Override
    protected void onAttach(Page page) {
        super.onAttach(page);
        String about = _manifest.getValue("about");

        if (about == null) {
            about = _manifest.getValue("description");
        }

        if (about != null) {
            String aboutPath = UUID.randomUUID() + ".html";
            Resource resource = new ByteArrayResource(about.getBytes());
            aboutSrc = page.registerResource(aboutPath, resource);
            refresh();
        }
    }

    /**
     * Sets the manifest.
     *
     * @param manifest The SMART manifest.
     */
    public void setManifest(SmartManifest manifest) {
        Assert.isTrue(!initialized, "A SMART manifest has already been set");
        initialized = true;
        _manifest.init(manifest);
        String contextStr = _manifest.getValue("context");
        _contexts.add("user");

        if (contextStr != null) {
            String[] contextArray = contextStr.replaceAll("\\s", "").split(",");
            Collections.addAll(_contexts, contextArray);
        }

        subscribeAll(true);
    }

    /**
     * Returns the container's activation state.
     *
     * @return True if the container is active.
     */
    public boolean isActive() {
        return _active;
    }

    /**
     * Sets the container's activation state. The updated state is passed to the client.
     *
     * @param active The activation state.
     */
    public void setActive(boolean active) {
        _active = active;
    }

    /**
     * ISmartContextSubscriber.updateContext is called by the associated SMART context to notify
     * this container of a change to the context.
     */
    @Override
    public void updateContext(
            String contextName,
            ContextMap context) {
        if (context != null && !context.isEmpty()) {
            _context.put(contextName, context);
        } else {
            _context.remove(contextName);
        }

        refresh();
    }

    @Override
    public void destroy() {
        super.destroy();
        messagingService.unregisterContainer(this);
        subscribeAll(false);
    }

    /**
     * Refreshes the container's SMART app.
     */
    public void refresh() {
        setSrc(null);
        setSrc(contextPrepared() ? getUrl() : aboutSrc);
    }

    /**
     * Returns true only if all registered contexts are populated.
     */
    private boolean contextPrepared() {
        return _context.keySet().containsAll(_contexts);
    }

    /**
     * Returns the url for the SMART plugin.
     *
     * @return SMART plugin url.
     */
    private String getUrl() {
        return SmartContextService.getInstance().getUrl(_manifest, _context.values());
    }

    /**
     * Subscribes/unsubscribes all contexts declared within the manifest.
     *
     * @param subscribe If true, subscribe; false, unsubscribe;
     */
    private void subscribeAll(boolean subscribe) {
        for (String context : _contexts) {
            subscribe(context, subscribe);
        }
    }

    /**
     * Subscribes/unsubscribes this container to/from a SMART context.
     *
     * @param contextName The name of the SMART context.
     * @param subscribe   If true, subscribe; false, unsubscribe;
     */
    private void subscribe(
            String contextName,
            boolean subscribe) {
        for (ISmartContext context: contextRegistry.get(contextName)) {
            if (subscribe) {
                context.subscribe(this);
            } else {
                context.unsubscribe(this);
            }
        }
    }

}
