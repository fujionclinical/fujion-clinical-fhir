package org.fujionclinical.fhir.lib.patientselection.common;

import org.fujion.common.StrUtil;

import static org.fujionclinical.fhir.lib.patientselection.common.Constants.*;

/**
 * Utility class for retrieving labels.
 */
public class Labels {

    private Labels() {
    }

    public static final String[] dateRanges() {
        return StrUtil.getLabel(LBL_DATE_RANGE_VALUES).split("\n");
    }

    public static final String txtDateRange() {
        return StrUtil.getLabel(LBL_DATE_RANGE_LABEL);
    }

    public static final String txtNoFilters() {
        return StrUtil.getLabel(LBL_WARN_NO_FILTERS);
    }

    public static final String txtNoPatients() {
        return StrUtil.getLabel(LBL_WARN_NO_PATIENTS);
    }

    public static final String txtNoList() {
        return StrUtil.getLabel(LBL_WARN_NO_LIST_SELECTED);
    }

    public static final String txtDemoTitle() {
        return StrUtil.getLabel(LBL_DEMOGRAPHIC_TITLE);
    }

    public static final String txtManageTitle() {
        return StrUtil.getLabel(LBL_MANAGE_TITLE);
    }

    public static final String txtRenameFilterTitle() {
        return StrUtil.getLabel(LBL_FILTER_RENAME_TITLE);
    }

    public static final String txtNewFilterTitle() {
        return StrUtil.getLabel(LBL_FILTER_NEW_TITLE);
    }

    public static final String txtFilterNamePrompt() {
        return StrUtil.getLabel(LBL_FILTER_NAME_PROMPT);
    }

    public static final String txtDeleteFilterTitle() {
        return StrUtil.getLabel(LBL_FILTER_DELETE_TITLE);
    }

    public static final String txtDeleteFilterPrompt() {
        return StrUtil.getLabel(LBL_FILTER_DELETE_PROMPT);
    }

    public static final String txtSearchMessage() {
        return StrUtil.getLabel(LBL_SEARCH_MESSAGE);
    }

    public static final String txtWaitMessage() {
        return StrUtil.getLabel(LBL_LIST_WAIT_MESSAGE);
    }
}
