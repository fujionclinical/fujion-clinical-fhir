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
package org.fujionclinical.fhir.dstu2.ui.patientselection;

import org.fujion.common.StrUtil;
import org.fujionclinical.api.property.PropertyUtil;
import org.fujionclinical.api.spring.SpringUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Class for managing boolean system and user preferences affecting patient selection.
 */
public class Features {
    
    /**
     * Used to allow EL expression to directly access property settings.
     */
    private class FeatureMap implements Map<String, Boolean> {
        
        @Override
        public Collection<Boolean> values() {
            return null;
        }
        
        @Override
        public Boolean put(String key, Boolean value) {
            return null;
        }
        
        @Override
        public Set<String> keySet() {
            return null;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public int size() {
            return 0;
        }
        
        @Override
        public void putAll(Map<? extends String, ? extends Boolean> t) {
        };
        
        @Override
        public void clear() {
        }
        
        @Override
        public boolean containsValue(Object value) {
            return false;
        }
        
        @Override
        public Boolean remove(Object key) {
            return null;
        }
        
        @Override
        public boolean containsKey(Object key) {
            return false;
        }
        
        @Override
        public Set<java.util.Map.Entry<String, Boolean>> entrySet() {
            return null;
        }
        
        @Override
        public Boolean get(Object obj) {
            return obj instanceof String ? isEnabled((String) obj) : null;
        }
    }
    
    private final String propertyPrefix;
    
    private final FeatureMap featureMap = new FeatureMap();
    
    /**
     * Return the user's feature settings.
     * 
     * @return User's feature settings.
     */
    public static Features getInstance() {
        return SpringUtil.getBean("patientSelectionFeatures", Features.class);
    }
    
    public Features(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    };
    
    /**
     * Returns true if the specified feature is enabled.
     * 
     * @param featureName Name of the feature. This maps to the property that is prefixed by the
     *            current property prefix.
     * @return True if the feature is enabled.
     */
    public boolean isEnabled(String featureName) {
        return isEnabled(featureName, true);
    }
    
    /**
     * Returns true if the specified feature is enabled.
     * 
     * @param featureName Name of the feature. This maps to the property that is prefixed by the
     *            current property prefix.
     * @param deflt The default value if the feature state is not found.
     * @return True if the feature is enabled.
     */
    public boolean isEnabled(String featureName, boolean deflt) {
        String value = getValue(featureName, "");
        return value.isEmpty() ? deflt : StrUtil.toBoolean(value);
    }
    
    /**
     * Returns the value of the specified feature.
     * 
     * @param featureName Name of the feature. This maps to the property that is prefixed by the
     *            current property prefix.
     * @param deflt The default value if the feature state is not found.
     * @return The feature's value.
     */
    public String getValue(String featureName, String deflt) {
        String value = PropertyUtil.getValue(propertyPrefix + featureName.toUpperCase(), null);
        return value == null ? deflt : value;
    }
    
    /**
     * Returns an instance of the feature map used in EL to determine if a feature should be
     * enabled.
     * 
     * @return The feature map.
     */
    public FeatureMap getFeatureMap() {
        return featureMap;
    }
}
