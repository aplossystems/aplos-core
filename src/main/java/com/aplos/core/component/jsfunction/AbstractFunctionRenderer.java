package com.aplos.core.component.jsfunction;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.Renderer;

import com.aplos.common.utils.ComponentUtil;

public abstract class AbstractFunctionRenderer extends Renderer {

	public abstract String getFunction(FacesContext context, UIComponent component) throws IOException;
	public abstract String getSourceId( FacesContext facesContext, UIComponent component );

    @Override
	public void decode(FacesContext facesContext, UIComponent uiComponent) {
        if (isSubmitted(facesContext, uiComponent)) {
        	uiComponent.queueEvent( new ActionEvent( uiComponent ) );
        }
    }

    protected boolean isSubmitted(FacesContext facesContext, UIComponent uiComponent) {
        if (ComponentUtil.determineBooleanAttributeValue(uiComponent, "disabled", false)) {
            return false;
        }

        Object value = getSourceId( facesContext, uiComponent );
        boolean submitted = uiComponent.getClientId().equals( value );

        return submitted;
    }

    public Map<String, Object> createParametersMap(UIComponent component) {
        Map<String, Object> parameters = new LinkedHashMap<String, Object>();

        if (component.getChildCount() > 0) {
            for (UIComponent child : component.getChildren()) {
                if (child instanceof UIParameter) {
                    UIParameter parameter = (UIParameter) child;
                    String name = parameter.getName();
                    Object value = parameter.getValue();
                    parameters.put(name, value);
                }
            }
        }

        return parameters;
    }

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component)
			throws IOException {
		ResponseWriter responseWriter = facesContext.getResponseWriter();
		String clientId = component.getClientId(facesContext);
		responseWriter.startElement("span", component);
		{
			String value = clientId;
			if (null != value && value.length() > 0) {
				responseWriter.writeAttribute("id", value, null);
			}

		}

		responseWriter.writeAttribute("style", "display: none;", null);

		responseWriter.startElement("script", component);
		responseWriter.writeAttribute("type", "text/javascript", null);

		{
			Object text = this.getFunction(facesContext, component);
			if (text != null) {
				responseWriter.writeText(text, null);
			}
		}

		responseWriter.endElement("script");
		responseWriter.endElement("span");

	}
}
