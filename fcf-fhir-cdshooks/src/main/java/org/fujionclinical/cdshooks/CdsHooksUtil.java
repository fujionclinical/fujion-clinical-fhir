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
package org.fujionclinical.cdshooks;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.google.gson.*;
import org.fujion.common.Assert;
import org.opencds.hooks.lib.json.JsonUtil;
import org.opencds.hooks.model.dstu2.util.Dstu2JsonUtil;
import org.opencds.hooks.model.r4.util.R4JsonUtil;
import org.opencds.hooks.model.r5.util.R5JsonUtil;
import org.opencds.hooks.model.response.Indicator;
import org.opencds.hooks.model.stu3.util.Stu3JsonUtil;

import java.lang.reflect.Type;

/**
 * Static utility methods.
 */
public class CdsHooksUtil {

    private static class IndicatorDeserializer implements JsonDeserializer<Indicator> {

        @Override
        public Indicator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String value = json.getAsString();
            return Indicator.resolve(value);
        }
    }

    public static final Gson GSON;
    
    public static final JsonUtil JSON_DSTU2 = new Dstu2JsonUtil();
    
    public static final JsonUtil JSON_STU3 = new Stu3JsonUtil();
    
    public static final JsonUtil JSON_R4 = new R4JsonUtil();

    public static final JsonUtil JSON_R5 = new R5JsonUtil();

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Indicator.class, new IndicatorDeserializer());
        GSON = builder.create();
    }

    public static JsonUtil getJsonUtil(IGenericClient fhirClient) {
        return getJsonUtil(fhirClient.getFhirContext());
    }

    public static JsonUtil getJsonUtil(FhirContext fhirContext) {
        return getJsonUtil(fhirContext.getVersion().getVersion());
    }

    public static JsonUtil getJsonUtil(FhirVersionEnum version) {
        switch (version) {
            case DSTU2:
            case DSTU2_HL7ORG:
                return JSON_DSTU2;
                
            case DSTU3:
                return JSON_STU3;
                
            case R4:
                return JSON_R4;

            case R5:
                return JSON_R5;

            default:
                return Assert.fail("Unsupported FHIR version: %s", version.getFhirVersionString());
        }
    }

    /**
     * Constructs a CDS Hook event name.
     *
     * @param eventType The event type.
     * @param components Additional components to append to the event.
     * @return The constructed event.
     */
    public static String makeEventName(String eventType, String... components) {
        StringBuilder sb = new StringBuilder("cdshook.").append(eventType);

        for (String component: components) {
            sb.append(".").append(component.replace(".", "_"));
        }

        return sb.toString();
    }

    private CdsHooksUtil() {
    }
}
