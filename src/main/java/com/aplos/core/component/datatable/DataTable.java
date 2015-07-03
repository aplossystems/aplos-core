package com.aplos.core.component.datatable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.model.DataModel;

import org.primefaces.component.api.UIData;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.row.Row;
import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SelectableDataModelWrapper;
import org.primefaces.model.SortOrder;
import org.primefaces.util.Constants;

import com.aplos.common.AplosDataListModel;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;

@FacesComponent("com.aplos.core.component.datatable.DataTable")
@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css" ),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="components", name="components.js")
})
public class DataTable extends UIData implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.DataTable";
	public static final String COMPONENT_FAMILY = "com.aplos.core.component";
	private static final String DEFAULT_RENDERER = "com.aplos.core.component.DataTableRenderer";
	private static final String OPTIMIZED_PACKAGE = "com.aplos.core.component.";
	
	public static final String RESET_FIELDS = "resetFields";

	protected enum PropertyKeys {

		widgetVar
		,scrollable
		,scrollHeight
		,scrollWidth
		,selectionMode
		,selectionSingle
		,selectionMultiple
		,rowIndexVar
		,emptyMessage
		,style
		,styleClass
		,dblClickSelect
		,liveScroll
		,rowStyleClass
		,onExpandStart
		,resizableColumns
		,sortBy
		,sortOrder
		,scrollRows
		,rowKey
		,filterEvent
		,tableStyle
		,tableStyleClass
		,actionAllowed;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public DataTable() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public DataTableState determineDataTableState() {
		return (DataTableState) getAttributes().get( "dataTableState" );
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);

		DataTableState dataTableState = determineDataTableState();
		if( dataTableState != null ) {
			Map<String,String> columnFilters = new HashMap<String,String>();
			for( Entry<String,String> mapEntry : dataTableState.getColumnFilters().entrySet() ) {
				columnFilters.put( mapEntry.getKey().trim(), mapEntry.getValue() );
			}
			setFilters( columnFilters );
			if( dataTableState.getSortField() != null ) {
				ExpressionFactory expf = context.getApplication().getExpressionFactory();
				ValueExpression sortByVE = expf.createValueExpression( context.getELContext(), "#{'" + dataTableState.getSortField() + "'}", String.class );
				setValueExpression("sortBy", sortByVE);
				setSortOrder( dataTableState.getSortOrder().toString() );
			}
		}
	}
	
	public void resetFilters() {
		setValueExpression("sortBy", null );
		setSortOrder( null );
		setFilters( new HashMap<String,String>() );
		DataTableState dataTableState = determineDataTableState();
		if( dataTableState != null ) {
			dataTableState.setSortField( null );
			dataTableState.getColumnFilters().clear();
		}
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
		handleAttribute("widgetVar", _widgetVar);
	}

	public boolean isScrollable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.scrollable, false);
	}
	public void setScrollable(boolean _scrollable) {
		getStateHelper().put(PropertyKeys.scrollable, _scrollable);
		handleAttribute("scrollable", _scrollable);
	}

	public int getScrollHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MIN_VALUE);
	}
	public void setScrollHeight(int _scrollHeight) {
		getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
		handleAttribute("scrollHeight", _scrollHeight);
	}

	public int getScrollWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollWidth, java.lang.Integer.MIN_VALUE);
	}
	public void setScrollWidth(int _scrollWidth) {
		getStateHelper().put(PropertyKeys.scrollWidth, _scrollWidth);
		handleAttribute("scrollWidth", _scrollWidth);
	}

	public java.lang.String getSelectionMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.selectionMode, null);
	}
	public void setSelectionMode(java.lang.String _selectionMode) {
		getStateHelper().put(PropertyKeys.selectionMode, _selectionMode);
		handleAttribute("selectionMode", _selectionMode);
	}

	public java.lang.Object getSelectionSingle() {
		return getStateHelper().eval(PropertyKeys.selectionSingle, null);
	}
	public void setSelectionSingle(java.lang.Object _selectionSingle) {
		getStateHelper().put(PropertyKeys.selectionSingle, _selectionSingle);
		handleAttribute("selectionSingle", _selectionSingle);
	}

	public java.lang.Object getSelectionMultiple() {
		return getStateHelper().eval(PropertyKeys.selectionMultiple, null);
	}
	public void setSelectionMultiple(java.lang.Object _selectionMultiple) {
		getStateHelper().put(PropertyKeys.selectionMultiple, _selectionMultiple);
		handleAttribute("selectionMultiple", _selectionMultiple);
	}

	public java.lang.String getRowIndexVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rowIndexVar, null);
	}
	public void setRowIndexVar(java.lang.String _rowIndexVar) {
		getStateHelper().put(PropertyKeys.rowIndexVar, _rowIndexVar);
		handleAttribute("rowIndexVar", _rowIndexVar);
	}

	public java.lang.String getEmptyMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyMessage, "No records found.");
	}
	public void setEmptyMessage(java.lang.String _emptyMessage) {
		getStateHelper().put(PropertyKeys.emptyMessage, _emptyMessage);
		handleAttribute("emptyMessage", _emptyMessage);
	}

	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(java.lang.String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
		handleAttribute("style", _style);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(java.lang.String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
		handleAttribute("styleClass", _styleClass);
	}

	public boolean isDblClickSelect() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dblClickSelect, false);
	}
	public void setDblClickSelect(boolean _dblClickSelect) {
		getStateHelper().put(PropertyKeys.dblClickSelect, _dblClickSelect);
		handleAttribute("dblClickSelect", _dblClickSelect);
	}

	public boolean isLiveScroll() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.liveScroll, false);
	}
	public void setLiveScroll(boolean _liveScroll) {
		getStateHelper().put(PropertyKeys.liveScroll, _liveScroll);
		handleAttribute("liveScroll", _liveScroll);
	}

	public java.lang.String getRowStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rowStyleClass, null);
	}
	public void setRowStyleClass(java.lang.String _rowStyleClass) {
		getStateHelper().put(PropertyKeys.rowStyleClass, _rowStyleClass);
		handleAttribute("rowStyleClass", _rowStyleClass);
	}

	public java.lang.String getOnExpandStart() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onExpandStart, null);
	}
	public void setOnExpandStart(java.lang.String _onExpandStart) {
		getStateHelper().put(PropertyKeys.onExpandStart, _onExpandStart);
		handleAttribute("onExpandStart", _onExpandStart);
	}

	public boolean isResizableColumns() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizableColumns, false);
	}
	public void setResizableColumns(boolean _resizableColumns) {
		getStateHelper().put(PropertyKeys.resizableColumns, _resizableColumns);
		handleAttribute("resizableColumns", _resizableColumns);
	}

	public java.lang.Object getSortBy() {
		return getStateHelper().eval(PropertyKeys.sortBy, null);
	}
	public void setSortBy(java.lang.Object _sortBy) {
		getStateHelper().put(PropertyKeys.sortBy, _sortBy);
		handleAttribute("sortBy", _sortBy);
	}

	public java.lang.String getSortOrder() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.sortOrder, "ascending");
	}
	public void setSortOrder(java.lang.String _sortOrder) {
		getStateHelper().put(PropertyKeys.sortOrder, _sortOrder);
		handleAttribute("sortOrder", _sortOrder);
	}

	public int getScrollRows() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollRows, 0);
	}
	public void setScrollRows(int _scrollRows) {
		getStateHelper().put(PropertyKeys.scrollRows, _scrollRows);
		handleAttribute("scrollRows", _scrollRows);
	}

	public java.lang.Object getRowKey() {
		return getStateHelper().eval(PropertyKeys.rowKey, null);
	}
	public void setRowKey(java.lang.Object _rowKey) {
		getStateHelper().put(PropertyKeys.rowKey, _rowKey);
		handleAttribute("rowKey", _rowKey);
	}

	public java.lang.String getFilterEvent() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.filterEvent, "keyup");
	}
	public void setFilterEvent(java.lang.String _filterEvent) {
		getStateHelper().put(PropertyKeys.filterEvent, _filterEvent);
		handleAttribute("filterEvent", _filterEvent);
	}

	public java.lang.String getTableStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.tableStyle, null);
	}
	public void setTableStyle(java.lang.String _tableStyle) {
		getStateHelper().put(PropertyKeys.tableStyle, _tableStyle);
		handleAttribute("tableStyle", _tableStyle);
	}

	public java.lang.String getTableStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.tableStyleClass, null);
	}
	public void setTableStyleClass(java.lang.String _tableStyleClass) {
		getStateHelper().put(PropertyKeys.tableStyleClass, _tableStyleClass);
		handleAttribute("tableStyleClass", _tableStyleClass);
	}


    private final static Logger logger = Logger.getLogger(DataTable.class.getName());

    public static final String CONTAINER_CLASS = "ui-datatable ui-widget";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String COLUMN_HEADER_CONTAINER_CLASS = "ui-header-column";
    public static final String COLUMN_FOOTER_CLASS = "ui-state-default";
    public static final String COLUMN_FOOTER_CONTAINER_CLASS = "ui-footer-column";
    public static final String DATA_CLASS = "ui-datatable-data ui-widget-content";
    public static final String EMPTY_DATA_CLASS = "ui-datatable-data-empty";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String HEADER_CLASS = "ui-datatable-header ui-widget-header";
    public static final String FOOTER_CLASS = "ui-datatable-footer ui-widget-header";
    public static final String SORTABLE_COLUMN_CLASS = "ui-sortable-column";
    public static final String SORTABLE_COLUMN_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
    public static final String SORTABLE_COLUMN_ASCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-n";
    public static final String SORTABLE_COLUMN_DESCENDING_ICON_CLASS = "ui-sortable-column-icon ui-icon ui-icon ui-icon-carat-2-n-s ui-icon-triangle-1-s";
    public static final String FILTER_COLUMN_CLASS = "ui-filter-column";
    public static final String COLUMN_FILTER_CLASS = "ui-column-filter";
    public static final String COLUMN_INPUT_FILTER_CLASS = "ui-column-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String RESIZABLE_COLUMN_CLASS = "ui-resizable-column";
    public static final String EXPANDED_ROW_CLASS = "ui-expanded-row";
    public static final String EXPANDED_ROW_CONTENT_CLASS = "ui-expanded-row-content";
    public static final String ROW_TOGGLER_CLASS = "ui-row-toggler";
    public static final String EDITABLE_COLUMN_CLASS = "ui-editable-column";
    public static final String CELL_EDITOR_CLASS = "ui-cell-editor";
    public static final String CELL_EDITOR_INPUT_CLASS = "ui-cell-editor-input";
    public static final String CELL_EDITOR_OUTPUT_CLASS = "ui-cell-editor-output";
    public static final String ROW_EDITOR_COLUMN_CLASS = "ui-row-editor-column";
    public static final String ROW_EDITOR_CLASS = "ui-row-editor";
    public static final String SELECTION_COLUMN_CLASS = "ui-selection-column";
    public static final String EVEN_ROW_CLASS = "ui-datatable-even";
    public static final String ODD_ROW_CLASS = "ui-datatable-odd";
    public static final String SCROLLABLE_CONTAINER_CLASS = "ui-datatable-scrollable";
    public static final String SCROLLABLE_HEADER_CLASS = "ui-widget-header ui-datatable-scrollable-header";
    public static final String SCROLLABLE_HEADER_BOX_CLASS = "ui-datatable-scrollable-header-box";
    public static final String SCROLLABLE_BODY_CLASS = "ui-datatable-scrollable-body";
    public static final String SCROLLABLE_FOOTER_CLASS = "ui-widget-header ui-datatable-scrollable-footer";
    public static final String SCROLLABLE_FOOTER_BOX_CLASS = "ui-datatable-scrollable-footer-box";
    public static final String COLUMN_RESIZER_CLASS = "ui-column-resizer";
    public static final String RESIZABLE_CONTAINER_CLASS = "ui-datatable-resizable";
    public static final String COLUMN_CONTENT_WRAPPER = "ui-dt-c";
    public static final String SUBTABLE_HEADER = "ui-datatable-subtable-header";
    public static final String SUBTABLE_FOOTER = "ui-datatable-subtable-footer";
    public static final String SUMMARY_ROW_CLASS = "ui-datatable-summaryrow ui-widget-header";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("page","sort","filter", "rowSelect", "rowUnselect", "rowEdit", "colResize"));

    public List<Column> columns;

    public List<Column> getColumns() {
        if(columns == null) {
            columns = new ArrayList<Column>();

            for(UIComponent child : this.getChildren()) {
                if(child.isRendered() && child instanceof Column) {
                    columns.add((Column) child);
                }
            }
        }

        return columns;
    }

    private Boolean pageRequest = null;
    private Boolean sortRequest = null;
    private Boolean filterRequest = null;
    //private Boolean clearFiltersRequest = null;

    public boolean isPaginationRequest(FacesContext context) {
        if(pageRequest == null) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            pageRequest = params.containsKey(this.getClientId(context) + "_paging");
        }

        return pageRequest;
    }

    public boolean isSortRequest(FacesContext context) {
        if(sortRequest == null) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            sortRequest = params.containsKey(this.getClientId(context) + "_sorting");
        }

        return sortRequest;
    }

    public boolean isFilterRequest(FacesContext context) {
        if(filterRequest == null) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            filterRequest = params.containsKey(this.getClientId(context) + "_filtering");
        }

        return filterRequest;
    }

    public boolean isGlobalFilterRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_globalFilter");
    }

    public boolean isInstantSelectionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_instantSelectedRowIndex");
    }

    public boolean isInstantUnselectionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_instantUnselectedRowIndex");
    }

    public boolean isRowExpansionRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_rowExpansion");
    }

    public boolean isRowEditRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_rowEdit");
    }

    public boolean isScrollingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_scrolling");
    }

    private Map<String,Column> filterMap;

    public Map<String,Column> getFilterMap() {
      if(filterMap == null) {
         filterMap = new HashMap<String,Column>();

         ColumnGroup group = getColumnGroup("header");
         if(group != null) {
            //column group
            for(UIComponent child : group.getChildren()) {
               Row headerRow = (Row) child;

               if(headerRow.isRendered()) {
                   for(UIComponent headerRowChild : headerRow.getChildren()) {
                      Column column= (Column) headerRowChild;

                      if(column.isRendered() && column.getValueExpression("filterBy") != null) {
                         filterMap.put(column.getClientId(FacesContext.getCurrentInstance()) + "_filter", column);
                      }
                   }
               }
            }
         } else {
            //single header row
            for(Column column : getColumns()) {
               if(column.getValueExpression("filterBy") != null) {
                  filterMap.put(column.getClientId(FacesContext.getCurrentInstance()) + "_filter", column);
               }
            }
         }
      }

      return filterMap;
   }

	public boolean hasFilter() {
		return getFilterMap().size() > 0;
	}

    public boolean isRowSelectionEnabled() {
        return this.getSelectionMode() != null && this.getSelectionMode() != "";
	}

    public boolean isColumnSelectionEnabled() {
        return getColumnSelectionMode() != null && getColumnSelectionMode() != "";
	}

    public String getColumnSelectionMode() {
        for(Column column : getColumns()) {
            String selectionMode = column.getSelectionMode();
            if(selectionMode != null) {
                return selectionMode;
            }
        }

		return null;
	}

    public boolean isSelectionEnabled() {
        return this.isRowSelectionEnabled() || isColumnSelectionEnabled();
	}

    public boolean isSingleSelectionMode() {
		String selectionMode = this.getSelectionMode();
        String columnSelectionMode = this.getColumnSelectionMode();

		if(selectionMode != null) {
			return selectionMode.equalsIgnoreCase("single");
		} else if(columnSelectionMode != null) {
			return columnSelectionMode.equalsIgnoreCase("single");
		} else {
			return false;
		}
	}

    @Override
    public void processDecodes(FacesContext context) {
		String clientId = getClientId(context);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

        if(isBodyUpdate(context)) {
            this.decode(context);
        }
        else if(params.containsKey(clientId + "_rowEditCancel")) {
            context.renderResponse();
        } else {
            super.processDecodes(context);
        }
	}

    @Override
    public void processUpdates(FacesContext context) {
		super.processUpdates(context);

		ValueExpression selectionVE = null;
		if( getSelectionMultiple() != null ) {
	        selectionVE = this.getValueExpression("selectionMultiple");

	        if(selectionVE != null) {
	            selectionVE.setValue(context.getELContext(), getStateHelper().get(PropertyKeys.selectionMultiple));
	            this.setSelectionMultiple(null);
	        }	
		} else {
	        selectionVE = this.getValueExpression("selectionSingle");

	        if(selectionVE != null) {
	            selectionVE.setValue(context.getELContext(), getStateHelper().get(PropertyKeys.selectionSingle));
	            this.setSelectionSingle(null);
	        }	
		}
	}

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            setRowIndex(-1);
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("rowSelect")) {
                String rowKey = params.get(clientId + "_instantSelectedRowKey");
                wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowKey));
            }
            else if(eventName.equals("rowUnselect")) {
                String rowKey = params.get(clientId + "_instantUnselectedRowKey");
                wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), this.getRowData(rowKey));
            }
            else if(eventName.equals("page")) {
                wrapperEvent = new PageEvent(this, behaviorEvent.getBehavior(), this.getPage());
            }
            else if(eventName.equals("sort")) {
                SortOrder order = SortOrder.valueOf(params.get(clientId + "_sortDir"));
                Column sortColumn = findColumn(params.get(clientId + "_sortKey"));

                wrapperEvent = new SortEvent(this, behaviorEvent.getBehavior(), sortColumn, order);
            }
            else if(eventName.equals("filter")) {
                wrapperEvent = event;
            }
            else if(eventName.equals("rowEdit")) {
                int editedRowIndex = Integer.parseInt(params.get(clientId + "_editedRowIndex"));
                setRowIndex(editedRowIndex);
                wrapperEvent = new RowEditEvent(this, behaviorEvent.getBehavior(), this.getRowData());
            }
            else if(eventName.equals("colResize")) {
                String columnId = params.get(clientId + "_columnId");
                int width = Integer.parseInt(params.get(clientId + "_width"));
                int height = Integer.parseInt(params.get(clientId + "_height"));

                wrapperEvent = new ColumnResizeEvent(this, behaviorEvent.getBehavior(), width, height, findColumn(columnId));
            }

            wrapperEvent.setPhaseId(event.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    private Column findColumn(String clientId) {
        for(Column column : getColumns()) {
            if(column.getClientId().equals(clientId)) {
                return column;
            }
        }

        return null;
    }

    public ColumnGroup getColumnGroup(String target) {
        for(UIComponent child : this.getChildren()) {
            if(child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                String type = colGroup.getType();

                if(type != null && type.equals(target)) {
                    return colGroup;
                }

            }
        }

        return null;
    }

    public boolean hasFooterColumn() {
        for(Column column : getColumns()) {
            if(column.getFacet("footer") != null || column.getFooterText() != null) {
				return true;
			}
        }

        return false;
    }
    
    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
    	if( getDataModel() == null ) {
    		return false;
    	}
    	return super.visitTree(context, callback);
    }

    public void loadLazyData() {
        DataModel model = getDataModel();

        if(model != null && model instanceof LazyDataModel) {
            LazyDataModel lazyModel = (LazyDataModel) model;

            Map<String,String> filters = getFilters();
            if( filters.get( "cc.idValueExpression" ) != null ) {
            	filters.put( "bean.id", filters.get( "cc.idValueExpression" ));
            	filters.remove( "cc.idValueExpression" );
            }
            List<?> data = lazyModel.load(getFirst(), getRows(), resolveSortField(this.getValueExpression("sortBy")), convertSortOrder(), filters);

            lazyModel.setPageSize(getRows());
            lazyModel.setWrappedData(data);

            //Update paginator for callback
            if(this.isPaginator()) {
                RequestContext requestContext = RequestContext.getCurrentInstance();

                if(requestContext != null) {
                    requestContext.addCallbackParam("totalRecords", lazyModel.getRowCount());
                }
            }
        }
    }

    protected SortOrder convertSortOrder() {
        String sortOrder = getSortOrder();

        if(sortOrder == null) {
			return SortOrder.UNSORTED;
		} else {
			return SortOrder.valueOf(sortOrder.toUpperCase(Locale.ENGLISH));
		}
    }

    protected String resolveSortField(ValueExpression expression) {
        if(expression != null) {
            String expressionString = expression.getExpressionString();
            expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}

            return expressionString;               
        }
        else {
            return null;
        }
    }

    public void clearLazyCache() {
        LazyDataModel model = (LazyDataModel) getDataModel();
        model.setWrappedData(null);
    }

    @SuppressWarnings("unchecked")
	public Map<String,String> getFilters() {
        return (Map<String,String>) getStateHelper().eval("filters", new HashMap<String,String>());
    }

    public void setFilters(Map<String,String> filters) {
        getStateHelper().put("filters", filters);
    }

    public void resetValue() {
        setValue(null);
    }

    public void reset() {
        resetValue();
        setFirst(0);
    }

    public boolean isFilteringEnabled() {
        Object value = getStateHelper().get("filtering");

        return value == null ? false : true;
	}
	public void enableFiltering() {
		getStateHelper().put("filtering", true);
	}

    public RowExpansion getRowExpansion() {
        for(UIComponent kid : getChildren()) {
            if(kid instanceof RowExpansion) {
				return (RowExpansion) kid;
			}
        }

        return null;
    }

    private List filteredData;

    public void setFilteredData(List list) {
        this.filteredData = list;
    }

    public List getFilteredData() {
        return this.filteredData;
    }

    private SelectableDataModelWrapper selectableDataModelWrapper = null;

    /**
    * Override to support filtering, we return the filtered subset in getValue instead of actual data.
    * In case selectableDataModel is bound, we wrap it with filtered data.
    *
    */
    @Override
    public Object getValue() {
        Object value = super.getValue();

        if(filteredData != null) {
            if(value instanceof SelectableDataModel) {
                return selectableDataModelWrapper == null
                                ? (selectableDataModelWrapper = new SelectableDataModelWrapper((SelectableDataModel) value, filteredData))
                                : selectableDataModelWrapper;
            }
            else {
                return filteredData;
            }
        }
        else {
            return value;
        }
    }

    public Object getLocalSelection() {
    	if( getStateHelper().get(PropertyKeys.selectionMode) == "mutliple" ) {
    		return getStateHelper().get(PropertyKeys.selectionMultiple);
    	} else {
    		return getStateHelper().get(PropertyKeys.selectionSingle);
    	}
	}

    public void setLocalSelection(java.lang.Object _selection) {
    	if( getStateHelper().get(PropertyKeys.selectionMode) == "mutliple" ) {
    		setSelectionMultiple( _selection );
    	} else {
    		setSelectionSingle( _selection );
    	}
	}

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isRequestSource(FacesContext context) {
        String partialSource = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM);

        return partialSource != null && this.getClientId(context).startsWith(partialSource);
    }

    public boolean isBodyUpdate(FacesContext context) {
        String clientId = this.getClientId(context);

        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_updateBody");
    }

    public SubTable getSubTable() {
        for(UIComponent kid : getChildren()) {
            if(kid instanceof SubTable) {
				return (SubTable) kid;
			}
        }

        return null;
    }
    
    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
    	super.broadcast(event);
    	if( event instanceof RowClickEvent ) {
    		AplosAbstractBean aplosBean = null;
    		if( getDataModel() instanceof AplosDataListModel ) {
    			AplosDataListModel aplosDataListModel = (AplosDataListModel) getDataModel();
    			aplosBean = (AplosAbstractBean) aplosDataListModel.getRowData((String)((RowClickEvent) event).getRowKey());
        		JSFUtil.getRequest().setAttribute( "tableBean", aplosBean );
        		aplosDataListModel.selectBean();
    		} else {
        		aplosBean = (AplosAbstractBean) getRowData((String)((RowClickEvent) event).getRowKey());
        		AplosLazyDataModel aplosLazyDataModel = (AplosLazyDataModel) getDataModel();
        		JSFUtil.getRequest().setAttribute( "tableBean", aplosBean );
        		aplosLazyDataModel.selectBean();	
    		}
    	}
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getRowKeyFromModel(Object object) {
        DataModel model = getDataModel();
        if(!(model instanceof SelectableDataModel)) {
            throw new FacesException("DataModel must implement org.primefaces.model.SelectableDataModel when selection is enabled.");
        }

        return ((SelectableDataModel) model).getRowKey(object);
    }

    @SuppressWarnings("rawtypes")
	public Object getRowData(String rowKey) {
        boolean hasRowKeyVe = this.getValueExpression("rowKey") != null;

        if(hasRowKeyVe) {
            Map<String,Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
            String var = this.getVar();
            Collection data = (Collection) getDataModel().getWrappedData();

            for(Iterator it = data.iterator(); it.hasNext();) {
                Object object = it.next();
                requestMap.put(var, object);

                if(String.valueOf(this.getRowKey()).equals(rowKey)) {
                    return object;
                }
            }

            return null;
        }
        else {
            DataModel model = getDataModel();
            if(!(model instanceof SelectableDataModel)) {
                throw new FacesException("DataModel must implement org.primefaces.model.SelectableDataModel when selection is enabled or you need to define rowKey attribute");
            }

            return ((SelectableDataModel) getDataModel()).getRowData(rowKey);
        }
    }

    private List<Object> selectedRowKeys = new ArrayList<Object>();

    void findSelectedRowKeys() {
        Object selection = getLocalSelection();
        selectedRowKeys = new ArrayList<Object>();
        boolean hasRowKeyVe = this.getValueExpression("rowKey") != null;
        String var = this.getVar();
        Map<String,Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        if(isSelectionEnabled() && selection != null) {
            if(this.isSingleSelectionMode()) {
                if(hasRowKeyVe) {
                    requestMap.put(var, selection);
                    selectedRowKeys.add(this.getRowKey());
                }
                else {
                    selectedRowKeys.add(this.getRowKeyFromModel(selection));
                }
            }
            else {
                for(int i = 0; i < Array.getLength(selection); i++) {
                    if(hasRowKeyVe) {
                        requestMap.put(var, Array.get(selection, i));
                        selectedRowKeys.add(this.getRowKey());
                    }
                    else {
                        selectedRowKeys.add(this.getRowKeyFromModel(Array.get(selection, i)));
                    }
                }
            }
        }
    }

    List<Object> getSelectedRowKeys() {
        return selectedRowKeys;
    }

    String getSelectedRowKeysAsString() {
        StringBuilder builder = new StringBuilder();
        for(Iterator<Object> iter = getSelectedRowKeys().iterator(); iter.hasNext();) {
            builder.append(iter.next());

            if(iter.hasNext()) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    public SummaryRow getSummaryRow() {
        for(UIComponent kid : getChildren()) {
            if(kid.isRendered() && kid instanceof SummaryRow) {
                return (SummaryRow) kid;
            }
        }

        return null;
    }

    private int columnsCount = -1;

    @SuppressWarnings("rawtypes")
	public int getColumnsCount() {
        if(columnsCount == -1) {
            columnsCount = 0;

            for(UIComponent kid : getChildren()) {
                if(kid.isRendered()) {
                    if(kid instanceof Column) {
                        columnsCount++;
                    } else if(kid instanceof Columns) {
                        Columns columns = (Columns) kid;
                        Collection collection = (Collection) columns.getValue();
                        if(collection != null) {
                            columnsCount += collection.size();
                        }
                    }
                }
            }
        }

        return columnsCount;
    }

    public boolean isLazy() {
    	return true;
    }

    public void setLazy(boolean value) {
        logger.info("Lazy attribute has been removed from datatable api, please also remove it from your page definition. See issue #2993.");
    }


	@Override
	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	@Override
	public String resolveWidgetVar() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null) {
			return userWidgetVar;
		} else {
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
		}
	}

	public void handleAttribute(String name, Object value) {
		@SuppressWarnings("unchecked")
		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
		if(setAttributes == null) {
			String cname = this.getClass().getName();
			if(cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
				setAttributes = new ArrayList<String>(6);
				this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
			}
		}
		if(setAttributes != null) {
			if(value == null) {
				ValueExpression ve = getValueExpression(name);
				if(ve == null) {
					setAttributes.remove(name);
				} else if(!setAttributes.contains(name)) {
					setAttributes.add(name);
				}
			}
		}
	}
}
