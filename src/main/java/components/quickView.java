package components;
import java.io.IOException;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.naming.directory.InvalidAttributeValueException;

import com.aplos.common.AplosUrl;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

public class quickView extends UINamingContainer {

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin(facesContext);
	}

	public boolean isBeanValid() {
		Object bean = getAttributes().get( "bean" );
		if (bean == null) {
			Boolean warnOnNull = (Boolean) getAttributes().get("warnOnNull");
			if (warnOnNull != null && warnOnNull) {
				return false;
			} else {
				return true;
			}
		}
		return bean instanceof AplosBean;
	}
	
	public void determineAction() throws InvalidAttributeValueException {
		ValueExpression valueExpression = getValueExpression( "action" );
		if( valueExpression != null ) {
			valueExpression.getValue(JSFUtil.getFacesContext().getELContext());
		} else {
			Object action = getAttributes().get( "action" );
			if (action == null) {
				AplosBean bean = (AplosBean) getAttributes().get( "bean" );
				if (bean != null) {
					bean.redirectToEditPage();
				}
			} else if (action instanceof String) {
				JSFUtil.redirect(new AplosUrl((String)action));
			} else {
				throw new InvalidAttributeValueException("action must be a String or EL");
			}
		}
		
	}
	
	public String getBeanTypeString() {
		AplosBean bean = (AplosBean) getAttributes().get( "bean" );
		if (bean != null) {
			String className = ApplicationUtil.getClass( bean ).getSimpleName();
			return FormatUtil.breakCamelCase(className);
		}
		return null;
	}
	
	public String getViewFilename() {
		
		Object viewName = getAttributes().get( "view" );
		if (viewName != null && viewName instanceof String) {
			return (String) viewName;
		} else {
			AplosBean bean = (AplosBean) getAttributes().get( "bean" );
			if (bean != null) {
				String className = ApplicationUtil.getClass( bean ).getSimpleName();
				return className.toLowerCase();
			}
		}
		
		return "default";
		
	}
	
	public String getDisplayText() throws InvalidAttributeValueException {
			
		Object text = getAttributes().get( "text" );
		if (text == null) {
			
			AplosBean bean = (AplosBean) getAttributes().get( "bean" );
			if (bean != null) {
				return bean.getDisplayName();
			} else {
				ValueExpression ve = getValueExpression( "text" );
				if (ve != null) {
					Object veObject = ve.getValue(JSFUtil.getFacesContext().getELContext());
					if (veObject instanceof AplosBean) {
						return ((AplosBean)veObject).getDisplayName();
					} else {
						return (String)veObject;
					}
				}
			}
			
		} else if (text instanceof MethodExpression) {
			
			return (String) ((MethodExpression) text).invoke(JSFUtil.getFacesContext().getELContext(), new Object[]{});
			
		} else if (text instanceof String) {

			return (String) text;
			
		} else {
			try {
				return String.valueOf(text);
			} catch (Exception ex) {
				return text.toString();
			}
		}
		
		return null;
		
	}

}
