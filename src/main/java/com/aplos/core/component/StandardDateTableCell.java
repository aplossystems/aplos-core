package com.aplos.core.component;

import java.io.IOException;
import java.util.Date;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.aplos.common.utils.FormatUtil;

@FacesComponent("com.aplos.core.component.StandardDateTableCell")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class StandardDateTableCell extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.StandardDateTableCell";
	public static final String VALUE_ATTRIBUTE_KEY = "value";
	public static final String STYLE_ATTRIBUTE_KEY = "style";
	public static final String RENDERED_ATTRIBUTE_KEY = "rendered";

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin( facesContext );

		Date dateValue = (Date) getAttributes().get( VALUE_ATTRIBUTE_KEY );

		if (dateValue != null) {
			ResponseWriter writer = facesContext.getResponseWriter();
			writer.startElement( "div", this );
			writer.writeAttribute( "title", FormatUtil.getStdHourMinuteFormat().format( dateValue ), "title" );
			writer.write( FormatUtil.formatDate(dateValue) );
			writer.endElement( "div" );
		}
	}

}
