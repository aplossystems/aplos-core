package com.aplos.core.component;

import java.io.IOException;
import java.util.logging.Level;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.html_basic.TextareaRenderer;
import com.sun.faces.util.MessageUtils;

public class AplosTextareaRenderer extends TextareaRenderer {

	@Override
    protected String writeIdAttributeIfNecessary(FacesContext context,
                                                 ResponseWriter writer,
                                                 UIComponent component) {

        String id = null;
        try {
            writer.writeAttribute("id", id = component.getClientId(context),
                                  "id");
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING)) {
                String message = MessageUtils.getExceptionMessageString
                      (MessageUtils.CANT_WRITE_ID_ATTRIBUTE_ERROR_MESSAGE_ID,
                       e.getMessage());
                logger.warning(message);
            }
        }
        return id;

    }
}
