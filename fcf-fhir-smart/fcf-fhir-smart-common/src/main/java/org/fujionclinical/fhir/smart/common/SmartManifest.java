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

import org.fujion.common.JSONUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds a manifest for a SMART app.
 */
public class SmartManifest {

    private final Map<String, String> map = new HashMap<>();

    public SmartManifest() {
    }

    public SmartManifest(Map<String, String> map) {
        init(map);
    }

    /**
     * Creates a SmartManifest object from an input stream.
     *
     * @param stream Input stream.
     * @throws IOException An IO exception.
     */
    @SuppressWarnings("unchecked")
    public SmartManifest(InputStream stream) throws IOException {
        try (InputStream is = stream) {
            init(JSONUtil.getMapper().readValue(is, Map.class));
        }
    }

    public void init(Map<String, String> map) {
        this.map.clear();

        if (map != null) {
            this.map.putAll(map);
        }
    }

    public void init(SmartManifest manifest) {
        init(manifest == null ? null : manifest.map);
    }

    /**
     * Returns a manifest value as a string.
     *
     * @param key The manifest key.
     * @return The manifest value.
     */
    public String getValue(String key) {
        return map.get(key);
    }

}
