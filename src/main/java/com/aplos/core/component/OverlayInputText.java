package com.aplos.core.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import com.aplos.common.appconstants.ComponentConstants;
import com.aplos.common.utils.CommonUtil;

@FacesComponent("com.aplos.core.component.OverlayInputText")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
})
public class OverlayInputText extends UIInput implements InternalComponentHolder, ClientBehaviorHolder {
	// Leave this component type so that the correct renderer can be found
	public static final String COMMAND_BUTTON = "commandButton";
	//private Object[] _state = null;

	@Override
	public String getRendererType() {
		return super.getRendererType();
	}

	@Override
	public void encodeBegin( FacesContext facesContext ) throws IOException {
		if( getAttributes().get( ComponentConstants.RENDERED_ATTRIBUTE_KEY ) == null ||
			!getAttributes().get( ComponentConstants.RENDERED_ATTRIBUTE_KEY ).equals( "false" ) ) {
			facesContext.getResponseWriter();
			//we need the button to rerender so we can use disabled, so dont do this check

 			HtmlInputText htmlInputText = (HtmlInputText) getFacet( COMMAND_BUTTON );
 			String overlayText = (String) getAttributes().get( "overlayText" ); 
			if( CommonUtil.isNullOrEmpty( (String) htmlInputText.getValue() ) ) {
				htmlInputText.setValue( overlayText );
				htmlInputText.setStyleClass( "overlayLabel" );
			}
			htmlInputText.encodeAll(facesContext);
		}
	}

	@Override
	public Collection<String> getEventNames() {
		List<String> eventNames = new ArrayList<String>();
		eventNames.add("click");
		return eventNames;
	}

	@Override
	public void initialiseComponents( FacesContext facesContext ) {
		UIComponent cmdBtnFacet = getFacet( COMMAND_BUTTON );
		if (cmdBtnFacet == null) {
			createCommandButtonFacet(facesContext);
		}
	}

	public void createCommandButtonFacet( FacesContext facesContext ) {
		String overlayText = (String) getAttributes().get( "overlayText" );
		HtmlInputText htmlInputText = new HtmlInputText();
		htmlInputText.setId( getId() + "inputText" );
		htmlInputText.setValueExpression("value", getValueExpression("value"));
		htmlInputText.setOnblur( "if( this.value == '' ) { this.value = '" + overlayText + "'; $(this).addClass( 'overlayLabel' ); }" );
		htmlInputText.setOnfocus( "if( this.value=='" + overlayText + "') { this.value = ''; $(this).removeClass( 'overlayLabel' ); }" );
		
		if( getValueExpression( "onkeyup" ) != null ) {
			htmlInputText.setValueExpression( "onkeyup", getValueExpression( "onkeyup" ) ); 
		}
		if( getAttributes().get( "onkeyup" ) != null ) {
			htmlInputText.getAttributes().put( "onkeyup", getAttributes().get( "onkeyup" ) );
		}
		htmlInputText.getClientId(facesContext);
		
		getFacets().put( COMMAND_BUTTON, htmlInputText );
	}

	@Override
	public void encodeEnd( FacesContext facesContext ) throws IOException {	}

	@Override
	public String getFamily() {
		return super.getFamily();
	}

	@Override
	public void processDecodes(FacesContext context) {
		super.processDecodes(context);
	}
	
	@Override
	public void decode(FacesContext context) {
		HtmlInputText htmlInputText = (HtmlInputText) getFacets().get( COMMAND_BUTTON );
		String overlayText = (String) getAttributes().get( "overlayText" );
		if( overlayText.equals( htmlInputText.getSubmittedValue() ) ) {
			htmlInputText.setValue( null );
			htmlInputText.setSubmittedValue( null );
		}
		super.decode(context);
	}

	@Override
	public Object saveState(FacesContext context) {
		
		return super.saveState(context);
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		
		super.restoreState(context, state);
	}

}
