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
package org.fujionclinical.fhir.ui.reporting;

import org.fujionclinical.ui.util.FCFUtil;

/**
 * Package-specific constants.
 */
public class Constants {

    public static final String RESOURCE_PREFIX = FCFUtil.getResourcePath(Constants.class);

    public static final String SCLASS_ABNORMAL_RESULT = "fcf-reporting-abnormalResult";

    public static final String SCLASS_NORMAL_RANGE = "fcf-reporting-normalRange";

    public static final String SCLASS_DRILLDOWN_LINK = "fcf-reporting-drillDown-link";

    public static final String SCLASS_DRILLDOWN_GRID = "fcf-reporting-drillDown-grid";

    public static final String SCLASS_DRILLDOWN_DOCUMENT_TITLE = "fcf-reporting-drillDown-document-title";

    public static final String SCLASS_REPORT_ALL = "fcf-reporting-report-all";

    public static final String SCLASS_REPORT_HEADER = "fcf-reporting-header";

    public static final String SCLASS_REPORT_TITLE = "fcf-reporting-header-title";

    public static final String SCLASS_TEXT_REPORT_TITLE = "fcf-reporting-textReport-title";

    public static final String SCLASS_TEXT_REPORT_BODY = "fcf-reporting-textReport-body";

    public static final String SCLASS_TEXT_REPORT_HEADER = "fcf-reporting-textReport-header";

    public static final String PROPERTY_ID_DRILLDOWN = "CAREWEB.ENABLE.DRILLDOWN";

    public static final String PROPERTY_ID_DATE_RANGE = "%.DATERANGE";

    public static final String PROPERTY_ID_EXPAND_DETAIL = "%.EXPAND.DETAIL";

    public static final String PROPERTY_ID_MAX_ROWS = "%.MAX.ROWS";

    public static final String PROPERTY_ID_SORT_MODE = "%.SORT";

    public static final String LABEL_ID_SORT_MODE = "%.plugin.cmbx.sort.mode.item.$.label";

    public static final String LABEL_ID_TITLE = "%.plugin.print.title";

    public static final String LABEL_ID_PAGE_ON = "%.plugin.btn.paging.label.on";

    public static final String LABEL_ID_PAGE_OFF = "%.plugin.btn.paging.label.off";

    public static final String LABEL_ID_NO_PATIENT = "%.plugin.patient.selection.required";

    public static final String LABEL_ID_MISSING_PARAMETER = "%.plugin.missing.parameter";

    public static final String LABEL_ID_NO_DATA = "%.plugin.no.data.found";

    public static final String LABEL_ID_FETCHING = "%.plugin.status.fetching";

    public static final String LABEL_ID_FILTERING = "%.plugin.status.filtering";

    public static final String LABEL_ID_WAITING = "%.plugin.status.waiting";

    /**
     * Enforce static class.
     */
    private Constants() {
    }
}
