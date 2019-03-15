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
package org.fujionclinical.fhir.common.smart;

import org.fujion.component.Iframe;
import org.fujionclinical.api.spring.SpringUtil;
import org.fujionclinical.fhir.common.smart.SmartContextBase.ContextMap;

import java.util.HashMap;
import java.util.Map;

/**
 * SMART Container Implementation
 */
public class SmartContainer extends Iframe implements ISmartContextSubscriber {

    protected final SmartManifest _manifest = new SmartManifest();

    protected final Map<String, ContextMap> _context = new HashMap<>();

    private final SmartContextRegistry contextRegistry;

    private boolean _active;

    public SmartContainer() {
        this.addStyle("background", "lightgray");
        contextRegistry = SpringUtil.getBean("smartContextRegistry", SmartContextRegistry.class);
    }

    /**
     * Sets the manifest.
     *
     * @param manifest The SMART manifest.
     */
    public void setManifest(SmartManifest manifest) {
        _manifest.init(manifest);
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
    public void updateContext(String contextScope, ContextMap context) {
        _context.remove(contextScope);

        if (context != null && !context.isEmpty()) {
            _context.put(contextScope, context);
        }

        refresh();
    }

    @Override
    public void destroy() {
        super.destroy();
        subscribeAll(false);
    }

    public void refresh() {
        setSrc(null);
        setSrc(getUrl());
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
     * Subscribes/unsubscribes all scopes declared within the manifest.
     *
     * @param subscribe If true, subscribe; false, unsubscribe;
     */
    private void subscribeAll(boolean subscribe) {
        String scopes = _manifest == null ? null : _manifest.getValue("scope");

        if (scopes != null) {
            for (String scope : scopes.split("\\,")) {
                subscribe(scope.trim(), subscribe);
            }
        }
    }

    /**
     * Subscribes/unsubscribes this subscriber to/from a SMART context scope.
     *
     * @param contextScope The name of the SMART context scope.
     * @param subscribe If true, subscribe; false, unsubscribe;
     */
    private void subscribe(String contextScope, boolean subscribe) {
        if (subscribe) {
            contextRegistry.get(contextScope).subscribe(this);
        } else {
            contextRegistry.get(contextScope).unsubscribe(this);
        }
    }

}
