package components;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.component.datatable.DataTable;


public class deleteBean extends UINamingContainer {
	private String reRenderTableId;
	private String beanName;
	public static final String HARD_DELETE = "hardDelete";
	/*
	 * Make sure it's transient so that it doesn't get serialised.
	 */
	private transient AplosLazyDataModel AqlAplosLazyDataModel;

	@Override
	public void encodeAll(FacesContext facesContext) throws IOException {
		super.encodeAll(facesContext);
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		super.encodeChildren(context);
	}

	public void setComponent(UIComponent component) {
	}

	public String getConfirmationVar() {
		return getClientId().replace( ":", "_" ) + "_confirmation";
	}

	public void setConfirmationVar( String var ) {
		// do nothing just a test
	}

	public AplosLazyDataModel getAplosLazyDataModel() {
		if( AqlAplosLazyDataModel == null ) {
			UIComponentBase parent = (UIComponentBase) getParent();

			beanName = "tableBean";

			while (true) {
				if (parent instanceof DataTable) {
					break;
				} else if (parent == null) {
					return null;
				}

				parent = (UIComponentBase) parent.getParent();
			}

			AqlAplosLazyDataModel = ((DataTable) parent).determineDataTableState().getLazyDataModel();
		}

		return AqlAplosLazyDataModel;
	}

	public AplosAbstractBean determineAplosAbstractBean() {
		return (AplosAbstractBean) JSFUtil.resolveVariable( beanName );
	}

	public void hardDeleteBeanAction() {
		getAplosLazyDataModel().determineDeleteBean( true );
	}

	public void deleteBeanAction() {
		getAplosLazyDataModel().determineDeleteBean( false );
	}

	public void reinstateBeanAction() {
		getAplosLazyDataModel().reinstateBean();
	}

	public String getDeleteBeanImage() {
		return ComponentUtil.getImageUrl(FacesContext.getCurrentInstance(), "action_delete.gif");
	}

	public String getReinstateBeanImage() {
		return ComponentUtil.getImageUrl(FacesContext.getCurrentInstance(), "action_add.gif");
	}
}
