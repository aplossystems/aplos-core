package com.aplos.core.component.datatable;

import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;

public class RowClickEvent extends ActionEvent {
	private static final long serialVersionUID = -2253704725647096756L;
	private String rowKey;

	public RowClickEvent(DataTable dataTable,String rowKey) {
		super(dataTable);
		setRowKey(rowKey);
	}
	
	@Override
	public void processListener(FacesListener listener) {
		super.processListener(listener);
	}

	/**
	 * @return the rowKey
	 */
	public String getRowKey() {
		return rowKey;
	}

	/**
	 * @param rowKey the rowKey to set
	 */
	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
	
	
}
