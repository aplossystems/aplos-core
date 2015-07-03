package com.aplos.core.component.jsfunction;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.faces.component.FacesComponent;
import javax.faces.component.UICommand;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

@FacesComponent("com.aplos.core.component.jsfunction.JsFunction")
public class JsFunction extends UICommand implements ClientBehaviorHolder {

    public JsFunction() {
        super();
		setRendererType("com.aplos.core.component.jsfunction.JsFunction");
    }

    @Override
    public void encodeAll(FacesContext context) throws IOException {
    	
    	super.encodeAll(context);
    }

    @Override
    public String getFamily() {
    	return "com.aplos.core.component.JsFunction";
    }

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(
        "beforedomupdate",
        "complete",
        "begin"
        ));

    @Override
	public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
	public String getDefaultEventName() {
        return null;
    }

    protected enum Properties {
        bypassUpdates,
        data,
        execute,
        limitRender,
        name,
        onbeforedomupdate,
        onbegin,
        oncomplete,
        render,
        status
    }

    public boolean isBypassUpdates() {
        Boolean value = (Boolean) getStateHelper().eval(Properties.bypassUpdates, false);
        return value;
    }

    public void setBypassUpdates(boolean bypassUpdates) {
        getStateHelper().put(Properties.bypassUpdates, bypassUpdates);
    }


    public Object getData() {
        Object value = getStateHelper().eval(Properties.data);
        return value;
    }

    public void setData(Object data) {
        getStateHelper().put(Properties.data, data);
    }


    public Object getExecute() {
        Object value = getStateHelper().eval(Properties.execute);
        return value;
    }

    public void setExecute(Object execute) {
        getStateHelper().put(Properties.execute, execute);
    }


    public boolean isLimitRender() {
        Boolean value = (Boolean) getStateHelper().eval(Properties.limitRender, false);
        return value;
    }

    public void setLimitRender(boolean limitRender) {
        getStateHelper().put(Properties.limitRender, limitRender);
    }


    public String getName() {
        String value = (String) getStateHelper().eval(Properties.name);
        return value;
    }

    public void setName(String name) {
        getStateHelper().put(Properties.name, name);
    }


    public String getOnbeforedomupdate() {
        String value = (String) getStateHelper().eval(Properties.onbeforedomupdate);
        return value;
    }

    public void setOnbeforedomupdate(String onbeforedomupdate) {
        getStateHelper().put(Properties.onbeforedomupdate, onbeforedomupdate);
    }


    public String getOnbegin() {
        String value = (String) getStateHelper().eval(Properties.onbegin);
        return value;
    }

    public void setOnbegin(String onbegin) {
        getStateHelper().put(Properties.onbegin, onbegin);
    }


    public String getOncomplete() {
        String value = (String) getStateHelper().eval(Properties.oncomplete);
        return value;
    }

    public void setOncomplete(String oncomplete) {
        getStateHelper().put(Properties.oncomplete, oncomplete);
    }


    public Object getRender() {
        Object value = getStateHelper().eval(Properties.render);
        return value;
    }

    public void setRender(Object render) {
        getStateHelper().put(Properties.render, render);
    }


    public String getStatus() {
        String value = (String) getStateHelper().eval(Properties.status);
        return value;
    }

    public void setStatus(String status) {
        getStateHelper().put(Properties.status, status);
    }



}
