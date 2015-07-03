package com.aplos.core.component.ajaxjsfunction;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import org.apache.commons.lang.StringUtils;
import org.primefaces.util.ComponentUtils;

import com.aplos.core.component.jsfunction.AbstractFunctionRenderer;
import com.sun.faces.renderkit.RenderKitUtils;

@FacesRenderer(componentFamily="com.aplos.core.component.AjaxJsFunction",rendererType="com.aplos.core.component.ajaxjsfunction.AjaxJsFunctionRenderer")
public class AjaxJsFunctionRenderer extends AbstractFunctionRenderer {

	@Override
	public String getSourceId( FacesContext facesContext, UIComponent component ) {
        Map<String, String> paramMap = facesContext.getExternalContext().getRequestParameterMap();
		return paramMap.get("javax.faces.source");
	}

    @Override
	public String getFunction(FacesContext context, UIComponent component) {
        AjaxJsFunction ajaxJsFunction = (AjaxJsFunction) component;
        String clientId = component.getClientId(context);

        String functionName = (String) component.getAttributes().get("name");

        if (functionName == null) {
            throw new FacesException("Value of 'name' attribute of a4j:jsFunction component is null!");
        }

        Map<String, Object> parameterMap = createParametersMap(component);
        String joinedParameters = StringUtils.join( parameterMap.keySet(), "," );
        StringBuilder javascriptBuf = new StringBuilder(functionName).append("=function(");
        javascriptBuf.append( joinedParameters );
        javascriptBuf.append(") { AplosComponents.ab(");

        //source
        javascriptBuf.append("{source:").append( "'" ).append( component.getClientId() ).append( "'" );

        //process
        String process = ajaxJsFunction.getProcess() != null ? ComponentUtils.findClientIds(context, component, ajaxJsFunction.getProcess()) : clientId;
        javascriptBuf.append(",process:'").append(process).append("'");

        //update
        if (ajaxJsFunction.getUpdate() != null) {
            javascriptBuf.append(",update:'").append(ComponentUtils.findClientIds(context, component, ajaxJsFunction.getUpdate())).append("'");
        }
        
        //delay
        if (ajaxJsFunction.getDelay() != null) {
            javascriptBuf.append(",delayVar:this,requestDelay:").append( ajaxJsFunction.getDelay() );
        }

        //behavior event
        javascriptBuf.append(",event:'").append(ajaxJsFunction.getEventName()).append("'");

        //async
        if(ajaxJsFunction.isAsync()) {
			javascriptBuf.append(",async:true");
		}

        //global
        if(!ajaxJsFunction.isGlobal()) {
			javascriptBuf.append(",global:false");
		}

        //callbacks
        if(ajaxJsFunction.getOnstart() != null) {
			javascriptBuf.append(",onstart:function(xhr){").append(ajaxJsFunction.getOnstart()).append(";}");
		}
        if(ajaxJsFunction.getOnerror() != null) {
			javascriptBuf.append(",onerror:function(xhr, status, error){").append(ajaxJsFunction.getOnerror()).append(";}");
		}
        if(ajaxJsFunction.getOnsuccess() != null) {
			javascriptBuf.append(",onsuccess:function(data, status, xhr){").append(ajaxJsFunction.getOnsuccess()).append(";}");
		}
        if(ajaxJsFunction.getOncomplete() != null) {
			javascriptBuf.append(",oncomplete:function(xhr, status, args){").append(ajaxJsFunction.getOncomplete()).append(";}");
		}

        javascriptBuf.append(",params:{");

        for( String key : parameterMap.keySet() ) {
        	RenderKitUtils.appendProperty(javascriptBuf, key, key, false );
        }

        javascriptBuf.append("}");


        javascriptBuf.append("}, arguments[1]); };");

        return javascriptBuf.toString();
    }
}
