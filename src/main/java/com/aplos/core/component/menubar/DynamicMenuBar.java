package com.aplos.core.component.menubar;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PostRestoreStateEvent;

import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.appconstants.ComponentConstants;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.tabpanels.TabPanel;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;

@FacesComponent("com.aplos.core.component.menubar.DynamicMenuBar")
public class DynamicMenuBar extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.menubar.DynamicMenuBar";
	public static final String TAB_PANEL_ATTRIBUTE_KEY = "tabPanel";
	public static final String HIERARCY_TYPE_ATTRIBUTE_KEY = "hierarchyType";
	private TabPanel tabPanel;

	private MenuHierarchyType menuHierarchyType;
	private Object[] _state = null;
	int instanceCount = 0;
	private boolean componentsUpdated = false;

	public enum MenuHierarchyType {
		SITE_TAB_PANEL,
		MAIN_TAB_PANEL,
		SUB_TAB_PANEL;
	}

	@Override
	public void encodeBegin( FacesContext facesContext ) throws IOException {
		if( this.getAttributes().get( ComponentConstants.RENDERED_ATTRIBUTE_KEY ).equals( "false" ) ) {
			this.setRendered( false );
		} else {
			this.setRendered( true );
		}
		if( !componentsUpdated ) {
			menuHierarchyType = MenuHierarchyType.valueOf( (String) getAttributes().get( HIERARCY_TYPE_ATTRIBUTE_KEY ) );
			updateComponentVariables(facesContext);
		}
		super.encodeBegin(facesContext);
	}
	
	@Override
	public void processEvent(ComponentSystemEvent event)
			throws AbortProcessingException {
		super.processEvent(event);
		if( event instanceof PostRestoreStateEvent ) {
			JSFUtil.getAplosRequestContext().determineTabPanelState();
		}
	}

	public void updateComponentVariables(FacesContext facesContext) {
		componentsUpdated = true;
		AplosContextListener aplosContextListener = (AplosContextListener) JSFUtil.getServletContext().getAttribute( AplosScopedBindings.CONTEXT_LISTENER );

		if ( menuHierarchyType.equals( MenuHierarchyType.SITE_TAB_PANEL ) ) {
			tabPanel = aplosContextListener.getSiteTabPanel();
		} else if ( menuHierarchyType.equals( MenuHierarchyType.MAIN_TAB_PANEL ) ) {
			tabPanel = JSFUtil.getAplosRequestContext().getMainTabPanel();
		} else {
			tabPanel = JSFUtil.getAplosRequestContext().getSubTabPanel();
		}

		DynamicMenuBarRendererInter menuBarRendererInter = (DynamicMenuBarRendererInter) getRenderer(facesContext);
		if ( tabPanel != null && menuBarRendererInter != null ) {
			menuBarRendererInter.updateComponentVariables( this );
		}
	}

	public TabPanel getTabPanel() {
		return tabPanel;
	}

	public void setTabPanel( TabPanel tabPanel ) {
		this.tabPanel = tabPanel;
	}

	@Override
	public void restoreState( FacesContext _context, Object _state ) {
		this._state = (Object[])_state;
		super.restoreState( _context, this._state[ 0 ] );
		ComponentUtil.addStateToAttributes( this, ComponentConstants.STYLE_ATTRIBUTE_KEY, this._state[1] );
	}

	@Override
	public Object saveState( FacesContext _context ) {
		if( _state == null ) {
			_state = new Object[ 2 ];
		}
		_state[ 0 ] = super.saveState( _context );
		_state[ 1 ] = this.getAttributes().get(ComponentConstants.STYLE_ATTRIBUTE_KEY);
		return _state;
	}

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

	public void setMenuHierarchyType( FacesContext facesContext, MenuHierarchyType tabPanelHierarchyType ) {
		this.menuHierarchyType = tabPanelHierarchyType;
		updateComponentVariables(facesContext);
	}

	public MenuHierarchyType getMenuHierarchyType() {
		return menuHierarchyType;
	}

}