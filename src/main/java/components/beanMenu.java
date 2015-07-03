package components;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;

import com.aplos.common.BeanMenuHelper;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.utils.JSFUtil;


public class beanMenu extends UINamingContainer {

	public void setComponent(UIComponent component) {
	}
	
	public void beanChanged() {
		MethodExpression listener = (MethodExpression) getAttributes().get( "listener" );
		if( listener != null ) {
			listener.invoke(JSFUtil.getFacesContext().getELContext(), new Object[0]);
		}
	}
	
	public String determineLabel() {
		String label = (String) getAttributes().get( "label" );
		if( label == null ) {
			BeanMenuHelper beanMenuHelper = (BeanMenuHelper) getAttributes().get( "beanMenuHelper" );
			if( beanMenuHelper != null ) {
				return beanMenuHelper.getBeanClassDisplayName();
			}
		}
		return label;
	}
	
	public boolean determineIsShowingEditBtn() {
		BeanMenuHelper beanMenuHelper = (BeanMenuHelper) getAttributes().get( "beanMenuHelper" );
		if( beanMenuHelper != null ) {
			if( beanMenuHelper.determineIsShowingEditBtn() && getAttributes().get( "value" ) != null ) {
				return true;
			}
		}
		return false;
	}
	
	public String getBeanDisplayName() {
		Object value = getAttributes().get( "value" );
		if( value != null && value instanceof AplosAbstractBean ) {
			return ((AplosAbstractBean) value).getDisplayName();
		}
		return "";
	}
}
