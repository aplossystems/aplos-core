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

@FacesComponent("com.aplos.core.component.RolloverButton")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class RolloverButton extends UIComponentBase {
	private static Logger logger = Logger.getLogger( RolloverButton.class );
	public static final String COMPONENT_TYPE = "com.aplos.core.component.RolloverButton";
	private Object[] _state = null;

	private String style;


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
			InputStream i = this.getClass().getResourceAsStream( ComponentConstants.TEMPLATES_FOLDER + "rolloverButtonBegin.html" );
			parser.setInput( i );

			// Component attributes
//			parser.setVariable( "ID", getClientId(facesContext) );
			parser.setVariable( "STYLE", ( style == null ? "" : style ) );
			parser.setVariable( "WIDTH", width );

			// Images
			parser.setVariable( "IMG_LEFT", ComponentUtil.getImageUrlWithTheme( facesContext, "btnLeft.png" ) );
			parser.setVariable( "IMG_MIDDLE", ComponentUtil.getImageUrlWithTheme( facesContext, "btnMiddle.png" ) );
			parser.setVariable( "IMG_RIGHT", ComponentUtil.getImageUrlWithTheme( facesContext, "btnRight.png" ) );

			parser.parse();
			writer.write( parser.toString() );
		} catch( NullPointerException e ) {
			logger.info( "Cannot find file /resources/rolloverButtonBegin.html. " + "Theme may not have been set in the session." );
		}
	}

	@Override
	public void encodeEnd( FacesContext facesContext ) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

		JDynamiTe parser = new JDynamiTe();
		try {
			InputStream i = this.getClass().getResourceAsStream( ComponentConstants.TEMPLATES_FOLDER + "rolloverButtonEnd.html" );
			parser.setInput( i );

			// Images
			parser.setVariable( "IMG_RIGHT", ComponentUtil.getImageUrlWithTheme( facesContext, "/btnRight.png" ) );

			parser.parse();
			writer.write( parser.toString() );
		} catch( NullPointerException e ) {
			logger.info( "Cannot find file /resources/rolloverButtonEnd.html. " + "Theme may not have been set in the session." );
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

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
