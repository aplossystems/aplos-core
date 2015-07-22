package com.aplos.core.component.deferrablestyle;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import com.aplos.common.beans.Website;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;
import com.sun.faces.renderkit.html_basic.ScriptStyleBaseRenderer;

@FacesRenderer(componentFamily=DeferrableStyle.COMPONENT_FAMILY, rendererType=DeferrableStyleRenderer.RENDERER_TYPE)
public class DeferrableStyleRenderer extends ScriptStyleBaseRenderer {

	// Public constants -----------------------------------------------------------------------------------------------

	/** The standard renderer type. */
	public static final String RENDERER_TYPE = "com.aplos.core.component.DeferrableStyle";
	public static final String DEFER_STYLE_WRITTEN = "deferStyleWritten";

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Writes a <code>&lt;style&gt;</code> element which calls <code>OmniFaces.Deferrablestyle.add</code> with as
	 * arguments the style URL and, if any, the onbegin, onsuccess and/or onerror callbacks. If the style resource is
	 * not resolvable, then a <code>RES_NOT_FOUND</code> will be written to <code>src</code> attribute instead.
	 */
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        Map<String,Object> attributes = component.getAttributes();
        Map<Object, Object> contextMap = context.getAttributes();

        String key = null;
        String href= (String) attributes.get("href");
        if( CommonUtil.isNullOrEmpty( href ) ) {
        	String name = (String) attributes.get("name");
        	String library = (String) attributes.get("library");
         	key = name + library;
            if (null == name) {
                return;
            }
    		Resource resource = context.getApplication().getResourceHandler().createResource(name, library);
    		if( resource == null ) {
    			ApplicationUtil.handleError( new Exception( "Stylesheet not found " + name ), false );
    			return;
    		}
            href = resource.getRequestPath();
        } else {
        	key = href;
        	/*
        	 * This stops it throwing an error when it trys to encode 
        	 * the children.
        	 */
        	attributes.put( "name", href );
        }
        
        /*
         *  Ensure this stylesheet is not rendered more than once per request.  This seems
         *  to be required because of a mistake in the HeadRenderer of primefaces that adds
         *  the style to both the style list and the script list!
         */
        if (contextMap.containsKey(key)) {
            return;
        }
        contextMap.put(key, Boolean.TRUE);
		
		Boolean isDeferred = null;
		if( component.getAttributes().get( "defer" ) == null ) {
			Website website = Website.getCurrentWebsiteFromTabSession();
			if( website != null ) {
				isDeferred = website.isDeferringStyle();
			}
		}
		if( isDeferred == null ) {
			isDeferred = ComponentUtil.determineBooleanAttributeValue( component, "defer", false );
		}

		ResponseWriter writer = context.getResponseWriter();
		writer.write( "\n" );

//		if( isDeferred ) {
//			writer.startElement("link", component);
//			writer.writeAttribute("type", "text/css", "type");
//			writer.writeAttribute("rel", "stylesheet", "rel");
//			writer.writeAttribute("href", href, "href");
//			writer.writeAttribute( "media", "none", "media" );
//			writer.writeAttribute( "onload", "if(media!='all')media='all'", "media" );
//			writer.endElement("link");
//		} else {
//			writer.startElement("link", component);
//			writer.writeAttribute("type", "text/css", "type");
//			writer.writeAttribute("rel", "stylesheet", "rel");
//			writer.writeAttribute("href", href, "href");
//			writer.endElement("link");
//		}
	}
	
	@Override
	protected void endElement(ResponseWriter writer) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void startElement(ResponseWriter writer, UIComponent component)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	private void encodeFunctionArgument(ResponseWriter writer, String function, boolean hasFunction) throws IOException {
		if (hasFunction) {
			writer.write(",function() {");
			writer.write(function);
			writer.write('}');
		}
		else {
			writer.write(",null");
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
	}
	
	@Override
	protected String verifyTarget(String toVerify) {
		if( CommonUtil.isNullOrEmpty(toVerify) ) {
			return "head";
		} else {
			if( toVerify.equalsIgnoreCase( "head" ) ) {
				return "head";
			} else if( toVerify.equalsIgnoreCase( "body" ) ) {
				return "body";
			}
		}
		return null;
	}

}
