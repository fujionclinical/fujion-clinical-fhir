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
package org.fujionclinical.fhir.ui.reporting.drilldown;

import org.fujion.common.MiscUtil;
import org.fujion.component.BaseUIComponent;
import org.fujion.event.ClickEvent;
import org.fujion.event.Event;
import org.fujion.event.IEventListener;

/**
 * Creates a drilldown event listener.
 *
 * @param <T> Class of drill down data object.
 */
public class DrillDownListener<T> implements IEventListener {
    
    private final T dataObject;
    
    private final Class<?> drillDownDisplayClass;
    
    private final BaseUIComponent component;
    
    /**
     * Create a drilldown event listener for the specified component.
     *
     * @param component The component to which the event listener will be attached.
     * @param dataObject Data object for the drilldown.
     * @param drillDownDisplayClass Dialog class for the drilldown display.
     */
    public DrillDownListener(BaseUIComponent component, T dataObject, Class<?> drillDownDisplayClass) {
        super();
        this.component = component;
        this.dataObject = dataObject;
        this.drillDownDisplayClass = drillDownDisplayClass;
        component.addEventListener(ClickEvent.class, this);
        component.addStyle("cursor", "pointer");
    }
    
    @Override
    public void onEvent(Event event) {
        try {
            DrillDownUtil.showDrillDown(component, dataObject, drillDownDisplayClass);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
}
