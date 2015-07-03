package com.aplos.core.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.application.ViewHandler;
import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import org.primefaces.component.behavior.ajax.AjaxBehavior;
import org.primefaces.component.commandlink.CommandLink;

import com.aplos.common.appconstants.ComponentConstants;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;

@FacesComponent("com.aplos.core.component.CommandButton")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
})
public class CommandButton extends UICommand implements InternalComponentHolder, ClientBehaviorHolder {
	// Leave this component type so that the correct renderer can be found
	public static final String COMMAND_BUTTON = "commandButton";
	public static final String AJAX_ENABLED_ATTRIBUTE_KEY = "ajaxButtons";
	public static final String FORCE_THEME_ATTRIBUTE_KEY = "forceTheme";
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

	    	getFacet( COMMAND_BUTTON ).encodeAll(facesContext);
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
		boolean isAjaxEnabled = ComponentUtil.determineBooleanAttributeValue(this, AJAX_ENABLED_ATTRIBUTE_KEY, false );

		if( this.getAttributes().get( ComponentConstants.RERENDER_ATTRIBUTE_KEY ) != null ) {
			isAjaxEnabled = true;
		}

		AplosPanel cmdBtnPnl = new AplosPanel();
		HtmlPanelGroup disabledPanel=null;
		UICommand stdLink=null;
		if( isAjaxEnabled ) {
			stdLink = new CommandLink();
		} else {
			stdLink = new HtmlCommandLink();
		}
		disabledPanel = new HtmlPanelGroup();
		HtmlPanelGroup disabledCmdBtnPnl = new HtmlPanelGroup();
		HtmlPanelGroup cmdBtnLeftPnl = new HtmlPanelGroup();
		HtmlPanelGroup disabledCmdBtnLeftPnl = new HtmlPanelGroup();
		UIOutput text = new UIOutput();
		UIOutput disabledText = new UIOutput();
		HtmlPanelGroup cmdBtnRightPnl = new HtmlPanelGroup();
		HtmlPanelGroup disabledCmdBtnRightPnl = new HtmlPanelGroup();
		HtmlPanelGroup group = new HtmlPanelGroup();

		cmdBtnRightPnl.getChildren().add(text);
		disabledCmdBtnRightPnl.getChildren().add(disabledText);
		cmdBtnPnl.getChildren().add( cmdBtnLeftPnl );
		cmdBtnLeftPnl.getChildren().add( cmdBtnRightPnl );
		disabledCmdBtnPnl.getChildren().add( disabledCmdBtnLeftPnl );
		disabledCmdBtnLeftPnl.getChildren().add( disabledCmdBtnRightPnl );
		disabledPanel.getChildren().add( disabledCmdBtnPnl );
		stdLink.getChildren().add( cmdBtnPnl );
		group.getChildren().add(stdLink);
		group.getChildren().add(disabledPanel);
		getFacets().put( COMMAND_BUTTON, group );

		text.setId( getId() + "cmdBtnTxt" ); // required due to styling
		group.setId( getId() + "group" );
		disabledCmdBtnRightPnl.setId( getId() + "disabledCmdBtnRightPnl" );
		cmdBtnRightPnl.setId( getId() + "cmdBtnRightPnl" );
		disabledText.setId( getId() + "disabledText" );
		disabledCmdBtnLeftPnl.setId( getId() + "disabledCmdBtnLeftPnl" );
		cmdBtnLeftPnl.setId( getId() + "cmdBtnLeftPnl" );
		disabledCmdBtnPnl.setId( getId() + "disabledCmdBtnPnl" );
		disabledPanel.setId( getId() + "disabledPanel" );
		stdLink.setId( getId() + "stdLink" );
		disabledPanel.setId( getId() + "disabledPanel" );
		cmdBtnPnl.setId( getId() + "cmdBtnPnl" );

		List<UIComponent> addedChildren = new ArrayList<UIComponent>();
		addedChildren.add( cmdBtnPnl );
		addedChildren.add( disabledPanel );
		addedChildren.add( stdLink );
		addedChildren.add( disabledPanel );
		addedChildren.add( disabledCmdBtnPnl );
		addedChildren.add( cmdBtnLeftPnl );
		addedChildren.add( disabledCmdBtnLeftPnl );
		addedChildren.add( text );
		addedChildren.add( disabledText );
		addedChildren.add( cmdBtnRightPnl );
		addedChildren.add( disabledCmdBtnRightPnl );
		addedChildren.add( group );

		for( UIComponent component : addedChildren ) {
			component.getClientId( facesContext );
		}


		String style = CommonUtil.emptyIfNull( (String) getAttributes().get( ComponentConstants.STYLE_ATTRIBUTE_KEY ) );
		String tooltip = CommonUtil.emptyIfNull( (String) getAttributes().get( ComponentConstants.TITLE_ATTRIBUTE_KEY ) );
		String width = (String) getAttributes().get( ComponentConstants.WIDTH_ATTRIBUTE_KEY );

		String forceTheme = (String) getAttributes().get( CommandButton.FORCE_THEME_ATTRIBUTE_KEY );
		String imageLeft, imageMiddle, imageRight;
		if (forceTheme == null || forceTheme.equals("")) {
			imageLeft = null; //ComponentUtil.getImageUrlWithTheme( facesContext, "btnLeft.png" );
			imageMiddle = null; //ComponentUtil.getImageUrlWithTheme( facesContext, "btnMiddle.png" );
			imageRight = null; //ComponentUtil.getImageUrlWithTheme( facesContext, "btnRight.png" );
		} else {
			ViewHandler handler = facesContext.getApplication().getViewHandler();
			imageLeft = handler.getResourceURL(facesContext, ComponentConstants.IMAGE_PATH + forceTheme + "/" + "btnLeft.png");
			imageMiddle = handler.getResourceURL(facesContext, ComponentConstants.IMAGE_PATH + forceTheme + "/" + "btnMiddle.png");
			imageRight = handler.getResourceURL(facesContext, ComponentConstants.IMAGE_PATH + forceTheme + "/" + "btnRight.png");
		}


		if( width == null ) {
			width = "166";
		}


		if( getAttributes().get( ComponentConstants.IMMEDIATE_ATTRIBUTE_KEY ) != null && this.getAttributes().get(ComponentConstants.IMMEDIATE_ATTRIBUTE_KEY).equals( "true" ) ) {
			setImmediate(true);
		}

		String styleClass = (String) getAttributes().get( "styleClass" );
		if (styleClass != null) {
			styleClass = " " + styleClass + " ";
		} else {
			styleClass = "";
		}


		disabledPanel.getAttributes().put( ComponentConstants.STYLE_ATTRIBUTE_KEY, style );
		disabledPanel.getAttributes().put("styleClass", "aplos-command-button aplosBtnAnchor aplos-disabled " + styleClass);

		if( isAjaxEnabled ) {
			if( getAttributes().get( ComponentConstants.ONCLICK_ATTRIBUTE_KEY ) != null ) {
				stdLink.getAttributes().put( "onclick", getAttributes().get( ComponentConstants.ONCLICK_ATTRIBUTE_KEY ) );
			}
			if( getAttributes().get( ComponentConstants.RERENDER_ATTRIBUTE_KEY ) != null ) {
				((CommandLink)stdLink).setUpdate( (String) getAttributes().get( ComponentConstants.RERENDER_ATTRIBUTE_KEY ) );
			}

			if( getAttributes().get( ComponentConstants.ONCOMPLETE_ATTRIBUTE_KEY ) != null ) {
				((CommandLink)stdLink).setOncomplete( (String) getAttributes().get( ComponentConstants.ONCOMPLETE_ATTRIBUTE_KEY ) );
			}

			if( getAttributes().get( ComponentConstants.PROCESS_ATTRIBUTE_KEY ) != null ) {
				((CommandLink)stdLink).setProcess( (String) getAttributes().get( ComponentConstants.PROCESS_ATTRIBUTE_KEY ) );
			}
		} else {
			if( getAttributes().get( ComponentConstants.RERENDER_ATTRIBUTE_KEY ) != null ) {
				AjaxBehavior ajaxBehavior = new AjaxBehavior();
				if( getAttributes().get( ComponentConstants.ONCLICK_ATTRIBUTE_KEY ) != null ) {
					ajaxBehavior.setOnstart(getAttributes().get( ComponentConstants.ONCLICK_ATTRIBUTE_KEY ).toString());
				}

				if( getAttributes().get( ComponentConstants.ONCOMPLETE_ATTRIBUTE_KEY ) != null ) {
					ajaxBehavior.setOncomplete( (String) getAttributes().get( ComponentConstants.ONCOMPLETE_ATTRIBUTE_KEY ) );
				}

				if( getAttributes().get( ComponentConstants.PROCESS_ATTRIBUTE_KEY ) != null ) {
					ajaxBehavior.setProcess( (String) getAttributes().get( ComponentConstants.PROCESS_ATTRIBUTE_KEY ) );
				}
				ajaxBehavior.setUpdate( (String) getAttributes().get( ComponentConstants.RERENDER_ATTRIBUTE_KEY ) );
			    stdLink.addClientBehavior( "click", ajaxBehavior );
			} else {
				if( getAttributes().get( ComponentConstants.ONCLICK_ATTRIBUTE_KEY ) != null ) {
					stdLink.getAttributes().put( "onclick", getAttributes().get( ComponentConstants.ONCLICK_ATTRIBUTE_KEY ) );
				}
			}
		}
		stdLink.getAttributes().put( ComponentConstants.STYLE_ATTRIBUTE_KEY, style );
		stdLink.getAttributes().put( ComponentConstants.TITLE_ATTRIBUTE_KEY, tooltip );
		stdLink.setActionExpression( this.getActionExpression() );
		stdLink.getAttributes().put("styleClass", "aplos-command-button aplosBtnAnchor " + styleClass);
		if( getAttributes().get( ComponentConstants.TARGET_ATTRIBUTE_KEY ) != null ) {
			stdLink.getAttributes().put( "target", getAttributes().get( ComponentConstants.TARGET_ATTRIBUTE_KEY ) );
		}

		stdLink.setImmediate( isImmediate() );


		//  This had to be hacked as the AjaxCommandLink won't reRender a div if it's its child
		//  therefore block has been removed for this element so that it is rendered as a span
//		cmdBtnPnl.setLayout( "block" );
		if( getAttributes().get( "title" ) != null ) {
			cmdBtnPnl.setTitle( (String) getAttributes().get( "title" ) );
		}
		cmdBtnPnl.setStyleClass( "aplos-command-button-right aplosSmallBorder" );
		if (imageRight != null) {
			cmdBtnPnl.setStyle( "cursor:pointer;display:inline-block;" + style + "background-image:url('" + imageRight + "');" );
		}

		disabledCmdBtnPnl.setStyleClass( "aplos-command-button-right aplosSmallBorder" );
		if (imageRight != null) {
			disabledCmdBtnPnl.setStyle( "cursor:pointer;display:inline-block;" + style + "background-image:url('" + imageRight + "');" );
		}

		cmdBtnLeftPnl.setStyleClass( "aplos-command-button-left btnLeftPnl" );
		if (imageLeft != null) {
			cmdBtnLeftPnl.setStyle( "background-image:url('" + imageLeft + "');display:block" );
		}

		disabledCmdBtnLeftPnl.setStyleClass( "aplos-command-button-left btnLeftPnl" );
		if (imageLeft != null) {
			disabledCmdBtnLeftPnl.setStyle( "background-image:url('" + imageLeft + "');display:block" );
		}

		if( getValueExpression( "value" ) != null ) {
			text.setValueExpression("value", getValueExpression( "value" ) );
		} else {
			text.setValue( getValue() );
		}
		text.getAttributes().put( "style", "" );

		if( getValueExpression( "value" ) != null ) {
			disabledText.setValueExpression("value", getValueExpression( "value" ) );
		} else {
			disabledText.setValue( getValue() );
		}

		cmdBtnRightPnl.setStyleClass( "aplos-command-button-middle btnRightPnl" );
		if (imageMiddle != null) {
			cmdBtnRightPnl.setStyle( "display:block;background-image:url('" + imageMiddle + "');" );
		}

		disabledCmdBtnRightPnl.setStyleClass( "aplos-command-button-middle btnRightPnl" );
		if (imageMiddle != null) {
			disabledCmdBtnRightPnl.setStyle( "display:block;background-image:url('" + imageMiddle + "');" );
		}

		String disabledExpression = null;
		ValueExpression disabledValueExpression = getValueExpression(ComponentConstants.DISABLED_ATTRIBUTE_KEY);
		if (disabledValueExpression != null) {
			disabledExpression = disabledValueExpression.getExpressionString().substring(2,disabledValueExpression.getExpressionString().length()-1);
		}
		if (disabledExpression == null || disabledExpression.equals("")) {
			disabledExpression = "false";
		}
		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
		ValueExpression disabledPanelRenderedElExp = expf.createValueExpression(facesContext.getELContext(), "#{ " + disabledExpression + " }", Object.class);
		disabledPanel.setValueExpression("rendered", disabledPanelRenderedElExp);


		ValueExpression linkPanelRenderedElExp = expf.createValueExpression(facesContext.getELContext(), "#{ !(" + disabledExpression + ") }", Object.class);
		cmdBtnPnl.setValueExpression("rendered", linkPanelRenderedElExp);

		group.setLayout("none");
	}

	@Override
	public void encodeEnd( FacesContext facesContext ) throws IOException {	}

	@Override
	public String getFamily() {
		return super.getFamily();
	}

	public void setAjaxEnabled( boolean ajaxEnabled ) {
		if( ajaxEnabled ) {
			getAttributes().put( AJAX_ENABLED_ATTRIBUTE_KEY, "true" );
		} else {
			getAttributes().put( AJAX_ENABLED_ATTRIBUTE_KEY, "false" );
		}
	}

	@Override
	public void processDecodes(FacesContext context) {
		
		super.processDecodes(context);
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
