/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2019 fujionclinical.org
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
package org.fujionclinical.fhir.dstu2.api.location;

import ca.uhn.fhir.model.dstu2.resource.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujionclinical.api.context.ContextItems;
import org.fujionclinical.api.context.ContextManager;
import org.fujionclinical.api.context.IContextEvent;
import org.fujionclinical.api.property.PropertyUtil;
import org.fujionclinical.fhir.dstu2.api.common.ResourceContext;

/**
 * Wrapper for shared user location context.
 */
public class LocationContext extends ResourceContext<Location> {


    private static final Log log = LogFactory.getLog(LocationContext.class);

    private static final String SUBJECT_NAME = "Location";

    private static final String PROPERTY_DEFAULT_LOCATION = "FCF LOCATION DEFAULT";

    public interface ILocationContextEvent extends IContextEvent {
    }

    /**
     * Returns the managed location context.
     *
     * @return Location context.
     */
    static public LocationContext getLocationContext() {
        return (LocationContext) ContextManager.getInstance().getSharedContext(LocationContext.class.getName());
    }

    /**
     * Returns the current location from the shared context.
     *
     * @return Current location.
     */
    public static Location getActiveLocation() {
        return getLocationContext().getContextObject(false);
    }

    /**
     * Requests a context change to the specified location.
     *
     * @param location The location.
     */
    public static void changeLocation(Location location) {
        try {
            getLocationContext().requestContextChange(location);
            setDefaultLocation(location);
        } catch (Exception e) {
            log.error("Error during request context change.", e);
        }
    }

    /**
     * Request a location context change.
     *
     * @param logicalId Logical id of the location.
     */
    public static void changeLocation(String logicalId) {
        getLocationContext().requestContextChange(logicalId);
    }

    /**
     * Returns the default location if any. This value is stored in the LOCATION.DEFAULT property.
     *
     * @return Default location or null if no default is available.
     */
    public static Location getDefaultLocation() {
        return null; // TODO
    }

    /**
     * Sets the default location to the specified value. This value is stored in the
     * LOCATION.DEFAULT property.
     *
     * @param location The location.
     */
    public static void setDefaultLocation(Location location) {
        PropertyUtil.saveValue(PROPERTY_DEFAULT_LOCATION, null, false, location.getIdElement().getIdPart());
    }

    /**
     * Creates the context wrapper and registers its context change callback interface.
     */
    public LocationContext() {
        this(getDefaultLocation());
    }

    /**
     * /** Creates the context wrapper and registers its context change callback interface.
     *
     * @param location Initial value for this context.
     */
    public LocationContext(Location location) {
        super(SUBJECT_NAME, Location.class, ILocationContextEvent.class, location);
    }

    /**
     * Commits or rejects the pending context change.
     *
     * @param accept If true, the pending change is committed. If false, the pending change is
     *               canceled.
     */
    @Override
    public void commit(boolean accept) {
        super.commit(accept);
    }

    /**
     * Creates a CCOW context from the specified location object.
     */
    @Override
    protected ContextItems toCCOWContext(Location location) {
        //TODO: contextItems.setItem(...);
        return contextItems;
    }

    /**
     * Returns a list of patient objects based on the specified CCOW context.
     */
    @Override
    protected Location fromCCOWContext(ContextItems contextItems) {
        Location location = null;

        try {
            location = new Location();
            //TODO: populate location object from context items
            return location;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Returns a priority value of 5.
     *
     * @return Priority value for context manager.
     */
    @Override
    public int getPriority() {
        return 5;
    }

}
