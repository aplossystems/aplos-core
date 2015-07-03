package com.aplos.core.component.jsfunction;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.apache.commons.lang.StringUtils;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.util.MessageUtils;

@FacesRenderer(componentFamily="com.aplos.core.component.JsFunction",rendererType="com.aplos.core.component.jsfunction.JsFunctionRenderer")
public class JsFunctionRenderer extends AbstractFunctionRenderer {

	@Override
	public String getSourceId( FacesContext facesContext, UIComponent component ) {
		String clientId = component.getClientId(facesContext);
        Map<String, String> paramMap = facesContext.getExternalContext().getRequestParameterMap();
        return paramMap.get(clientId);
	}

    @Override
	public String getFunction(FacesContext context, UIComponent component) throws IOException {
        String functionName = (String) component.getAttributes().get("name");

        if (functionName == null) {
            throw new FacesException("Value of 'name' attribute of a4j:jsFunction component is null!");
        }

        Map<String, Object> parameterMap = createParametersMap(component);
        String joinedParameters = StringUtils.join( parameterMap.keySet(), "," );

        ResponseWriter writer = context.getResponseWriter();
        String formClientId = RenderKitUtils.getFormClientId(component, context);
        if (formClientId == null) {
            writer.write(MessageUtils.getExceptionMessageString(
                  MessageUtils.COMMAND_LINK_NO_FORM_MESSAGE_ID));
            writer.endElement("span");
            return null;
        }

        String componentClientId = component.getClientId(context);
        StringBuilder javascriptBuf = new StringBuilder(functionName).append("=function(");
        javascriptBuf.append( joinedParameters );
        javascriptBuf.append( "){ mojarra.jsfcljs(document.getElementById('" );
        javascriptBuf.append(formClientId);
        javascriptBuf.append("'),{");

        RenderKitUtils.appendProperty(javascriptBuf, componentClientId, componentClientId);

        for( String key : parameterMap.keySet() ) {
        	RenderKitUtils.appendProperty(javascriptBuf, key, key, false );
        }

        javascriptBuf.append("},'");

        /*
         * Not quite sure what this is yet.
        if (submitTarget != null) {
            javascriptBuf.append(submitTarget);
        }
        */

        javascriptBuf.append("') ");

//        if (preventDefault) {
//            javascriptBuf.append(";return false");
//        }

        javascriptBuf.append(" };");

        return javascriptBuf.toString();
    }
}
