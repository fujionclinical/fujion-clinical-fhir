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
package org.fujionclinical.fhir.stu3.ui.reporting.common;

import org.fujion.ancillary.PrintOptions;
import org.fujion.component.BaseUIComponent;
import org.fujionclinical.fhir.stu3.ui.reporting.headers.ReportHeaderRegistry;
import org.springframework.util.StringUtils;

/**
 * Utility methods for processing report elements.
 */
public class Util {
    
    /**
     * Invokes a client-side print request.
     *
     * @param printRoot Root component for printing.
     * @param title Optional title text.
     * @param header Header to print at top of first page.
     * @param styleSheet Style sheet to be applied.
     */
    public static void print(BaseUIComponent printRoot, String title, String header, String styleSheet) {
        PrintOptions options = new PrintOptions();
        options.title = title;
        options.stylesheet = styleSheet;
        
        if (StringUtils.hasText(header)) {
            String id = ReportHeaderRegistry.getInstance().getHeaderId(header, printRoot.getPage());
            
            if (id != null) {
                options.prepend = "#" + id;
            }
        }
        
        printRoot.print(options);
    }
    
    /**
     * Enforces static class.
     */
    private Util() {
    }
}
