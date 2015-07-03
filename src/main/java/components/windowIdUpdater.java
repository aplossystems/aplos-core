package components;
import java.io.IOException;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.AplosUrl;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="scripts", name="windowIdUpdater.js"),
	@ResourceDependency(library="scripts", name="aploscommon.js")
})
public class windowIdUpdater extends UINamingContainer {

	public windowIdUpdater() {
		setRendered( CommonConfiguration.getCommonConfiguration().isUsingWindowId() );
	}
	
	@Override
	public void encodeAll(FacesContext context) throws IOException {
		super.encodeAll(context);
	}

	@Override
	public void processDecodes(FacesContext context) {
		
		super.processDecodes(context);

		JSFUtil.getExternalContext().getRequestParameterNames();
		if( JSFUtil.getRequestParameter( getClientId() + ":windowIdUpdater" ) != null ) {
			Integer currentWindowId = null;
			try {
				currentWindowId = Integer.parseInt( JSFUtil.getRequestParameter( AplosAppConstants.WINDOW_ID ) );
			} catch( NumberFormatException nfEx ) {
				/*
				 * It seems that strings are getting inserted into the windowId somehow, I'm
				 * not sure if this is due to applications trying to hack into the interface but
				 * it happens quite often, normally with either _blank or e_1CmW 
				 */
//				ApplicationUtil.getAplosContextListener().handleError( nfEx );
				return;
			}
			JSFUtil.getRequest().setAttribute( AplosAppConstants.WINDOW_ID, new String[] { String.valueOf( currentWindowId ) } );
			
			// This moves any attributes that were put into the TabSession before the windowId was set
			// into the new TabSession
			Map<String,Object> nullIdTabSessionMap = JSFUtil.getTabSession(JSFUtil.getSessionTemp(), true, false, null );
			Map<String,Object> windowIdTabSessionMap = JSFUtil.getTabSession(JSFUtil.getSessionTemp(), true, false, String.valueOf( currentWindowId ) );
			for( String tempKey : nullIdTabSessionMap.keySet() ) {
				windowIdTabSessionMap.put( tempKey, nullIdTabSessionMap.get( tempKey ) );
			}
			nullIdTabSessionMap.clear();

			if (JSFUtil.getFromFlashViewMap(AplosScopedBindings.EXTERNAL_LOGIN_REDIRECTION_URL) != null) {
				JSFUtil.addToFlashViewMap(AplosScopedBindings.EXTERNAL_LOGIN_REDIRECTION_URL, JSFUtil.getFromFlashViewMap(AplosScopedBindings.EXTERNAL_LOGIN_REDIRECTION_URL));
			}
			String redirectionUrl = (String) JSFUtil.getSessionTemp().getAttribute( AplosScopedBindings.WINDOW_ID_REDIRECTION_URL );
			if( redirectionUrl != null ) {
				JSFUtil.redirect( new AplosUrl(redirectionUrl), false );
				JSFUtil.getSessionTemp().setAttribute( AplosScopedBindings.WINDOW_ID_REDIRECTION_URL, null );
			}
		}
	}
}
