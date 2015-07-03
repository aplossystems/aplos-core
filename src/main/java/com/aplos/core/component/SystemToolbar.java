package com.aplos.core.component;

import java.io.IOException;

import javax.el.ExpressionFactory;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;

@FacesComponent("com.aplos.core.component.SystemToolbar")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class SystemToolbar extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.SystemToolbar";
	//private Object[] _state = null;

	@Override
	public void encodeBegin( FacesContext facesContext ) throws IOException {
		facesContext.getResponseWriter();

		UIComponent button = getFacet("systemToolbar");
		if (button == null) {
			createSystemToolbarFacet(facesContext);
		} else if( button.getChildCount() > 0 ) {
			HtmlCommandLink debugTxt = (HtmlCommandLink) button.getChildren().get( 0 );
			if( ApplicationUtil.getAplosContextListener().isDebugMode() ) {
				debugTxt.setValue( "Debug" );
				debugTxt.setStyleClass( "aplosSystemToolBarDebugTxt" );
			} else {
				debugTxt.setValue( "General" );
				debugTxt.setStyleClass( "aplosSystemToolBarGeneralTxt" );
			}
		}
		getFacet("systemToolbar").encodeAll(facesContext);

	}



	public void createSystemToolbarFacet( FacesContext facesContext ) {
		HtmlPanelGroup outerDiv = new HtmlPanelGroup();
		outerDiv.setId(ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ));

		if( JSFUtil.isLocalHost() ||
				ApplicationUtil.getAplosContextListener().isDebugMode() ) {
			HtmlCommandLink debugTxt = new HtmlCommandLink();
			debugTxt.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "debugTxt" ) );
			ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
			debugTxt.setActionExpression(expf.createMethodExpression(facesContext.getELContext(), "#{contextListener.toggleDebugMode}", null, new Class[] {}));
			if( ApplicationUtil.getAplosContextListener().isDebugMode() ) {
				debugTxt.setValue( "Debug" );
				debugTxt.setStyleClass( "aplosSystemToolBarDebugTxt" );
			} else {
				debugTxt.setValue( "General" );
				debugTxt.setStyleClass( "aplosSystemToolBarGeneralTxt" );
			}
			outerDiv.getChildren().add( debugTxt );
		}

		getFacets().put("systemToolbar", outerDiv );
	}

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

}
