package com.aplos.core.event;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import com.aplos.core.component.InternalComponentHolder;

/**
 * Registers components to auto update before rendering
 */
public class InternalComponentListener implements SystemEventListener {

    @Override
	public void processEvent(SystemEvent cse) throws AbortProcessingException {
    	InternalComponentHolder component = (InternalComponentHolder) cse.getSource();
        FacesContext context = FacesContext.getCurrentInstance();
        component.initialiseComponents( context );
    }

    @Override
	public boolean isListenerForSource(Object o) {
        return true;
    }
}
