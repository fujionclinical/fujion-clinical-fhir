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
package org.fujionclinical.fhir.r4.api.common;

import org.fujionclinical.api.context.ISerializer;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.StringType;

import java.util.List;

/**
 * CCOW serializer / deserializer for HumanName class.
 */
public class NameSerializer implements ISerializer<HumanName> {
    
    private static final String NAME_DELIM = "^";
    
    @Override
    public String serialize(HumanName value) {
        return value.getFamily() + NAME_DELIM + getComponent(value.getGiven(), 0) + NAME_DELIM
                + getComponent(value.getGiven(), 1) + NAME_DELIM + getComponent(value.getSuffix(), 0) + NAME_DELIM
                + getComponent(value.getPrefix(), 0) + NAME_DELIM + getComponent(value.getSuffix(), 1) + NAME_DELIM
                + value.getUse().toCode();
    }
    
    private String getComponent(List<StringType> list, int index) {
        if (index >= list.size()) {
            return "";
        }
        
        StringType value = list.get(index);
        String result = value.getValue();
        return result == null ? "" : result;
    }
    
    @Override
    public HumanName deserialize(String value) {
        String pcs[] = value.split("\\" + NAME_DELIM);
        HumanName result = new HumanName();
        int i = 0;
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.setFamily(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addGiven(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addGiven(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addSuffix(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addPrefix(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addSuffix(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.getUseElement().setValueAsString(value.toLowerCase());
        }
        
        return result;
    }
    
    private String getComponent(String[] pcs, int index) {
        if (index >= pcs.length) {
            return null;
        }
        
        String result = pcs[index];
        return result == null || result.isEmpty() ? null : result;
    }
    
    @Override
    public Class<HumanName> getType() {
        return HumanName.class;
    }
}
