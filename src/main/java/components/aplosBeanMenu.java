package components;
import java.io.IOException;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.component.InternalComponentHolder;


public class aplosBeanMenu extends UINamingContainer implements InternalComponentHolder {
	
	@Override
	public void initialiseComponents(FacesContext facesContext) {
		getStateHelper().put( "beanValue", (AplosAbstractBean) getAttributes().get( "value" ) );
	}
	
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
	
	public void beanChanged() {
		AplosAbstractBean loadedBeanValue = getBeanValue();
		getValueExpression("value").setValue( JSFUtil.getFacesContext().getELContext(), loadedBeanValue);
		MethodExpression listener = (MethodExpression) getAttributes().get( "listener" );
		if( listener != null ) {
			listener.invoke(JSFUtil.getFacesContext().getELContext(), new Object[0]);
		}
	}
	
	public AplosAbstractBean getBeanValue() {
		return (AplosAbstractBean) getStateHelper().get( "beanValue" );
	}
	
	public void setBeanValue( AplosAbstractBean aplosBean ) {
		getStateHelper().put( "beanValue", aplosBean );
	}
}
