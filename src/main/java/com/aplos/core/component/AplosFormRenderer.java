package com.aplos.core.component;

import java.io.IOException;
import java.util.Date;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpSession;

import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.sun.faces.renderkit.html_basic.FormRenderer;

@ResourceDependencies({
	@ResourceDependency(library="components", name="components.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="components", name="components.js")
})
public class AplosFormRenderer extends FormRenderer {
	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		super.encodeBegin(context, component);
   

        ResponseWriter writer = context.getResponseWriter();
		if( CommonConfiguration.getCommonConfiguration().isUsingWindowId() ) {
			writer.startElement("input", component);
	        writer.writeAttribute("type", "hidden", "type");
	        writer.writeAttribute("id", component.getClientId() + ":" + AplosAppConstants.WINDOW_ID, "id" );
	        writer.writeAttribute("name", AplosAppConstants.WINDOW_ID, "name" );
	        String windowId = CommonUtil.getStringOrEmpty( JSFUtil.getWindowId() );
	        if( !windowId.equals( "null" ) && !windowId.equals( "" ) ) {
		        try {
		        	Long.parseLong( windowId );
		        } catch( NumberFormatException nfEx ) {
		        	/**
		        	 *  do nothing it looks like various bots will send up false information
		        	 *  in an attempt to hack the system.
		        	 */
		        	windowId = "";
		        }
	        }
	        writer.writeAttribute("value", windowId, "value");
	        writer.endElement("input");
	        writer.write('\n');
		}

		BackingPage backingPage = JSFUtil.getCurrentBackingPage();
		if( backingPage != null ) {
			writer.startElement("input", component);
	        writer.writeAttribute("type", "hidden", "type");
	        writer.writeAttribute("id", component.getClientId() + ":" + AplosScopedBindings.DATE_TIME_PAGE_CREATED, "id" );
	        writer.writeAttribute("name", AplosScopedBindings.DATE_TIME_PAGE_CREATED, "name" );
	        writer.writeAttribute("value", FormatUtil.formatDateTimeForDB( new Date() ), "value");
	        writer.endElement("input");
	        writer.write('\n');
	        
	        HttpSession session = JSFUtil.getSessionTemp(false);
	        if( session != null ) {
				writer.startElement("input", component);
		        writer.writeAttribute("type", "hidden", "type");
		        writer.writeAttribute("id", component.getClientId() + ":" + AplosScopedBindings.LAST_SESSION_ID, "id" );
		        writer.writeAttribute("name", AplosScopedBindings.LAST_SESSION_ID, "name" );
		        writer.writeAttribute("value", session.getId(), "value");
		        writer.endElement("input");
		        writer.write('\n');
				writer.startElement("input", component);
		        writer.writeAttribute("type", "hidden", "type");
		        writer.writeAttribute("id", component.getClientId() + ":" + AplosScopedBindings.SESSION_LAST_ACCESSED, "id" );
		        writer.writeAttribute("name", AplosScopedBindings.SESSION_LAST_ACCESSED, "name" );
		        writer.writeAttribute("value", FormatUtil.formatDateTimeForDB( new Date( session.getLastAccessedTime() ) ), "value");
		        writer.endElement("input");
		        writer.write('\n');
				writer.startElement("input", component);
		        writer.writeAttribute("type", "hidden", "type");
		        writer.writeAttribute("id", component.getClientId() + ":" + AplosScopedBindings.SESSION_MAX_INTERVAL, "id" );
		        writer.writeAttribute("name", AplosScopedBindings.SESSION_MAX_INTERVAL, "name" );
		        writer.writeAttribute("value", session.getMaxInactiveInterval(), "value");
		        writer.endElement("input");
		        writer.write('\n');
	        }
		}
	}
}
