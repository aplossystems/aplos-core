package com.aplos.core.component.socket;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;

import org.primefaces.renderkit.CoreRenderer;

import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.utils.WidgetBuilder;

@FacesRenderer(componentFamily="com.aplos.core.component.Socket",rendererType="com.aplos.core.component.socket.SocketRenderer")
public class SocketRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Socket socket = (Socket) component;
        String channel = socket.getChannel();
        String channelUrl = "aplospush" + channel;
        String url = getResourceURL(context, channelUrl);
        String pushServer = ApplicationUtil.getAplosContextListener().getServerUrl();
        String contextPath = JSFUtil.getContextPath().replaceFirst( "/", "" ); 
        if( contextPath.length() > 0 ) {
        	pushServer = pushServer + contextPath + "/"; 
        }
        pushServer = "http://localhost:8080/altrui/";
        String clientId = socket.getClientId(context);
        
        if(pushServer != null) {
            url = pushServer + url;
        }

        WidgetBuilder wb = new WidgetBuilder(context);
        wb.initWithDomReady("Socket", socket.resolveWidgetVar(), clientId);
        
        wb.attr("url", url)
        	.attr("autoConnect", socket.isAutoConnect())
        	.attr("transport", socket.getTransport())
        	.attr("fallbackTransport", socket.getFallbackTransport())
        	.callback("onMessage", socket.getOnMessage())
        	.callback("onError", "function(response)", socket.getOnError())
        	.callback("onClose", "function(response)", socket.getOnClose())
        	.callback("onOpen", "function(response)", socket.getOnOpen())
        	.callback("onReconnect", "function(response)", socket.getOnReconnect())
        	.callback("onMessagePublished", "function(response)", socket.getOnMessagePublished())
        	.callback("onTransportFailure", "function(response, request)", socket.getOnTransportFailure())
        	.callback("onLocalMessage", "function(response)", socket.getOnLocalMessage());

        encodeClientBehaviors(context, socket);

        wb.finish();
    }
}
