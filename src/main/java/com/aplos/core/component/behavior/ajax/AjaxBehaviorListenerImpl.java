package com.aplos.core.component.behavior.ajax;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

public class AjaxBehaviorListenerImpl implements AjaxBehaviorListener, Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = -239109903249415750L;
	private MethodExpression listener;

    public AjaxBehaviorListenerImpl() {}

    public AjaxBehaviorListenerImpl(MethodExpression listener) {
        this.listener = listener;
    }

    @Override
	public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        final ELContext elContext = context.getELContext();

        try{
            listener.invoke(elContext, new Object[]{});
        } catch (MethodNotFoundException mnfe) {
            MethodExpression argListener = context.getApplication().getExpressionFactory().
                        createMethodExpression(elContext, listener.getExpressionString(), null, new Class[]{event.getClass()});

            argListener.invoke(elContext, new Object[]{event});
        }
    }
}