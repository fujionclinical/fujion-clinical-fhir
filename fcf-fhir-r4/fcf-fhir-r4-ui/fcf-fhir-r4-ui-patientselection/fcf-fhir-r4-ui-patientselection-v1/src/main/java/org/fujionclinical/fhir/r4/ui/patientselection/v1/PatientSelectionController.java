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
package org.fujionclinical.fhir.r4.ui.patientselection.v1;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fujion.annotation.EventHandler;
import org.fujion.annotation.WiredComponent;
import org.fujion.common.DateRange;
import org.fujion.common.StrUtil;
import org.fujion.component.*;
import org.fujion.component.Rows.Selectable;
import org.fujion.component.Window.Mode;
import org.fujion.event.*;
import org.fujion.model.ListModel;
import org.fujionclinical.fhir.common.ui.patientselection.Constants;
import org.fujionclinical.fhir.r4.api.patient.PatientContext;
import org.fujionclinical.fhir.r4.api.patientlist.*;
import org.fujionclinical.fhir.r4.api.patientlist.IPatientListFilterManager.FilterCapability;
import org.fujionclinical.fhir.r4.ui.patientselection.*;
import org.fujionclinical.shell.ShellUtil;
import org.fujionclinical.ui.controller.FrameworkController;
import org.fujionclinical.ui.dialog.DateRangePicker;
import org.fujionclinical.ui.dialog.DialogUtil;
import org.hl7.fhir.r4.model.Patient;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Controller for patient selection dialog.
 */
public class PatientSelectionController extends FrameworkController {

    private static final Log log = LogFactory.getLog(PatientSelectionController.class);

    private static final String ATTR_PATIENT_LIST = "list";

    private static final String FILTER_DROP_ID = "patientselection.filter.drop.id";

    private final String[] dateRanges = StrUtil.getLabel(Constants.LBL_DATE_RANGE_VALUES).split("\n");

    private final String txtDateRange = StrUtil.getLabel(Constants.LBL_DATE_RANGE_LABEL);

    private final String txtNoFilters = StrUtil.getLabel(Constants.LBL_WARN_NO_FILTERS);

    private final String txtNoPatients = StrUtil.getLabel(Constants.LBL_WARN_NO_PATIENTS);

    private final String txtNoList = StrUtil.getLabel(Constants.LBL_WARN_NO_LIST_SELECTED);

    private final String txtDemoTitle = StrUtil.getLabel(Constants.LBL_DEMOGRAPHIC_TITLE);

    private final String txtManageTitle = StrUtil.getLabel(Constants.LBL_MANAGE_TITLE);

    private final String txtRenameFilterTitle = StrUtil.getLabel(Constants.LBL_FILTER_RENAME_TITLE);

    private final String txtNewFilterTitle = StrUtil.getLabel(Constants.LBL_FILTER_NEW_TITLE);

    private final String txtFilterNamePrompt = StrUtil.getLabel(Constants.LBL_FILTER_NAME_PROMPT);

    private final String txtDeleteFilterTitle = StrUtil.getLabel(Constants.LBL_FILTER_DELETE_TITLE);

    private final String txtDeleteFilterPrompt = StrUtil.getLabel(Constants.LBL_FILTER_DELETE_PROMPT);

    private final String txtSearchMessage = StrUtil.getLabel(Constants.LBL_SEARCH_MESSAGE);

    private final String txtWaitMessage = StrUtil.getLabel(Constants.LBL_LIST_WAIT_MESSAGE);

    @WiredComponent
    private Radiogroup rgrpLists;

    @WiredComponent
    private Listbox lstFilter;

    @WiredComponent
    private Label lblDateRange;

    @WiredComponent
    private DateRangePicker drpDateRange;

    @WiredComponent
    private Button btnManageList;

    @WiredComponent
    private Button btnFavorite;

    @WiredComponent
    private Textbox edtSearch;

    @WiredComponent
    private Grid grdSearch;

    @WiredComponent
    private Label lblPatientList;

    @WiredComponent
    private Grid grdPatientList;

    @WiredComponent
    private BaseUIComponent pnlDemographics;

    @WiredComponent
    private BaseUIComponent pnlDemoRoot;

    @WiredComponent
    private Button btnDemoDetail;

    @WiredComponent
    private Timer timer;

    @WiredComponent
    private BaseUIComponent pnlManagedList;

    @WiredComponent
    private BaseUIComponent pnlManagedListFilters;

    @WiredComponent
    private Listbox lstManagedListFilter;

    @WiredComponent
    private Button btnManagedListFilterNew;

    @WiredComponent
    private Button btnManagedListFilterRename;

    @WiredComponent
    private Button btnManagedListFilterDelete;

    @WiredComponent
    private BaseUIComponent pnlManagedListItems;

    @WiredComponent
    private Label lblManagedList;

    @WiredComponent
    private Button btnManagedListAdd;

    @WiredComponent
    private Button btnManagedListImport;

    @WiredComponent
    private Button btnManagedListAddCurrent;

    @WiredComponent
    private Button btnManagedListRemove;

    @WiredComponent
    private Button btnManagedListRemoveAll;

    @WiredComponent
    private Grid grdManagedList;

    @WiredComponent
    private Button btnOK;

    @WiredComponent
    private Pane paneDemographics;

    private Window root;

    private Radiobutton rbFavorites;

    private IPatientListRegistry registry;

    private IPatientList activeList;

    private IPatientList managedList;

    private IPatientList originalList;

    private IPatientListItemManager itemManager;

    private IPatientListFilterManager filterManager;

    private AbstractPatientListFilter activeFilter;

    private FavoritePatientList favorites;

    private Patient activePatient;

    private boolean manageListMode;

    private DateRange defaultDateRange;

    private final List<PatientListItem> pendingListItem = new ArrayList<>();

    private IPatientDetailRenderer patientDetailRenderer = new PatientDetailRenderer();

    /**
     * Handles drag/drop events for filters in filter management mode.
     */
    private final IEventListener filterDropListener = (event) -> {
        DropEvent dropEvent = (DropEvent) event;
        Listitem dragged = (Listitem) dropEvent.getRelatedTarget();
        Listitem target = (Listitem) dropEvent.getTarget();
        filterManager.moveFilter((AbstractPatientListFilter) dragged.getData(), target.getIndex());
        dragged.getListbox().addChild(dragged, target);
    };

    /**
     * Initial setup.
     */
    @Override
    public void afterInitialized(BaseComponent comp) {
        super.afterInitialized(comp);
        root = (Window) comp;
        initDateRanges();
        initRegisteredLists();
        initRenderers();
        ShellUtil.associateCSH(root, "patientSelectionV1Help", null, null);
    }

    /**
     * Initialize the date ranges to be used for filtering lists.
     */
    private void initDateRanges() {
        drpDateRange.loadChoices(dateRanges);
        defaultDateRange = drpDateRange.getSelectedRange();
    }

    /**
     * Loads the registered lists into the radio group.
     */
    private void initRegisteredLists() {
        for (IPatientList list : registry) {
            if (!list.isDisabled()) {
                Radiobutton radio = new Radiobutton();
                radio.setLabel(list.getName());
                radio.setAttribute(ATTR_PATIENT_LIST, list);
                rgrpLists.addChild(radio);

                if (list == favorites) {
                    rbFavorites = radio;
                }
            }
        }

        rgrpLists.getFirstChild(Radiobutton.class).setChecked(true);
        pendingListItem.add(new PatientListItem(null, txtWaitMessage));
    }

    /**
     * Initializes renderers for list boxes.
     */
    private void initRenderers() {
        setRenderer(grdPatientList);
        setRenderer(grdSearch);
        setRenderer(grdManagedList);
        setRenderer(lstFilter);
        setRenderer(lstManagedListFilter);
    }

    private void setRenderer(Grid grid) {
        grid.getRows().setRenderer(new PatientListItemRenderer(grid));
    }

    private void setRenderer(Listbox listbox) {
        listbox.setRenderer(new PatientListFilterRenderer());
    }

    /**
     * Returns the renderer for the patient detail view.
     *
     * @return Patient detail renderer.
     */
    public IPatientDetailRenderer getPatientDetailRenderer() {
        return patientDetailRenderer;
    }

    /**
     * Sets the renderer for the patient detail view.
     *
     * @param patientDetailRenderer The patient detail renderer.
     */
    public void setPatientDetailRenderer(IPatientDetailRenderer patientDetailRenderer) {
        this.patientDetailRenderer = patientDetailRenderer;
    }

    /**
     * Sets the specified list as active.
     *
     * @param list The patient list to make active.
     */
    private void setActiveList(IPatientList list) {
        activeList = list;
        activeFilter = null;
        btnFavorite.setDisabled(list == this.favorites);
        boolean hasDateRange = (list != null && list.isDateRangeRequired());
        lblDateRange.setVisible(hasDateRange);
        drpDateRange.setVisible(hasDateRange);

        if (hasDateRange) {
            DateRange range = list.getDateRange();

            if (range == null) {
                range = defaultDateRange;
                list.setDateRange(range);
            }

            Comboitem item = drpDateRange.findMatchingItem(range);
            item = item == null ? drpDateRange.addChoice(range, true) : item;
            drpDateRange.setSelectedItem(item);
            lblDateRange.setLabel(MessageFormat.format(txtDateRange, list.getEntityName()));
        }

        refreshFilterList();
        refreshPatientList();
        updateControls();
    }

    private void refreshFilterList() {
        boolean hasFilter = activeList != null && activeList.isFiltered();
        lstFilter.setVisible(hasFilter);

        if (hasFilter) {
            activeFilter = activeList.getActiveFilter();
            Collection<AbstractPatientListFilter> filters = activeList.getFilters();

            if (filters == null || filters.isEmpty()) {
                lstFilter.getModelAndView().setModel(null);
                lstFilter.addChild(new Listitem(txtNoFilters));
            } else {
                lstFilter.getModelAndView(AbstractPatientListFilter.class).setModel(new ListModel<>(filters));

                if (activeFilter == null) {
                    activeFilter = filters.iterator().next();
                    activeList.setActiveFilter(activeFilter);
                }
            }

            selectFilter(lstFilter, activeFilter);
        }
    }

    /**
     * Selects the list box item corresponding to the specified filter.
     *
     * @param lb List box to search.
     * @param filter The filter whose associated list item is to be selected.
     * @return True if the item was successfully selected.
     */
    private boolean selectFilter(Listbox lb, AbstractPatientListFilter filter) {
        if (filter != null) {
            for (Listitem item : lb.getChildren(Listitem.class)) {
                AbstractPatientListFilter flt = (AbstractPatientListFilter) item.getData();

                if (flt != null && filter.equals(flt)) {
                    lb.setSelectedItem(item);
                    //item.scrollIntoView(true);
                    return true;
                }
            }
        }

        return false;
    }

    private void refreshPatientList() {
        timer.stop();

        if (activeList != null) {
            Collection<PatientListItem> items;

            if (activeList.isPending()) {
                items = pendingListItem;
                timer.start();
            } else {
                items = activeList.getListItems();
            }

            ListModel<PatientListItem> model = items == null ? new ListModel<>() : new ListModel<>(items);

            if (model.isEmpty()) {
                model.add(new PatientListItem(null, txtNoPatients));
                grdPatientList.getRows().setSelectable(Selectable.NO);
            } else {
                grdPatientList.getRows().setSelectable(Selectable.SINGLE);
            }

            grdPatientList.getRows().setModel(model);
            lblPatientList.setLabel(activeList.getDisplayName());
        } else {
            grdPatientList.getRows().setModel(null);
            lblPatientList.setLabel(txtNoList);
        }

        setActivePatient((Patient) null);
    }

    private void setActiveFilter(AbstractPatientListFilter filter) {
        activeFilter = filter;
        activeList.setActiveFilter(filter);

        if (drpDateRange.isVisible()) {
            setActiveDateRange(drpDateRange.getSelectedRange());
        } else {
            refreshPatientList();
        }
    }

    private void setActiveDateRange(DateRange range) {
        if (range != null) {
            activeList.setDateRange(range);
            refreshFilterList();
            refreshPatientList();
        }
    }

    /**
     * Sets the active patient based on an event.
     *
     * @param event An event.
     */
    public void setActivePatient(Event event) {
        PatientListItem pli = getItem(event);
        setActivePatient(pli == null ? null : pli.getPatient());
    }

    private void setActivePatient(Patient patient) {
        // Build the demographic display here
        activePatient = patient;
        root.setAttribute(Constants.SELECTED_PATIENT_ATTRIB, activePatient);
        pnlDemoRoot.destroyChildren();

        if (patient != null && patientDetailRenderer != null) {
            pnlDemoRoot.addChild(patientDetailRenderer.render(patient));
        }

        btnDemoDetail.setDisabled(activePatient == null);
        updateControls();
    }

    /**
     * Called by Spring to finish initialization.
     */
    public void init() {
    }

    /**
     * Search for matching patients based on user input.
     */
    private void doSearch() {
        log.trace("Start doSearch()");
        grdPatientList.getRows().clearSelected();

        try {
            PatientSearchUtil.execute(edtSearch.getValue(), 100, (matches) -> {
                if (matches != null) {
                    grdSearch.getRows().setModel(new ListModel<>(matches));
                    grdSearch.getRows().setSelectable(Selectable.SINGLE);

                    if (matches.size() == 1) {
                        grdSearch.getRows().getFirstChild(Row.class).setSelected(true);
                        setActivePatient(matches.get(0));
                    }
                }
            });
        } catch (Exception e) {
            displaySearchMessage(e.getMessage());
        }

        edtSearch.setFocus(true);
        edtSearch.selectAll();
    }

    private void displaySearchMessage(String message) {
        grdSearch.getRows().setModel(null);

        if (message != null) {
            Row row = new Row();
            row.addChild(new Cell(message));
            row.setHint(message);
            grdSearch.getRows().addChild(row);
            grdSearch.getRows().setSelectable(Selectable.NO);
        }
    }

    /**
     * Set the patient list registry (injected by Spring).
     *
     * @param registry The patient list registry.
     */
    public void setPatientListRegistry(IPatientListRegistry registry) {
        this.registry = registry;
    }

    /**
     * Set a reference to the favorites list (injected by Spring).
     *
     * @param list The favorite patient list.
     */
    public void setFavoritesList(FavoritePatientList list) {
        this.favorites = list;
    }

    /**
     * Sets list management mode.
     *
     * @param value If true, the dialog enters list management mode. If false, the dialog reverts to
     *            patient selection mode.
     */
    private void setManageListMode(boolean value) {
        manageListMode = value;
        pnlManagedList.setVisible(value);
        pnlDemographics.setVisible(!value);
        paneDemographics.setTitle(MessageFormat.format(value ? txtManageTitle : txtDemoTitle, activeList.getName()));

        if (originalList != null) {
            originalList.refresh();
        }

        if (manageListMode) {
            originalList = activeList;
            managedList = activeList.copy();
            itemManager = managedList.getItemManager();
            filterManager = managedList.getFilterManager();
            pnlManagedListFilters.setVisible(filterManager != null);
            btnManagedListFilterNew.setVisible(filterManager != null && filterManager.hasCapability(FilterCapability.ADD));
            btnManagedListFilterDelete
            .setVisible(filterManager != null && filterManager.hasCapability(FilterCapability.REMOVE));
            btnManagedListFilterRename
            .setVisible(filterManager != null && filterManager.hasCapability(FilterCapability.RENAME));

            if (filterManager != null) {
                lstManagedListFilter.setModel(new ListModel<>(managedList.getFilters()));

                if (filterManager.hasCapability(FilterCapability.MOVE)) {
                    addDragDropSupport(lstManagedListFilter, FILTER_DROP_ID, filterDropListener);
                }
            }

            pnlManagedListItems.setVisible(itemManager != null);
            lblManagedList.setVisible(itemManager != null);
            grdManagedList.getRows().setModel(null);

            if (selectFilter(lstManagedListFilter, managedList.getActiveFilter())) {
                managedListFilterChanged();
            }
        } else {
            originalList = null;
            managedList = null;
            itemManager = null;
            filterManager = null;
            setActiveList(activeList);
        }

        updateControls();
    }

    /**
     * Changes the active filter for the currently managed list.
     *
     * @param filter The patient list filter to make active.
     */
    private void setManagedListFilter(AbstractPatientListFilter filter) {
        if (itemManager != null) {
            itemManager.save();
        }

        managedList.setActiveFilter(filter);
        managedListFilterChanged();

    }

    /**
     * Adds drag/drop support to the items belonging to the specified list box.
     *
     * @param lb The list box.
     * @param dropId The drop id to be used.
     * @param eventListener The event listener to handle the drag/drop operations.
     */
    private void addDragDropSupport(Listbox lb, String dropId, IEventListener eventListener) {
        for (Listitem item : lb.getChildren(Listitem.class)) {
            item.setDragid(dropId);
            item.setDropid(dropId);
            item.addEventListener(DropEvent.class, eventListener);
        }
    }

    /**
     * Update control states.
     */
    private void updateControls() {
        if (manageListMode) {
            boolean filterSelected = lstManagedListFilter.getSelectedItem() != null;
            boolean patientSelected = grdManagedList.getRows().getSelectedCount() != 0;
            btnManagedListFilterRename.setDisabled(!filterSelected);
            btnManagedListFilterDelete.setDisabled(!filterSelected);
            btnManagedListAddCurrent.setDisabled(!filterSelected || PatientContext.getActivePatient() == null);
            btnManagedListAdd.setDisabled(!filterSelected || activePatient == null);
            btnManagedListImport.setDisabled(!filterSelected || grdPatientList.getRows().getModel() == null);
            btnManagedListRemove.setDisabled(!patientSelected);
            btnManagedListRemoveAll.setDisabled(grdManagedList.getRows().getChildCount() == 0);
            btnOK.setDisabled(false);
            btnManageList.setDisabled(true);
        } else {
            btnManageList.setDisabled(
                activeList == null || (activeList.getItemManager() == null && activeList.getFilterManager() == null));
            btnOK.setDisabled(activePatient == null);
        }
    }

    /**
     * Adds the specified patient to the currently selected managed list.
     *
     * @param patient The patient to add.
     * @param refresh If true, refresh the display.
     */
    private void managedListAdd(Patient patient, boolean refresh) {
        if (patient != null) {
            managedListAdd(new PatientListItem(patient, null), refresh);
        }
    }

    private void managedListAdd(PatientListItem item, boolean refresh) {
        if (item != null && item.getPatient() != null) {
            itemManager.addItem(item);

            if (refresh) {
                managedListRefresh();
            }
        }
    }

    private void managedListRemove(PatientListItem item, boolean refresh) {
        if (item != null) {
            itemManager.removeItem(item);

            if (refresh) {
                managedListRefresh();
            }
        }
    }

    private void managedListRefresh() {
        grdManagedList.getRows().setModel(new ListModel<>(managedList.getListItems()));
    }

    private void managedListFilterChanged() {
        if (itemManager != null) {
            itemManager.save();
            grdManagedList.getRows().setModel(new ListModel<>(managedList.getListItems()));
            AbstractPatientListFilter filter = managedList.getActiveFilter();
            lblManagedList.setLabel(managedList.getEntityName() + (filter == null ? "" : ": " + filter.getName()));
        }
        updateControls();
    }

    private AbstractPatientListFilter getFilter(Event event) {
        Object target = event.getTarget();

        if (target instanceof Listbox) {
            return getFilter((Listbox) target);
        } else if (target instanceof Listitem) {
            return getFilter((Listitem) target);
        } else {
            return null;
        }
    }

    private AbstractPatientListFilter getFilter(Listbox lb) {
        return getFilter(lb.getSelectedItem());
    }

    private AbstractPatientListFilter getFilter(Listitem item) {
        return item == null ? null : (AbstractPatientListFilter) item.getData();
    }

    private PatientListItem getItem(Event event) {
        Row row = event.getTarget().getAncestor(Row.class, true);
        return getItem(row);
    }

    private PatientListItem getItem(Grid grid) {
        Row row = grid.getRows().getSelectedRow();
        return getItem(row);
    }

    private PatientListItem getItem(Row item) {
        return item == null ? null : (PatientListItem) item.getData();
    }

    /**
     * Adds or renames a filter.
     *
     * @param filter If not null, assumes we are renaming an existing filter. If null, assumes we
     *            are adding a new filter.
     * @param message Message to prefix to dialog prompt.
     */
    private void addOrRenameFilter(AbstractPatientListFilter filter, String message) {
        String oldName = filter == null ? null : filter.getName();

        DialogUtil.input(message + txtFilterNamePrompt, filter == null ? txtNewFilterTitle : txtRenameFilterTitle, oldName,
                (name) -> {
                    try {
                        if (!StringUtils.isEmpty(name)) {
                            AbstractPatientListFilter afilter;

                            if (filter == null) {
                                afilter = filterManager.addFilter(name);
                            } else {
                                afilter = filter;
                                filterManager.renameFilter(afilter, name);
                            }

                            lstManagedListFilter.setModel(new ListModel<>(managedList.getFilters()));
                            selectFilter(lstManagedListFilter, afilter);
                            setManagedListFilter(afilter);
                        }

                    } catch (PatientListException e) {
                        addOrRenameFilter(filter, e.getMessage() + "\n");
                    }
                });

    }

    private void doClose() {
        if (manageListMode) {
            if (itemManager != null) {
                itemManager.save();
            }

            setManageListMode(false);
            return;
        }

        if (activePatient == null) {
            doCancel();
            return;
        }

        root.close();
    }

    private void doCancel() {
        if (manageListMode) {
            setManageListMode(false);
        } else {
            root.removeAttribute(Constants.SELECTED_PATIENT_ATTRIB);
            root.close();
        }
    }

    /* ================== Event Handlers ================== */

    /* ----------------- Dialog Control ------------------- */

    /**
     * If in list management mode, clicking the OK button will save pending changes to the managed
     * list and revert to patient selection mode. If in patient selection mode, clicking the OK
     * button will select the current patient into the shared context and close the dialog.
     */
    @EventHandler(value = "click", target = "btnOK")
    private void onClick$btnOK() {
        doClose();
    }

    /**
     * If in list management mode, clicking the cancel button will cancel pending changes to the
     * managed list and revert to patient selection mode. If in patient selection mode, clicking the
     * cancel button will close the dialog without further action.
     */
    @EventHandler(value = "click", target = "btnCancel")
    private void onClick$btnCancel() {
        doCancel();
    }

    /**
     * Initializes dialog each time it is opened.
     */
    @EventHandler(value = "open")
    private void onOpen() {
        root.removeAttribute(Constants.SELECTED_PATIENT_ATTRIB);
        grdSearch.getRows().clearSelected();
        onChange$rgrpLists();

        if (root.getMode() != Mode.MODAL) {
            root.modal(null);
        }
    }

    /* ------------------ List Control -------------------- */

    /**
     * When a radio button is selected, its associated patient list is activated.
     */
    @EventHandler(value = "change", target = "rgrpLists")
    private void onChange$rgrpLists() {
        Radiobutton radio = rgrpLists.getSelected();

        if (radio == null) {
            radio = (Radiobutton) rgrpLists.getChildAt(0);
            radio.setChecked(true);
        }

        IPatientList list = (IPatientList) radio.getAttribute(ATTR_PATIENT_LIST);
        setActiveList(list);
    }

    @EventHandler(value = "timer", target = "timer")
    private void onTimer$timer(TimerEvent event) {
        if (activeList == null || !activeList.isPending()) {
            refreshPatientList();
        }
    }

    /**
     * When a filter is selected, make it the active filter for the active patient list.
     *
     * @param event The onSelect event.
     */
    @EventHandler(value = "change", target = "lstFilter")
    private void onChange$lstFilter(Event event) {
        setActiveFilter(getFilter(event));
    }

    /**
     * When the date range changes, make it the current date range for the active patient list.
     */
    @EventHandler(value = "selectRange", target = "drpDateRange")
    private void onSelectRange$drpDateRange() {
        setActiveDateRange(drpDateRange.getSelectedRange());
    }

    /**
     * Enter list management mode when the manage button is clicked.
     */
    @EventHandler(value = "click", target = "btnManageList")
    private void onClick$btnManageList() {
        setManageListMode(true);
    }

    /**
     * Add the active list to the favorites.
     */
    @EventHandler(value = "click", target = "btnFavorite")
    private void onClick$btnFavorite() {
        favorites.addFavorite(activeList);
        rbFavorites.invoke("widget$.effect", "pulsate", Collections.singletonMap("times", 3), 500);
    }

    /* ---------------- Patient Selection ------------------ */

    /**
     * Double-clicking a patient list item is the same as selecting it and then clicking the OK
     * button.
     *
     * @param event The onDoubleClick event.
     */
    @EventHandler(value = "dblclick", target = "grdPatientList")
    @EventHandler(value = "dblclick", target = "grdSearch")
    private void onDoubleClick$grdPatientList(Event event) {
        setActivePatient(event);

        if (activePatient != null) {
            if (!manageListMode) {
                doClose();
            } else if (itemManager != null && !btnManagedListAdd.isDisabled()) {
                managedListAdd(activePatient, true);
            }
        }
    }

    /**
     * Set the active patient when selected from the list.
     *
     * @param event The change event.
     */
    @EventHandler(value = "change", target = "grdPatientList")
    private void onChange$grdPatientList(ChangeEvent event) {
        selectedFromList(event, grdSearch);
    }

    @EventHandler(value = "change", target = "grdSearch")
    private void onChange$grdSearch(ChangeEvent event) {
        selectedFromList(event, grdPatientList);
    }

    private void selectedFromList(ChangeEvent event, Grid otherGrid) {
        if (event.getValue(Boolean.class)) {
            otherGrid.getRows().clearSelected();
            setActivePatient(event);
        }
    }

    /* ----------------- Patient Search ------------------- */

    @EventHandler(value = "click", target = "btnSearch")
    @EventHandler(value = "enter", target = "edtSearch")
    private void onClick$btnSearch() {
        root.addMask(txtSearchMessage);
        displaySearchMessage(txtSearchMessage);
        doSearch();
        root.removeMask();
        edtSearch.focus();
    }

    /* ----------------- List Management ------------------ */

    @EventHandler(value = "change", target = "lstManagedListFilter")
    private void onChange$lstManagedListFilter(Event event) {
        setManagedListFilter(getFilter(event));
    }

    @EventHandler(value = "change", target = "grdManagedList")
    private void onChange$grdManagedList() {
        updateControls();
    }

    /**
     * Create a new filter, prompting for a name.
     */
    @EventHandler(value = "click", target = "btnManagedListFilterNew")
    private void onClick$btnManagedListFilterNew() {
        addOrRenameFilter(null, "");
    }

    /**
     * Rename an existing filter, prompting for a new name.
     */
    @EventHandler(value = "click", target = "btnManagedListFilterRename")
    private void onClick$btnManagedListFilterRename() {
        addOrRenameFilter(managedList.getActiveFilter(), "");
    }

    @EventHandler(value = "click", target = "btnManagedListFilterDelete")
    private void onClick$btnManagedListFilterDelete() {
        AbstractPatientListFilter filter = managedList.getActiveFilter();

        if (filter != null) {
            DialogUtil.confirm(txtDeleteFilterPrompt, MessageFormat.format(txtDeleteFilterTitle, filter.getName()),
                (confirm) -> {
                    if (confirm) {
                        filterManager.removeFilter(filter);
                        lstManagedListFilter.getSelectedItem().detach();
                        setManagedListFilter(null);
                    }
                });
        }
    }

    @EventHandler(value = "click", target = "btnManagedListAddCurrent")
    private void onClick$btnManagedListAddCurrent() {
        managedListAdd(PatientContext.getActivePatient(), true);
    }

    @EventHandler(value = "click", target = "btnManagedListAdd")
    private void onClick$btnManagedListAdd() {
        managedListAdd(activePatient, true);
    }

    @EventHandler(value = "click", target = "btnManagedListImport")
    private void onClick$btnManagedListImport() {
        for (Object item : grdPatientList.getRows().getModel()) {
            managedListAdd((PatientListItem) item, false);
        }

        managedListRefresh();
    }

    @EventHandler(value = "click", target = "btnManagedListRemove")
    private void onClick$btnManagedListRemove() {
        managedListRemove(getItem(grdManagedList), true);
    }

    @EventHandler(value = "click", target = "btnManagedListRemoveAll")
    private void onClick$btnManagedListRemoveAll() {
        for (PatientListItem item : new ArrayList<>(managedList.getListItems())) {
            managedListRemove(item, false);
        }

        managedListRefresh();
    }

}
