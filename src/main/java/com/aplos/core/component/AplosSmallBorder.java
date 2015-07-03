package com.aplos.core.component;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.appconstants.ComponentConstants;
import com.aplos.common.utils.ComponentUtil;

@FacesComponent("com.aplos.core.component.AplosSmallBorder")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class AplosSmallBorder extends UIComponentBase {
	private static Logger logger = Logger.getLogger( AplosSmallBorder.class );
	public static final String COMPONENT_TYPE = "com.aplos.core.component.AplosSmallBorder";
	public static final String IS_SEARCH_TEXT_FIELD_KEY = "isSearchTextField";
	private Object[] _state = null;

	@Override
	public void encodeBegin( FacesContext facesContext ) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String theme = ComponentUtil.getTheme( facesContext );

		String width = (String) getAttributes().get( ComponentConstants.WIDTH_ATTRIBUTE_KEY );

		if( width == null ) {
			width = "166";
		}

		JDynamiTe parser = new JDynamiTe();
		try {
			InputStream i = this.getClass().getResourceAsStream( ComponentConstants.TEMPLATES_FOLDER + "aplosSmallBorderBegin.html" );
			parser.setInput( i );

			// Component attributes
//			parser.setVariable( "ID", getClientId(facesContext) );
			String style = (String) getAttributes().get( ComponentConstants.STYLE_ATTRIBUTE_KEY );
			parser.setVariable( "STYLE", ( style == null ? "" : style ) );
			parser.setVariable( "WIDTH", width );
			parser.setVariable( "MID_WIDTH", String.valueOf( Integer.parseInt( width ) - 60 ) );

			// Images
			if( (getAttributes().get( IS_SEARCH_TEXT_FIELD_KEY ) == null) || getAttributes().get( IS_SEARCH_TEXT_FIELD_KEY ).toString() == "false" ) {
				parser.setVariable("APLOSSMALLBORDERLEFT", "aplosSmallBorderLeftDiv");
				parser.setVariable("APLOSSMALLBORDERMIDDLE", "aplosSmallBorderMiddleDiv");
				parser.setVariable("APLOSSMALLBORDERRIGHT", "aplosSmallBorderRightDiv");
				//Using the same classes as the command buttons they have to match (this ensures future changes get propagated)
				//parser.setVariable("APLOSSMALLBORDERLEFT", "aplos-command-button-left");
				//parser.setVariable("APLOSSMALLBORDERMIDDLE", "aplos-command-button-middle");
				//parser.setVariable("APLOSSMALLBORDERRIGHT", "aplos-command-button aplos-command-button-right");
				parser.setVariable( "IMG_LEFT", ComponentUtil.getImageUrlWithTheme( facesContext, "btnLeft.png" ) );
				parser.setVariable( "IMG_MIDDLE", ComponentUtil.getImageUrlWithTheme( facesContext, "btnMiddle.png" ) );
				parser.setVariable( "IMG_RIGHT", ComponentUtil.getImageUrlWithTheme( facesContext, "btnRight.png" ) );
				parser.setVariable( "LINE_HEIGHT", "30");
			}
			else {
				parser.setVariable("APLOSSMALLBORDERLEFT", "searchFieldLeftDiv");
				parser.setVariable("APLOSSMALLBORDERMIDDLE", "searchFieldMiddleDiv");
				parser.setVariable("APLOSSMALLBORDERRIGHT", "searchFieldRightDiv");
				parser.setVariable( "IMG_LEFT", ComponentUtil.getImageUrlWithTheme( facesContext, "searchFieldLeft.png" ) );
				parser.setVariable( "IMG_MIDDLE", ComponentUtil.getImageUrlWithTheme( facesContext, "searchFieldMiddle.png" ) );
				parser.setVariable( "IMG_RIGHT", ComponentUtil.getImageUrlWithTheme( facesContext, "searchFieldRight.png" ) );
				parser.setVariable( "LINE_HEIGHT", "45");
			}



			
			if (theme.equals("mellow")) {
				parser.setVariable("END_WIDTH", "19");
			} else {
				parser.setVariable("END_WIDTH", "30");
			}


			parser.parse();
			writer.write( parser.toString() );
		} catch( NullPointerException e ) {
			logger.info( "Cannot find file /resources/aplosSmallBorderBegin.html. " + "Theme may not have been set in the session." );
		}
	}

	@Override
	public void encodeEnd( FacesContext facesContext ) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

		JDynamiTe parser = new JDynamiTe();
		try {
			InputStream i = this.getClass().getResourceAsStream( ComponentConstants.TEMPLATES_FOLDER + "aplosSmallBorderEnd.html" );
			parser.setInput( i );

			// Images
			parser.setVariable( "IMG_RIGHT", ComponentUtil.getImageUrlWithTheme( facesContext, "/btnRight.png" ) );

			parser.parse();
			writer.write( parser.toString() );
		} catch( NullPointerException e ) {
			logger.info( "Cannot find file /resources/aplosSmallBorderEnd.html. " + "Theme may not have been set in the session." );
		}
	}

	@Override
	public void restoreState( FacesContext _context, Object _state ) {
		this._state = (Object[])_state;
		super.restoreState( _context, this._state[ 0 ] );
		ComponentUtil.addStateToAttributes( this, ComponentConstants.WIDTH_ATTRIBUTE_KEY, this._state[1] );
		ComponentUtil.addStateToAttributes( this, ComponentConstants.STYLE_ATTRIBUTE_KEY, this._state[2] );
	}

	@Override
	public Object saveState( FacesContext _context ) {
		if( _state == null ) {
			_state = new Object[ 3 ];
		}

		_state[ 0 ] = super.saveState( _context );

		_state[ 1 ] = this.getAttributes().get(ComponentConstants.WIDTH_ATTRIBUTE_KEY);
		_state[ 2 ] = this.getAttributes().get(ComponentConstants.STYLE_ATTRIBUTE_KEY);

		return _state;
	}

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

}
