package components;
import java.io.IOException;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.IssueReportedPage;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.backingpage.SessionTimeoutPage;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.tabpanels.NavigationStack;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

public class controlPanel extends UINamingContainer {

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin(facesContext);
	}

	public boolean isEditButtonsValid() {
		BackingPage backingPage = JSFUtil.getCurrentBackingPage();
		if (backingPage != null && backingPage instanceof EditPage) {
			return true;
		}
		return false;
	}

	public boolean isEditActionButtonsValid() {
		BackingPage backingPage = JSFUtil.getCurrentBackingPage();
		if (backingPage != null && backingPage.isShowingEditActionButtons() ) {
			if( backingPage.getBeanDao() != null ) {
				return true;
			}
		}
		return false;
	}

	public String getEmailRedirectStr() {
		if (ApplicationUtil.getAplosContextListener().isDebugMode()) {
			return AplosEmail.getDivertEmailAddress();
		} else {
			return "<em>Not active (Not in Debug mode)</em>";
		}
	}

	public boolean isListButtonsValid() {
		BackingPage backingPage = JSFUtil.getCurrentBackingPage();
		if (backingPage != null && backingPage instanceof ListPage) {
			return true;
		} else if (backingPage instanceof SessionTimeoutPage || backingPage instanceof IssueReportedPage) {
			return true;
		}
		return false;
	}
	
	public boolean isShowListBackButton() {
		NavigationStack navStack = JSFUtil.getNavigationStack();
		if (navStack != null && navStack.size() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public void listBackButtonAction() {
		NavigationStack navStack = JSFUtil.getNavigationStack();
		navStack.navigateBack();
	}
	
}
