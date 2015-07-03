package com.aplos.core.component.ajaxjsfunction;

import java.io.IOException;

import javax.el.MethodExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;


@FacesComponent("com.aplos.core.component.ajaxjsfunction.AjaxJsFunction")
@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class AjaxJsFunction extends UICommand implements ClientBehaviorHolder {

    public AjaxJsFunction() {
        super();
		setRendererType("com.aplos.core.component.jsfunction.AjaxJsFunction");
    }

    @Override
    public void encodeAll(FacesContext context) throws IOException {
    	
    	super.encodeAll(context);
    }

    @Override
    public String getFamily() {
    	return "com.aplos.core.component.AjaxJsFunction";
    }

    protected enum Properties {
    	eventname,
    	update,
    	process,
    	global,
    	async,
    	oncomplete,
    	onerror,
    	onsuccess,
    	onstart,
    	listener,
    	immediate,
    	disabled,
    	delay;
    }

    public MethodExpression getListener() {
    	MethodExpression value = (MethodExpression) getStateHelper().eval(Properties.listener);
        return value;
    }

    public void setListener(MethodExpression listener) {
        getStateHelper().put(Properties.listener, listener);
    }

    public String getEventName() {
    	String value = (String) getStateHelper().eval(Properties.eventname);
        return value;
    }

    public void setEventName(String eventname) {
        getStateHelper().put(Properties.eventname, eventname);
    }

    public String getOnstart() {
    	String value = (String) getStateHelper().eval(Properties.onstart);
        return value;
    }

    public void setOnstart(String onstart) {
        getStateHelper().put(Properties.onstart, onstart);
    }

    public String getOnsuccess() {
    	String value = (String) getStateHelper().eval(Properties.onsuccess);
        return value;
    }

    public void setOnsuccess(String onsuccess) {
        getStateHelper().put(Properties.onsuccess, onsuccess);
    }

    public String getOncomplete() {
    	String value = (String) getStateHelper().eval(Properties.oncomplete);
        return value;
    }

    public void setOncomplete(String oncomplete) {
        getStateHelper().put(Properties.oncomplete, oncomplete);
    }

    public String getOnerror() {
    	String value = (String) getStateHelper().eval(Properties.onerror);
        return value;
    }

    public void setOnerror(String onerror) {
        getStateHelper().put(Properties.onerror, onerror);
    }

    public String getProcess() {
    	String value = (String) getStateHelper().eval(Properties.process);
        return value;
    }

    public void setProcess(String process) {
        getStateHelper().put(Properties.process, process);
    }

    public String getUpdate() {
    	String value = (String) getStateHelper().eval(Properties.update);
        return value;
    }

    public void setUpdate(String update) {
        getStateHelper().put(Properties.update, update);
    }

    public boolean isDisabled() {
        Boolean value = (Boolean) getStateHelper().eval(Properties.disabled, false);
        return value;
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(Properties.disabled, disabled);
    }

    @Override
	public boolean isImmediate() {
        Boolean value = (Boolean) getStateHelper().eval(Properties.immediate, false);
        return value;
    }

    @Override
	public void setImmediate(boolean immediate) {
        getStateHelper().put(Properties.immediate, immediate);
    }

    public boolean isAsync() {
        Boolean value = (Boolean) getStateHelper().eval(Properties.async, false);
        return value;
    }

    public void setAsync(boolean async) {
        getStateHelper().put(Properties.async, async);
    }

    public boolean isGlobal() {
        Boolean value = (Boolean) getStateHelper().eval(Properties.global, false);
        return value;
    }

    public void setBypassUpdates(boolean global) {
        getStateHelper().put(Properties.global, global);
    }

    public String getDelay() {
    	String value = (String) getStateHelper().eval(Properties.delay);
        return value;
    }

    public void setDelay(String delay) {
        getStateHelper().put(Properties.delay, delay);
    }
}
