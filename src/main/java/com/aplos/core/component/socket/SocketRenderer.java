package com.aplos.core.component.socket;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;

import org.primefaces.renderkit.CoreRenderer;

import com.aplos.common.AplosUrl;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.SslProtocolEnum;
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
        AplosUrl aplosUrl = new AplosUrl(channelUrl);
        aplosUrl.setHost(Website.getCurrentWebsiteFromTabSession());
        if( JSFUtil.getRequest().getRequestURL().toString().startsWith( "https" ) ) {
        	aplosUrl.setScheme(SslProtocolEnum.FORCE_SSL);
        } else {
            aplosUrl.setScheme(SslProtocolEnum.FORCE_HTTP);
        }
        String clientId = socket.getClientId(context);

        WidgetBuilder wb = new WidgetBuilder(context);
        wb.initWithDomReady("Socket", socket.resolveWidgetVar(), clientId);
        
        wb.attr("url", aplosUrl.toString())
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
