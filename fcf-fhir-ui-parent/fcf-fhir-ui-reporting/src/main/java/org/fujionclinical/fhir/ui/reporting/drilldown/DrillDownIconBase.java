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

import org.fujion.common.StrUtil;
import org.fujion.component.Image;
import org.fujionclinical.fhir.ui.reporting.Constants;

/**
 * A clickable icon for invoking a drill down dialog on a single entity. All entity types are
 * supported by specifying a drillDownDisplayClass.
 *
 * @param <T> Class of drill down data object.
 */
public abstract class DrillDownIconBase<T> extends Image {
    
    private static final String TOOLTIP = "resultsdisplay.drilldownimage.hint";
    
    private static final String[] stockIcons = new String[] { "drilldown", "textreport", "image" };
    
    protected final T dataObject;
    
    protected final Class<?> drillDownDisplayClass;
    
    public enum StockIcons {
        standard, report, image
    };
    
    /**
     * Constructor for DrillDownLink
     *
     * @param dataObject Domain data object to interrogate
     * @param drillDownDisplayClass Declaration of the class to use to interrogate the dataObject
     */
    public DrillDownIconBase(T dataObject, Class<?> drillDownDisplayClass) {
        this(dataObject, drillDownDisplayClass, StockIcons.standard, null);
    }
    
    /**
     * @param dataObject Data Object
     * @param drillDownDisplayClass Drill down display class
     * @param stockIcon StockIcons
     * @param hint Tooltip text
     */
    public DrillDownIconBase(T dataObject, Class<?> drillDownDisplayClass, StockIcons stockIcon, String hint) {
        this(dataObject, drillDownDisplayClass, Constants.RESOURCE_PREFIX + stockIcons[stockIcon.ordinal()] + ".png",
                Constants.RESOURCE_PREFIX + stockIcons[stockIcon.ordinal()] + "2.png", hint);
    }
    
    /**
     * @param dataObject Data Object
     * @param drillDownDisplayClass Drill down display class
     * @param glyph1 Icon mouseout
     * @param glyph2 Icon mouseover
     * @param hint hint text
     */
    public DrillDownIconBase(T dataObject, Class<?> drillDownDisplayClass, String glyph1, String glyph2, String hint) {
        this.dataObject = dataObject;
        this.drillDownDisplayClass = drillDownDisplayClass;
        setSrc(glyph1);
        //TODO: setHover(glyph2 == null ? glyph1 : glyph2);
        setHint(hint != null ? hint : StrUtil.getLabel(TOOLTIP));
        addClass(Constants.SCLASS_DRILLDOWN_LINK);
        attachEventListener();
    }
    
    protected abstract void attachEventListener();
}
