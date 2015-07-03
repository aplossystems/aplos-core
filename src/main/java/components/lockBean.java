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
import com.aplos.core.component.InternalComponentHolder;
import com.aplos.core.component.datatable.DataTable;


public class lockBean extends UINamingContainer implements InternalComponentHolder {
	private String reRenderTableId;
	private String beanName;
	public static final String RERENDER_ATTRIBUTE = "reRender";
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

	@Override
	public void initialiseComponents(FacesContext facesContext) {
		getAplosLazyDataModel();
	}

	public AplosLazyDataModel getAplosLazyDataModel() {
		if( AqlAplosLazyDataModel == null ) {
			UIComponentBase parent = (UIComponentBase) getParent();
			String reRenderAttribute = null;
			if (this.getAttributes().get(RERENDER_ATTRIBUTE) != null) {
				reRenderAttribute = (String) this.getAttributes().get(RERENDER_ATTRIBUTE);
			}
			if (reRenderAttribute != null) {
				reRenderTableId = reRenderAttribute;
			}

			beanName = "tableBean";

			while (true) {
				if (parent instanceof DataTable) {
					if (reRenderAttribute == null) {
						reRenderTableId = parent.getId();
					}
					break;
				} else if (parent == null) {
					return null;
				}

				parent = (UIComponentBase) parent.getParent();
			}

			if (((DataTable) parent).determineDataTableState() != null ) {
				AqlAplosLazyDataModel = ((DataTable) parent).determineDataTableState().getLazyDataModel();
			}
		}

		return AqlAplosLazyDataModel;
	}

	public AplosAbstractBean determineAplosAbstractBean() {
		return (AplosAbstractBean) JSFUtil.resolveVariable( beanName );
	}

	public void lockBeanAction() {
		getAplosLazyDataModel().lockBean();
	}

	public void unlockBeanAction() {
		getAplosLazyDataModel().unlockBean();
	}

	public String getLockBeanImage() {
		return ComponentUtil.getImageUrl(FacesContext.getCurrentInstance(), "unlocked.gif");
	}

	public String getUnlockBeanImage() {
		return ComponentUtil.getImageUrl(FacesContext.getCurrentInstance(), "locked.gif");
	}

	public String getDeterminedReRender() {
		return reRenderTableId;
	}
}
