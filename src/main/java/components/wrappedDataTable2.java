package components;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.component.datatable.DataTable;

public class wrappedDataTable2 extends UINamingContainer {
	public static final String SHOW_SEARCH = "showSearch";
	public static final String SHOW_DELETE_COLUMN = "showDeleteColumn";
	public static final String DATA_TABLE_STATE = "dataTableState";
	public static final String DELETE_BEAN_RENDERED = "deleteBeanRendered";
	public static final String MULTI_SELECT = "multiSelect";

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin(facesContext);
	}

	public String getJsFriendlyClientId() {
		String clientId = getClientId();
		return clientId.replace( ":", "_" );
	}
	
	public Object getSelectedRow() {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		if( dataTableState.getLazyDataModel() != null ) {
			return dataTableState.getLazyDataModel().getSelectedRow();
		} else {
			return null;
		}
	}
	
	public void setSelectedRow( Object selectedRow ) {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		if( dataTableState.getLazyDataModel() != null ) {
			dataTableState.getLazyDataModel().setSelectedRow( selectedRow );
		} 
	}
	
	public void resetFilters() {
		DataTable dataTable = (DataTable) findComponent( "internalDataTable" );
		if( dataTable != null ) {
			dataTable.resetFilters();
			JSFUtil.getRequest().setAttribute( DataTable.RESET_FIELDS, "true" );
		}
	}
	
	public Object[] getSelectedRows() {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		if( dataTableState.getLazyDataModel() != null ) {
			return dataTableState.getLazyDataModel().getSelectedRows();
		} else {
			return null;
		}
	}
	
	public String getMojarraBugFixPnlId() {
		return ":" + findComponent( "mojarraBugFixPnl" ).getClientId();
	}
	
	public Long getBeanId() {
		return (Long) JSFUtil.resolveVariable("tableBean.id");
	}
	
	public ValueExpression getIdValueExpression() {
	    try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			if (ctx != null) {
				ValueExpression ve = JSFUtil.createVE("bean.id", Long.class);
				return ve;
			}
	    } catch (Exception e) {
	        ApplicationUtil.handleError(e);
	    }
	    
	    return null;
	}
	
	public void setSelectedRows( Object[] selectedRows ) {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		if( dataTableState.getLazyDataModel() != null ) {
			dataTableState.getLazyDataModel().setSelectedRows(selectedRows);
		}
	}
	
	public void selectBean() {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		if( dataTableState.getDataListModel() != null ) {
			AplosBean aplosBean = (AplosBean) JSFUtil.getRequest().getAttribute( "tableBean" );
			aplosBean.redirectToEditPage();
		} else {
			dataTableState.getLazyDataModel().selectBean();
		}
	}
	
	public Object getDataTableValue() {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		/*
		 * This can happen in the render response phase where the tree is visited.  Even
		 * if the component isn't rendered it will check through the children via the 
		 * data model which might not exist as it wasn't meant to be rendered.
		 */
		if( dataTableState == null ) {
			return null;
		}
		if( dataTableState.getDataListModel() != null ) {
			return dataTableState.getDataListModel();
		} else {
			return dataTableState.getLazyDataModel();
		}
	}
	
	public String getDeletedColumnWidth() {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		boolean twoStageHardDelete = ComponentUtil.determineBooleanAttributeValue( this, "twoStageHardDelete", false);
		if( dataTableState.isShowingDeleted() && twoStageHardDelete ) {
			return "width:40px";
		} else {
			return "width:20px";
		}
	}

	public String getShowingDeletedBtnValue() {
		DataTableState dataTableState = (DataTableState) getAttributes().get( "dataTableState" );
		if ( dataTableState != null && dataTableState.isShowingDeleted() ) {
			return "Show active";
		} else {
			return "Show deleted";
		}
	}
	
	public String getSelectionMode() {
		Boolean multiSelect = ComponentUtil.determineBooleanAttributeValue( this, MULTI_SELECT, null );
		if( multiSelect ) {
			return "";
		} else {
			return "single";
		}
	}

	public String determineSearchTxt() {
		String searchTxt = (String) getAttributes().get( "searchTxt" );
		if (CommonConfiguration.getCommonConfiguration().getIsInternationalizedApplication()
				&& CommonUtil.isNullOrEmpty( searchTxt ) ) {
			searchTxt = CommonUtil.translate("SEARCH");
		}
		return searchTxt;
	}

	public boolean isShowingSearch() {
		Boolean showSearch = ComponentUtil.determineBooleanAttributeValue( this, SHOW_SEARCH, null );
		if( showSearch == null ) {
			DataTableState dataTableState = ((DataTableState) getAttributes().get( DATA_TABLE_STATE )); 
			if( dataTableState == null ) {
				return true;
			} else {
				return dataTableState.isShowingSearch();
			}
		} else {
			return showSearch;
		}
	}

	public boolean isShowingDeleteColumn() {
		boolean showDeleteColumn = ComponentUtil.determineBooleanAttributeValue( this, SHOW_DELETE_COLUMN, true );
		if( showDeleteColumn ) {
			DataTableState dataTableState = ((DataTableState) getAttributes().get( DATA_TABLE_STATE )); 
			if( dataTableState == null ) {
				return true;
			} else {
				showDeleteColumn = dataTableState.isShowingDeleteColumn();
				if( showDeleteColumn ) {
					ValueExpression ve = getValueExpression( DELETE_BEAN_RENDERED );
					if( ve != null ) {
						return (Boolean) ve.getValue( JSFUtil.getFacesContext().getELContext() );
					} else {
						return true;
					}
				}
			}
		}

		return false;
	}

	public String getMojarraBugFixPnlClientId( boolean addColon ) {
		String clientId = findComponent( "mojarraBugFixPnl" ).getClientId();
		if( addColon ) {
			clientId = ":" + clientId;
		}
		return clientId;
	}

	@Override
	public void processDecodes(FacesContext context) {
		super.processDecodes(context);
	}
}
