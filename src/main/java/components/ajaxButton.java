package components;
import java.io.IOException;

import javax.el.MethodExpression;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.ThemeManager;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;

public class ajaxButton extends UINamingContainer {
	private final String USING_PICTURED_BUTTONS = "isUsingPicturedButtons";
	
	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin(facesContext);
		String theme = ThemeManager.getThemeManager().getTheme();
		getStateHelper().put(USING_PICTURED_BUTTONS, "modern".equals( theme ) );
	}
	
	public boolean determineAjaxEnabled() {
		boolean isAjaxEnabled = ComponentUtil.determineBooleanAttributeValue( this, "ajaxEnabled", false );
		if( isAjaxEnabled ) {
			return true;
		}
		if( getAttributes().get( "oncomplete") != null ) {
			return true;
		}
		if( getAttributes().get( "reRender") != null && !getAttributes().get( "reRender").equals( "null" ) ) {
			return true;
		}
		return false;
	}
	
	public String getStyleClasses() {
		StringBuffer styleClasses = new StringBuffer( "aplos-command-button" );
		if( isShowingPicturedBtn() ) {
			styleClasses.append( " aplosBtnAnchor" );
		}
		String styleClass = (String) getAttributes().get( "styleClass" );
		if( !CommonUtil.isNullOrEmpty( styleClass ) ) {
			styleClasses.append( " " ).append( styleClass );
		}
		return styleClasses.toString();
	}
	
	public boolean isShowingPicturedBtn() {
		Boolean isUsingPicturedBtns = (Boolean) getStateHelper().get( USING_PICTURED_BUTTONS ); 
		if( isUsingPicturedBtns == null ) {
			return false;
		} else {
			return isUsingPicturedBtns;
		}
	}
	
	public boolean isContainingActions() {
		if( getAttributes().get( "action" ) != null || 
				getAttributes().get( "oncomplete" ) != null || 
				(getAttributes().get( "reRender" ) != null && !getAttributes().get( "reRender").equals( "null" )) ) {
			return true;
		} else {
			return false;
		}
	}
	
	public void invokeAction() {
		MethodExpression methodExpr = (MethodExpression) getAttributes().get( "action" );
		if( methodExpr != null ) {
			methodExpr.invoke(JSFUtil.getFacesContext().getELContext(), new Object[0] );
		}
	}
}
