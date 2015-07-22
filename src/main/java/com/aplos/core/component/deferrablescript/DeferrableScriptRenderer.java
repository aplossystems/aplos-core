package com.aplos.core.component.deferrablescript;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.aplos.common.beans.Website;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;
import com.sun.faces.renderkit.html_basic.ScriptStyleBaseRenderer;

@FacesRenderer(componentFamily=DeferrableScript.COMPONENT_FAMILY, rendererType=DeferrableScriptRenderer.RENDERER_TYPE)
public class DeferrableScriptRenderer extends ScriptStyleBaseRenderer {

	// Public constants -----------------------------------------------------------------------------------------------

	/** The standard renderer type. */
	public static final String RENDERER_TYPE = "com.aplos.core.component.DeferrableScript";
	public static final String DEFER_FUNCTION_WRITTEN = "deferFunctionWritten";

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Writes a <code>&lt;script&gt;</code> element which calls <code>OmniFaces.DeferrableScript.add</code> with as
	 * arguments the script URL and, if any, the onbegin, onsuccess and/or onerror callbacks. If the script resource is
	 * not resolvable, then a <code>RES_NOT_FOUND</code> will be written to <code>src</code> attribute instead.
	 */
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		Map<String, Object> attributes = component.getAttributes();
		String library = (String) attributes.get("library");
		String name = (String) attributes.get("name");
		Boolean isDeferred = null;
		if( component.getAttributes().get( "defer" ) == null ) {
			Website website = Website.getCurrentWebsiteFromTabSession();
			if( website != null ) {
				isDeferred = website.isDeferringScript();
			}
		}
		if( isDeferred == null ) {
			isDeferred = ComponentUtil.determineBooleanAttributeValue( component, "defer", false );
		}
		Resource resource = context.getApplication().getResourceHandler().createResource(name, library);

		ResponseWriter writer = context.getResponseWriter();
		writer.write( "\n" );
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", "type");

		if (resource != null) {
			if( isDeferred ) {
				if( JSFUtil.getRequest().getAttribute(DEFER_FUNCTION_WRITTEN) == null ) {
					writer.write( "aplosDeferScript=function(){function n(e){if(document.readyState===\"complete\"){");
					writer.write("setTimeout(e)}else if(window.addEventListener){window.addEventListener(\"load\",e,false)" );
					writer.write("}else if(window.attachEvent){window.attachEvent(\"onload\",e)}else if(typeof window.onload===\"function\"){" );
					writer.write("var t=window.onload;window.onload=function(){t();e()}}else{window.onload=e}}" );
					writer.write("function r(e){if(e<0||e>=t.length){return} var n=t[e];var i=document.createElement(\"script\");var s=document.head||document.documentElement;");
					writer.write("i.async=true;i.src=n.url;i.onerror=function(){if(n.error){n.error()}};i.onload=i.onreadystatechange=function(t,s){" );
					writer.write("if(s||!i.readyState||/loaded|complete/.test(i.readyState)){i.onload=i.onreadystatechange=null;if(s){" );
					writer.write("i.onerror()}else if(n.success){n.success()}i=null;r(e+1)}};if(n.begin){n.begin()}s.insertBefore(i,null)}" );
					writer.write("var e={};var t=[];e.add=function(e,i,s,o){t.push({url:e,begin:i,success:s,error:o});if(t.length==1){" );
					writer.write("n(function(){r(0)})}};return e}();");
					JSFUtil.getRequest().setAttribute(DEFER_FUNCTION_WRITTEN, true);
				}
				
				writer.write("aplosDeferScript.add('");
				writer.write(resource.getRequestPath());
				writer.write('\'');
	
				String onbegin = (String) attributes.get("onbegin");
				String onsuccess = (String) attributes.get("onsuccess");
				String onerror = (String) attributes.get("onerror");
				boolean hasOnbegin = !CommonUtil.isNullOrEmpty(onbegin);
				boolean hasOnsuccess = !CommonUtil.isNullOrEmpty(onsuccess);
				boolean hasOnerror = !CommonUtil.isNullOrEmpty(onerror);
	
				if (hasOnbegin || hasOnsuccess || hasOnerror) {
					encodeFunctionArgument(writer, onbegin, hasOnbegin);
				}
	
				if (hasOnsuccess || hasOnerror) {
					encodeFunctionArgument(writer, onsuccess, hasOnsuccess);
				}
	
				if (hasOnerror) {
					encodeFunctionArgument(writer, onerror, true);
				}
	
				writer.write(");");
			} else {
				writer.writeAttribute("src", resource.getRequestPath(), "src");
			}
		}
		else {
			ApplicationUtil.handleError( new Exception( name ), false );
			writer.writeURIAttribute("src", "RES_NOT_FOUND", "src");
		}
	}
	
	@Override
	protected void endElement(ResponseWriter writer) throws IOException {	
	}
	
	@Override
	protected void startElement(ResponseWriter writer, UIComponent component)
			throws IOException {	
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
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("script");
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
