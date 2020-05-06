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

import org.fujionclinical.shell.elements.ElementBase;
import org.fujionclinical.shell.elements.ElementPlugin;
import org.fujionclinical.shell.plugins.PluginDefinition;

/**
 * This class is used for all SMART plugins to wrap a SMART container as a framework UI element.
 */
public class SmartPlugin extends ElementPlugin {
    
    static {
        ElementBase.registerAllowedParentClass(SmartPlugin.class, ElementBase.class);
    }
    
    private final SmartContainer smartContainer = new SmartContainer();
    
    /**
     * Sets the container as the wrapped component and registers itself to receive action
     * notifications from the container.
     */
    public SmartPlugin() {
        super();
        getOuterComponent().addStyle("overflow", "hidden");
    }
    
    /**
     * Also passes the associated SMART manifest to the container.
     *
     * @see org.fujionclinical.shell.elements.ElementBase#setDefinition(org.fujionclinical.shell.plugins.PluginDefinition)
     */
    @Override
    public void setDefinition(PluginDefinition definition) {
        super.setDefinition(definition);
        SmartManifest manifest = definition.getResources(SmartResource.class).get(0).getManifest();
        fullSize(smartContainer);
        smartContainer.setParent(this.getOuterComponent());
        smartContainer.setManifest(manifest);
    }
    
    /**
     * Returns the SMART container wrapped by this UI element.
     *
     * @return The SMART container.
     */
    public SmartContainer getSmartContainer() {
        return smartContainer;
    }
    
    /**
     * Passes the activation request to the container.
     *
     * @see org.fujionclinical.shell.elements.ElementUI#activateChildren(boolean)
     */
    @Override
    public void activateChildren(boolean active) {
        super.activateChildren(active);
        smartContainer.setActive(active);
    }
    
    /**
     * Passes the destroy event to the container.
     *
     * @see org.fujionclinical.shell.elements.ElementBase#destroy()
     */
    @Override
    public void destroy() {
        smartContainer.destroy();
        super.destroy();
    }
    
}
