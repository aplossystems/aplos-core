package com.aplos.core.component;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.outputpanel.OutputPanelRenderer;

@FacesRenderer(componentFamily="org.primefaces.component",rendererType="com.aplos.core.component.AplosPanelRenderer")
public class AplosPanelRenderer extends OutputPanelRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		AplosPanel panel = (AplosPanel) component;
        String clientId = panel.getClientId(context);
		String tagName = getLayoutTag(context, panel);
		if (tagName != null) {
			writer.startElement(tagName, panel);
			writer.writeAttribute("id", clientId, "id");
			if(panel.getStyle() != null) {
				writer.writeAttribute("style", panel.getStyle(), "style");
			}
			if(panel.getStyleClass() != null) {
				writer.writeAttribute("class", panel.getStyleClass(), "styleClass");
			}
			if(panel.getTitle() != null) {
				writer.writeAttribute("title", panel.getTitle(), "title");
			}
		}
		renderChildren(context, panel);
		if (tagName != null) {
			writer.endElement(tagName);
		}
	}

	@Override
	public void decode(FacesContext context, UIComponent component) {
		super.decode(context, component);

	}

	@Override
	protected String getLayoutTag(FacesContext context, OutputPanel panel) {
		String layout = panel.getLayout();
		if (layout.equalsIgnoreCase("none")) {
			return null;
		} else if (layout.equalsIgnoreCase("block")) {
			return "div";
		} else {
			return "span";
		}
	}
}
