package components;
import java.util.ArrayList;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PostRestoreStateEvent;

import com.aplos.common.enums.TabActionType;
import com.aplos.common.tabpanels.MenuTab;
import com.aplos.common.tabpanels.TabPanel;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;


public class menubar extends UINamingContainer {
	
	public List<MenuTab> getMenuTabs() {
		TabPanel tabPanel = (TabPanel) getAttributes().get( "tabPanel" );
		List<MenuTab> tabList;
		if( tabPanel != null ) {
			tabList = JSFUtil.getAplosRequestContext().getProcessedMenuTabs(tabPanel);
		} else {
			tabList = new ArrayList<MenuTab>();
		}
		return tabList;
	}
	
	@Override
	public void processEvent(ComponentSystemEvent event)
			throws AbortProcessingException {
		super.processEvent(event);
		if( event instanceof PostRestoreStateEvent ) {
			JSFUtil.getAplosRequestContext().determineTabPanelState();
		}
	}
	
	public void linkAction() {
		MenuTab menuTab = JSFUtil.getBeanFromRequest( "menuTab" );
		boolean clearDown = true;
		TabPanel tabPanel = (TabPanel) getAttributes().get( "tabPanel" );
		if (tabPanel.isLinkedToBean()) {
			clearDown = false;
		}
		String linkAction;
		if (TabActionType.COMMAND_LINK.equals( menuTab.getTabActionType() )) { 
			linkAction = "#{" + menuTab.getLinkAction() + "}";
		} else if (menuTab.getHasEffectiveEl()) { //TODO: port to tabbed rendered
			linkAction = menuTab.getEffectiveTabAction();
		} else {
			linkAction = "#{viewUtil.menuRedirect('" + menuTab.determineTabAction() + "', " + clearDown + ", true, " + menuTab.getId() + ")}";
		}
		
		FacesContext facesContext = JSFUtil.getFacesContext();
		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
		MethodExpression methodExpression = expf.createMethodExpression(facesContext.getELContext(), linkAction, null, new Class[] {});
		try {
			methodExpression.invoke(facesContext.getELContext(), new Object[0] );
		} catch( MethodNotFoundException mnfex ) {
			ApplicationUtil.handleError( mnfex );
		}
	}


	public String getMenuItemStyleClass() {
		MenuTab menuTab = JSFUtil.getBeanFromRequest( "menuTab" );
		if( menuTab.isSelected() ) {
			return "current";
		} else {
			return "menulink";
		}
	}
}
