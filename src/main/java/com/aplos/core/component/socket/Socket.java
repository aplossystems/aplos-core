package com.aplos.core.component.socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.render.Renderer;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="components", name="components.js"),
	@ResourceDependency(library="scripts", name="push.js")
})
@FacesComponent("com.aplos.core.component.socket.Socket")
public class Socket extends UIComponentBase implements javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "com.aplos.core.component.Socket";
	public static final String COMPONENT_FAMILY = "com.aplos.core.component.Socket";
	private static final String DEFAULT_RENDERER = "com.aplos.core.component.socket.SocketRenderer";

	protected enum PropertyKeys {

		widgetVar
		,channel
		,transport
		,fallbackTransport
		,onMessage
		,onError
		,onClose
		,onOpen
		,onReconnect
		,onMessagePublished
		,onTransportFailure
		,onLocalMessage
		,autoConnect;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Socket() {
		setRendererType(DEFAULT_RENDERER);
	}
	
	@Override
	protected Renderer getRenderer(FacesContext context) {
		// TODO Auto-generated method stub
		return super.getRenderer(context);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
	}

	public java.lang.String getChannel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.channel, null);
	}
	public void setChannel(java.lang.String _channel) {
		getStateHelper().put(PropertyKeys.channel, _channel);
	}

	public java.lang.String getTransport() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.transport, "websocket");
	}
	public void setTransport(java.lang.String _transport) {
		getStateHelper().put(PropertyKeys.transport, _transport);
	}

	public java.lang.String getFallbackTransport() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.fallbackTransport, "long-polling");
	}
	public void setFallbackTransport(java.lang.String _fallbackTransport) {
		getStateHelper().put(PropertyKeys.fallbackTransport, _fallbackTransport);
	}

	public java.lang.String getOnMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onMessage, null);
	}
	public void setOnMessage(java.lang.String _onMessage) {
		getStateHelper().put(PropertyKeys.onMessage, _onMessage);
	}

	public java.lang.String getOnError() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onError, null);
	}
	public void setOnError(java.lang.String _onError) {
		getStateHelper().put(PropertyKeys.onError, _onError);
	}

	public java.lang.String getOnClose() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onClose, null);
	}
	public void setOnClose(java.lang.String _onClose) {
		getStateHelper().put(PropertyKeys.onClose, _onClose);
	}

	public java.lang.String getOnOpen() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onOpen, null);
	}
	public void setOnOpen(java.lang.String _onOpen) {
		getStateHelper().put(PropertyKeys.onOpen, _onOpen);
	}

	public java.lang.String getOnReconnect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onReconnect, null);
	}
	public void setOnReconnect(java.lang.String _onReconnect) {
		getStateHelper().put(PropertyKeys.onReconnect, _onReconnect);
	}

	public java.lang.String getOnMessagePublished() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onMessagePublished, null);
	}
	public void setOnMessagePublished(java.lang.String _onMessagePublished) {
		getStateHelper().put(PropertyKeys.onMessagePublished, _onMessagePublished);
	}

	public java.lang.String getOnTransportFailure() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTransportFailure, null);
	}
	public void setOnTransportFailure(java.lang.String _onTransportFailure) {
		getStateHelper().put(PropertyKeys.onTransportFailure, _onTransportFailure);
	}

	public java.lang.String getOnLocalMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onLocalMessage, null);
	}
	public void setOnLocalMessage(java.lang.String _onLocalMessage) {
		getStateHelper().put(PropertyKeys.onLocalMessage, _onLocalMessage);
	}

	public boolean isAutoConnect() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoConnect, true);
	}
	public void setAutoConnect(boolean _autoConnect) {
		getStateHelper().put(PropertyKeys.autoConnect, _autoConnect);
	}


    private final static String DEFAULT_EVENT = "message";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("message"));

    private Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }
	public String resolveWidgetVar() {
		FacesContext context = getFacesContext();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null)
			return userWidgetVar;
		 else
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
	}
}
