package components;
import java.io.File;
import java.io.IOException;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;

import com.aplos.common.backingpage.ErrorCheckPage;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

public class loggedInUserControls extends UINamingContainer {
	
	@Override
	public void encodeAll(FacesContext arg0) throws IOException {
//		CommonUtil.timeTrial( "loggedInUserControls started" );
		super.encodeAll(arg0);
	}

	public void errorCheck() {
		JSFUtil.redirect( ErrorCheckPage.class );
	}

	public String clearCache() {
		//this is EhCache specific
//		CacheManager.getInstance().clearAll();
//		JSFUtil.addMessage("EH Cache manager instance cleared.");
		//this might be better for the above: http://java.dzone.com/tips/clearing-hibernate-second-leve
		JSFUtil.getHistoryList().clear();
		JSFUtil.addMessage("History list cleared.");
		JSFUtil.getNavigationStack().clear();
		JSFUtil.addMessage("Navigation stack cleared.");
		ApplicationUtil.getPersistenceContext().getBeanMap().clear();
		try {
			FileUtils.cleanDirectory( new File( CommonWorkingDirectory.PROCESSED_RESOURCES_DIR.getDirectoryPath(true) ) );
			FileUtils.cleanDirectory( new File( CommonWorkingDirectory.COMBINED_RESOURCES.getDirectoryPath(true) ) );
		} catch( IOException ioex ) {
			ApplicationUtil.handleError(ioex);
		}
		return null;
	}

}
