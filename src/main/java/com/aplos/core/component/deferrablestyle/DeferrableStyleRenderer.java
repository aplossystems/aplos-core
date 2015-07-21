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

			if( isDeferred ) {
//				writer.startElement("script", component);
//				writer.writeAttribute("type", "text/javascript", "type");
//				if( JSFUtil.getRequest().getAttribute(DEFER_STYLE_WRITTEN) == null ) {
//					writer.write( "aplosDeferStyle=function(){function n(e){if(document.readyState===\"complete\"){");
//					writer.write("setTimeout(e)}else if(window.addEventListener){window.addEventListener(\"load\",e,false)" );
//					writer.write("}else if(window.attachEvent){window.attachEvent(\"onload\",e)}else if(typeof window.onload===\"function\"){" );
//					writer.write("var t=window.onload;window.onload=function(){t();e()}}else{window.onload=e}}" );
//					writer.write("function r(e){if(e<0||e>=t.length){return} var n=t[e];var i=document.createElement(\"link\");var s=document.head||document.documentElement;");
//					writer.write("i.media='only x';i.href=n.url;i.type='text/css';i.rel='stylesheet';i.onerror=function(){if(n.error){n.error()}};i.onload=i.onreadystatechange=function(t,s){" );
//					writer.write("if(s||!i.readyState||/loaded|complete/.test(i.readyState)){i.onload=i.onreadystatechange=null;if(s){" );
//					writer.write("i.onerror()}else if(n.success){n.success()}i.media='all';i=null;r(e+1)}};if(n.begin){n.begin()}s.insertBefore(i,null)}" );
//					writer.write("var e={};var t=[];e.add=function(e,i,s,o){t.push({url:e,begin:i,success:s,error:o});if(t.length==1){" );
//					writer.write("n(function(){r(0)})}};return e}();");
//					JSFUtil.getRequest().setAttribute(DEFER_STYLE_WRITTEN, true);
//				}
//				
//				writer.write("aplosDeferStyle.add('");
//				writer.write(resource.getRequestPath());
//				writer.write('\'');
//	
//				String onbegin = (String) attributes.get("onbegin");
//				String onsuccess = (String) attributes.get("onsuccess");
//				String onerror = (String) attributes.get("onerror");
//				boolean hasOnbegin = !CommonUtil.isNullOrEmpty(onbegin);
//				boolean hasOnsuccess = !CommonUtil.isNullOrEmpty(onsuccess);
//				boolean hasOnerror = !CommonUtil.isNullOrEmpty(onerror);
//	
//				if (hasOnbegin || hasOnsuccess || hasOnerror) {
//					encodeFunctionArgument(writer, onbegin, hasOnbegin);
//				}
//	
//				if (hasOnsuccess || hasOnerror) {
//					encodeFunctionArgument(writer, onsuccess, hasOnsuccess);
//				}
//	
//				if (hasOnerror) {
//					encodeFunctionArgument(writer, onerror, true);
//				}
//	
//				writer.write(");");
//				writer.endElement("script");
				writer.startElement("link", component);
				writer.writeAttribute("type", "text/css", "type");
				writer.writeAttribute("rel", "stylesheet", "rel");
				writer.writeAttribute("href", href, "href");
				writer.writeAttribute( "media", "none", "media" );
				writer.writeAttribute( "onload", "if(media!='all')media='all'", "media" );
				writer.endElement("link");
			} else {
				writer.startElement("link", component);
				writer.writeAttribute("type", "text/css", "type");
				writer.writeAttribute("rel", "stylesheet", "rel");
				writer.writeAttribute("href", href, "href");
				writer.endElement("link");
			}
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

}
