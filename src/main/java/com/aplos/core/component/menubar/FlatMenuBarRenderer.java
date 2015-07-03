package com.aplos.core.component.menubar;

import java.io.IOException;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import com.aplos.common.BackingPageUrl;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.enums.TabActionType;
import com.aplos.common.listeners.PageBindingPhaseListener;
import com.aplos.common.tabpanels.MenuTab;
import com.aplos.common.tabpanels.SiteTabPanel;
import com.aplos.common.tabpanels.TabPanel;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;

@FacesRenderer(componentFamily = "com.aplos.core.component.menubar.DynamicMenuBar", rendererType = "com.aplos.core.component.menubar.FlatMenuBarRenderer")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class FlatMenuBarRenderer extends Renderer implements DynamicMenuBarRendererInter {
	public static final String RENDERER_TYPE = "com.aplos.core.component.menubar.FlatMenuBarRenderer";

	@Override
	public void updateComponentVariables( DynamicMenuBar menuBar ) {}

	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent component) throws IOException {
		DynamicMenuBar menuBar = (DynamicMenuBar) component;
		String theme = ComponentUtil.getTheme( facesContext );

		if (menuBar.getTabPanel() != null) {
			/*
			 * Don't cache the tabPanels as they won't be recreated on Ajax and sometimes
			 * the menu options change.
			 */
//			if( menuBar.getFacet("tabListFacet") == null ) {
				if( menuBar.getTabPanel().isVertical() ) {
					createVerticalMenuList(facesContext, theme, menuBar );
				} else {
					createHorizontalMenuList(facesContext, theme, menuBar );
				}
//			}

			menuBar.getFacet("tabListFacet").encodeAll(facesContext);
		}
	}

	@Override
	public void createHorizontalMenuList( FacesContext facesContext, String theme, DynamicMenuBar menuBar ) {
		TabPanel thisTabPanel = menuBar.getTabPanel();
		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();

		List<MenuTab> tabList = JSFUtil.getAplosRequestContext().getProcessedMenuTabs(thisTabPanel );

		//process as normal now
		javax.faces.component.html.
		HtmlPanelGroup tabDiv = new HtmlPanelGroup();
		tabDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
		tabDiv.setStyleClass("aplosMainMenu");

		for (int i = 0; i < tabList.size(); i++) {
			MenuTab menuTab = tabList.get( i );

			HtmlPanelGroup linkDiv = new HtmlPanelGroup();
			linkDiv.setLayout( "block" );
			linkDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "linkDiv" ) );
			linkDiv.setStyleClass(getMenuItemStyleClass( menuTab ));


			if( menuTab.isOutputLink() ) {
				HtmlOutputLink link = new HtmlOutputLink();
				link.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
				link.setStyleClass( "stdLink" );
				link.setValue( menuTab.getLinkValue() ); //menuTab.determineTabAction() );
				link.setTarget( menuTab.getTarget() );
				HtmlOutputText menuTabText = new HtmlOutputText();
				menuTabText.setValue( menuTab.getInternationalisedText() );

				link.getChildren().add( menuTabText );
				linkDiv.getChildren().add( link );

			} else {

				HtmlCommandLink link = new HtmlCommandLink();
				link.setId( ComponentUtil.getUniqueId( menuBar.getMenuHierarchyType().name(), "link" ) );
				if (thisTabPanel instanceof SiteTabPanel) { //TODO: port to tabbed renderer
					link.setStyle("display:block;");
				}
				HtmlOutputText linkText = new HtmlOutputText(); //TODO: port to tabbed renderer
				linkText.setEscape(false);
				//linkText.setStyle("float: left;padding-left: 10px;");
				linkText.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "linkText" ) );
				linkText.setValue( menuTab.getInternationalisedText() );
				//link.setValue( linkText );
				link.getChildren().add(linkText);
				boolean clearDown = true;
				if (thisTabPanel.isLinkedToBean()) {
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

				link.setActionExpression( expf.createMethodExpression(facesContext.getELContext(), linkAction, null, new Class[] {}) );
				link.setImmediate( true );
				linkDiv.getChildren().add( link );
			}


			tabDiv.getChildren().add(linkDiv);
		}

		menuBar.getFacets().put( "tabListFacet", tabDiv );
	}

	@Override
	public void decode(FacesContext context, UIComponent component) {
		
		super.decode(context, component);
	}

	@Override
	public void createVerticalMenuList( FacesContext facesContext, String theme, DynamicMenuBar menuBar ) {
		/***
		 *
		 * commented out code not tested yet
		 *
		 */

//		TabPanel tabPanel = menuBar.getTabPanel();
//		List<MenuTab> tabList = tabPanel.getTabList();
//		HtmlPanelGroup tabDiv = new HtmlPanelGroup();
//		tabDiv.setLayout( "block" );
//		tabDiv.setStyleClass( "verticalTabPanel" );
//		tabDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "sideSubMenuDiv" ) );
//		tabDiv.setStyle( "position:absolute;width:" + String.valueOf( tabPanel.getVerticalWidth() ) + "px;left:-" + String.valueOf( tabPanel.getVerticalWidth() - 10 ) + "px;top:40px;" );
//
//		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
//
//		String frameColourPixelUrl = ComponentUtil.getImageUrlWithTheme( facesContext, "frameColourPixel.png" );
//		for (int i = 0; i < tabList.size(); i++) {
//			MenuTab tab = tabList.get( i );
//
//			HtmlCommandLink link = new HtmlCommandLink();
//			link.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
//			link.setStyleClass( "stdLink" );
//			link.setValue( tab.getDisplayName() );
//			link.setActionExpression( expf.createMethodExpression(facesContext.getELContext(), tab.deteremineTabAction(), null, new Class[] {}) );
//			link.setImmediate( true );
//
//			tabDiv.getChildren().add( link );
//		}

//		menuBar.getFacets().put( "tabListFacet", tabDiv );
	}


	public String getMenuItemStyleClass( MenuTab menuTab  ) {
		BackingPage backingPage = null;
		if( menuTab.determineTabAction() instanceof BackingPageUrl ) {
			backingPage	= PageBindingPhaseListener.resolveBackingPage( ((BackingPageUrl) menuTab.determineTabAction()).getBackingPageClass() );
		}

		if( menuTab.isSelected() ) {
			return "current";
		} else if( backingPage != null && backingPage.isTabHighlighted() ) {
			return "highlight";
		} else {
			return "menulink";
		}
	}

}
