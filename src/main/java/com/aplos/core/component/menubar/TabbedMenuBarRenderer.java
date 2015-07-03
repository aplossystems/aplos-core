package com.aplos.core.component.menubar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.lookups.UserLevel;
import com.aplos.common.listeners.PageBindingPhaseListener;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.tabpanels.MenuTab;
import com.aplos.common.tabpanels.SiteTabPanel;
import com.aplos.common.tabpanels.TabPanel;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.component.menubar.DynamicMenuBar.MenuHierarchyType;

@FacesRenderer(componentFamily = "com.aplos.core.component.menubar.DynamicMenuBar", rendererType = "com.aplos.core.component.menubar.TabbedMenuBarRenderer")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class TabbedMenuBarRenderer extends Renderer implements DynamicMenuBarRendererInter {
	public static final String RENDERER_TYPE = "com.aplos.core.component.menubar.TabbedMenuBarRenderer";
	private String imgUrlTabsFarLeftUp;
	private String imgUrlTabsFarLeftDown;
	private String imgUrlSubTabFarRightUp;
	private String imgUrlSubTabFarRightDown;
	private String imgUrlSubTabLeftUp;
	private String imgUrlSubTabRightUp;
	private String imgUrlSubTabBothDown;
	private String imgUrlTabExtenderUp;
	private String imgUrlTabExtenderDown;

	private String tabDivStyleClass;

	@Override
	public void updateComponentVariables( DynamicMenuBar menuBar ) {
		if( menuBar.getMenuHierarchyType().equals( MenuHierarchyType.MAIN_TAB_PANEL ) ) {
			imgUrlTabsFarLeftUp = "tabsFarLeftUp.png";
			imgUrlTabsFarLeftDown = "tabsFarLeftDown.png";
			tabDivStyleClass = "horizontalTabbedMenu";
			imgUrlSubTabFarRightUp = "tabsFarRightUp.png";
			imgUrlSubTabFarRightDown = "tabsFarRightDown.png";
			imgUrlSubTabLeftUp = "tabsLeftUp.png";
			imgUrlSubTabRightUp = "tabsRightUp.png";
			imgUrlSubTabBothDown = "tabsBothDown.png";
			imgUrlTabExtenderUp = "tabExtenderUp";
			imgUrlTabExtenderDown = "tabExtenderDown";
		} else {
			imgUrlTabsFarLeftUp = "subTabFarLeftUp.png";
			imgUrlTabsFarLeftDown = "subTabFarLeftDown.png";
			tabDivStyleClass = "horizontalTabbedMenu";
			imgUrlSubTabFarRightUp = "subTabFarRightUp.png";
			imgUrlSubTabFarRightDown = "subTabFarRightDown.png";
			imgUrlSubTabLeftUp = "subTabLeftUp.png";
			imgUrlSubTabRightUp = "subTabRightUp.png";
			imgUrlSubTabBothDown = "subTabBothDown.png";
			imgUrlTabExtenderUp = "subTabExtenderUp";
			imgUrlTabExtenderDown = "subTabExtenderDown";
		}
	}

	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent component)
			throws IOException {
		DynamicMenuBar menuBar = (DynamicMenuBar) component;
		String theme = ComponentUtil.getTheme( facesContext );

		if (menuBar.getTabPanel() != null) {
			if( menuBar.getTabPanel().isVertical() ) {
				if (menuBar.getFacet("tabListFacet") == null) {
					createVerticalMenuList(facesContext, theme, menuBar );
				}

			} else {
				if (menuBar.getFacet("tabListFacet") == null) {
					createHorizontalMenuList(facesContext, theme, menuBar );
				}
			}

			menuBar.getFacet("tabListFacet").encodeAll(facesContext);
		}
	}

	public static String getVerticalTabLeftImagePath( FacesContext facesContext, String theme, List<MenuTab> menuTabs, int index ) {
		String imagePath;
		if( index == menuTabs.size() - 1 ) {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabLeftBottom.png");
		} else {
			if( menuTabs.get( index + 1 ).isSelected() ) {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabLeftBelowUp.png" );
			} else {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabLeftAboveUp.png" );
			}
		}

		return imagePath;
	}

	@Override
	public void createHorizontalMenuList( FacesContext facesContext, String theme, DynamicMenuBar menuBar ) {

		TabPanel thisTabPanel = menuBar.getTabPanel();
		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
		List<MenuTab> tabList = thisTabPanel.getProcessedTabList();
		

		//process as normal now
		final String firstTabImage = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext,
				(tabList.size() == 0 || tabList.get(0).isSelected() ? imgUrlTabsFarLeftUp : imgUrlTabsFarLeftDown ) );

		HtmlPanelGroup menuDiv = new HtmlPanelGroup();
		menuDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
		menuDiv.setStyleClass("tabPanel");
		menuDiv.setStyleClass( tabDivStyleClass );

		HtmlGraphicImage farLeftImage = new HtmlGraphicImage();
		farLeftImage.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
		farLeftImage.setValue( firstTabImage );
		farLeftImage.setStyle( "float:left;" );

		for (int i = 0; i < tabList.size(); i++) {
			MenuTab tab = tabList.get( i );
			if (tab.determineTabAction() != null) {
				HtmlCommandLink link = new HtmlCommandLink();
				link.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
				link.setStyleClass( getTabStyle( tab ) );
				if (CommonConfiguration.getCommonConfiguration().getIsInternationalizedApplication()) {
					//internationalised - the tab panel 'names' should actually be resource bundle keys
					//start with a _ for safety so we can mix/match... and not break anything legacy
					if (tab.getDisplayName().startsWith("_")) {
						link.setValue( CommonUtil.translate(tab.getDisplayName()) );
					} else {
						link.setValue( tab.getDisplayName() );
					}
				} else {
					link.setValue( tab.getDisplayName() );
				}
				boolean clearDown = true;
				if (thisTabPanel.isLinkedToBean()) { //newbeantabpanels
					clearDown = false;
				}
				link.setActionExpression( expf.createMethodExpression(facesContext.getELContext(), "#{viewUtil.menuRedirect('" + tab.determineTabAction() + "', " + clearDown + ", true, " + tab.getId() + ")}", null, new Class[] {}) );
				link.setImmediate( true );

				HtmlPanelGroup tabDiv = new HtmlPanelGroup();
				tabDiv.setStyleClass( "aplosCompTabContentContainer" );
				tabDiv.setLayout( "block" );
				tabDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );

				if (i==0) {
					tabDiv.getChildren().add( farLeftImage );
				} else {
					HtmlGraphicImage leftImage = new HtmlGraphicImage();
					leftImage.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
					leftImage.setValue( getMenuTabImagePath( facesContext, theme, tabList, i, true ) );
					leftImage.setStyle( "float:left;" );
					tabDiv.getChildren().add( leftImage );
				}

				HtmlGraphicImage rightImage = new HtmlGraphicImage();
				rightImage.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
				rightImage.setValue( getMenuTabImagePath( facesContext, theme, tabList, i, false ) );
				rightImage.setStyle( "float:left;" );

				tabDiv.getChildren().add( link );
				tabDiv.getChildren().add( rightImage );

				menuDiv.getChildren().add(tabDiv);
			}
		}

		menuBar.getFacets().put( "tabListFacet", menuDiv );
	}

	@Override
	public void createVerticalMenuList( FacesContext facesContext, String theme, DynamicMenuBar menuBar ) {
		TabPanel tabPanel = menuBar.getTabPanel();
		List<MenuTab> tabList = JSFUtil.getAplosRequestContext().getProcessedMenuTabs( tabPanel );
		HtmlPanelGroup tabDiv = new HtmlPanelGroup();
		tabDiv.setLayout( "block" );
		tabDiv.setStyleClass( "verticalTabPanel" );
		tabDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "sideSubMenuDiv" ) );
		tabDiv.setStyle( "position:absolute;width:" + String.valueOf( tabPanel.getVerticalWidth() ) + "px;left:-" + String.valueOf( Integer.valueOf(tabPanel.getVerticalWidth()) - 10 ) + "px;top:40px;" );

		String topMiddleImageUrl = ComponentUtil.getImageUrlWithTheme( facesContext, "sideSubTabMiddleTop.png" );
		HtmlPanelGroup topLayerDiv = new HtmlPanelGroup();
		topLayerDiv.setLayout( "block" );
		topLayerDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "topLayer" ) );
		topLayerDiv.setStyle( "height:22px;background-image:url('" + topMiddleImageUrl + "');margin-left:22px" );

		HtmlGraphicImage topLeftImage = new HtmlGraphicImage();
		topLeftImage.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "topLeft" ) );
		topLeftImage.setValue( ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabLeftTop.png") );
		topLeftImage.setStyle( "float:left;margin-left:-22px" );


		final String topRightImageUrl = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext,
						(tabList.get(0).isSelected() ? "sideSubTabRightTopUp.png" : "sideSubTabRightTopDown.png") );
		HtmlGraphicImage topRightImage = new HtmlGraphicImage();
		topRightImage.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "topRight" ) );
		topRightImage.setValue( topRightImageUrl );
		topRightImage.setStyle( "float:right" );

		topLayerDiv.getChildren().add( topLeftImage );
		topLayerDiv.getChildren().add( topRightImage );
		tabDiv.getChildren().add( topLayerDiv );

		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();

		String leftMiddleImageUrl = ComponentUtil.getImageUrlWithTheme( facesContext, "sideSubTabLeftMiddle.png" );
		String frameColourPixelUrl = ComponentUtil.getImageUrlWithTheme( facesContext, "frameColourPixel.png" );
		for (int i = 0; i < tabList.size(); i++) {
			MenuTab menuTab = tabList.get( i );

			HtmlPanelGroup middleLayerLeftDiv = new HtmlPanelGroup();
			middleLayerLeftDiv.setLayout( "block" );
			middleLayerLeftDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "middleLayerLeft" ) );
			middleLayerLeftDiv.setStyle( "background-image:url('" + leftMiddleImageUrl + "');background-repeat:repeat-y;" );

			String middleRightImageUrl = ComponentUtil.getImageUrlWithTheme( facesContext,
					(tabList.get(i).isSelected() ? "sideSubTabRightMiddleUp.png" : "sideSubTabRightMiddleDown.png") );
			HtmlPanelGroup middleLayerRightDiv = new HtmlPanelGroup();
			middleLayerRightDiv.setLayout( "block" );
			middleLayerRightDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "middleLayerRight" ) );
			middleLayerRightDiv.setStyle( "background-image:url('" + middleRightImageUrl + "');background-repeat:repeat-y;background-position:100% 0%" );

			HtmlPanelGroup middleLayerMiddleDiv = new HtmlPanelGroup();
			middleLayerMiddleDiv.setLayout( "block" );
			middleLayerMiddleDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "middleLayerMiddle" ) );
			middleLayerMiddleDiv.setStyle( "background-image:url('" + frameColourPixelUrl + "');margin-left:8px;margin-right:10px;padding-right:10px;margin-top:-4px;margin-bottom:-2px" );

			if( menuTab.isOutputLink() ) {
				HtmlOutputLink link = new HtmlOutputLink();
				link.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
				link.setStyleClass( "stdLink" );
				link.setValue( menuTab.determineTabAction() );
				link.setTarget( "blank" );
				HtmlOutputText menuTabText = new HtmlOutputText();
				menuTabText.setValue( menuTab.getDisplayName() );
				link.getChildren().add( menuTabText );
				middleLayerMiddleDiv.getChildren().add( link );
			} else {
				HtmlCommandLink link = new HtmlCommandLink();
				link.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), menuBar.getId() ) );
				link.setStyleClass( "stdLink" );
				link.setValue( menuTab.getDisplayName() );

				//link.setActionExpression( expf.createMethodExpression(facesContext.getELContext(), menuTab.determineTabAction(), null, new Class[] {}) );
				boolean clearDown = true;
				if (tabPanel.isLinkedToBean()) { //newbeantabpanels
					clearDown = false;
				}
				link.setActionExpression( expf.createMethodExpression(facesContext.getELContext(), "#{viewUtil.menuRedirect('" + menuTab.determineTabAction() + "', " + clearDown + ", true, " + menuTab.getId() + ")}", null, new Class[] {}) );

				link.setImmediate( true );
				middleLayerMiddleDiv.getChildren().add( link );
			}
			middleLayerRightDiv.getChildren().add( middleLayerMiddleDiv );
			middleLayerLeftDiv.getChildren().add( middleLayerRightDiv );
			tabDiv.getChildren().add( middleLayerLeftDiv );


			final String bottomMiddleImageUrl = getVerticalTabMiddleImagePath( facesContext, theme, tabList, i );
			HtmlPanelGroup bottomLayerDiv = new HtmlPanelGroup();
			bottomLayerDiv.setLayout( "block" );
			bottomLayerDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "bottomLayer" ) );
			bottomLayerDiv.setStyle( "height:22px;background-image:url('" + bottomMiddleImageUrl + "');margin-left:22px" );

			HtmlGraphicImage bottomLeftImage = new HtmlGraphicImage();
			bottomLeftImage.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "bottomLeft" ) );
			bottomLeftImage.setValue( getVerticalTabLeftImagePath( facesContext, theme, tabList, i ) );
			bottomLeftImage.setStyle( "float:left;margin-left:-22px" );

			HtmlGraphicImage bottomRightImage = new HtmlGraphicImage();
			bottomRightImage.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "bottomRight" ) );
			bottomRightImage.setValue( getVerticalTabRightImagePath( facesContext, theme, tabList, i ) );
			bottomRightImage.setStyle( "float:right" );

			bottomLayerDiv.getChildren().add( bottomLeftImage );
			bottomLayerDiv.getChildren().add( bottomRightImage );
			tabDiv.getChildren().add( bottomLayerDiv );
		}

		menuBar.getFacets().put( "tabListFacet", tabDiv );
	}

	public String getMenuTabImagePath( FacesContext facesContext, String theme, List<MenuTab> menuTabs, int index, Boolean leftImage ) {
		String imagePath;
		MenuTab tempMenuTab = menuTabs.get( index );
		if (leftImage) {

				if( tempMenuTab.isSelected() ) {
					imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "/" + imgUrlSubTabRightUp );
				} else {
					imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "/" + imgUrlSubTabBothDown );
				}
		} else {
			if( index == menuTabs.size() - 1 ) {
				if( tempMenuTab.isSelected()  ) {
					imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "/" + imgUrlSubTabFarRightUp );
				} else {
					imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "/" + imgUrlSubTabFarRightDown );
				}
			} else {
				if( tempMenuTab.isSelected() ) {
					imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "/" + imgUrlSubTabLeftUp );
				} else {
					imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "/" + imgUrlSubTabBothDown );
				}
			}
		}

		return imagePath;
	}

	public static String getVerticalTabMiddleImagePath( FacesContext facesContext, String theme, List<MenuTab> menuTabs, int index ) {
		String imagePath;
		if( index == menuTabs.size() - 1 ) {
				imagePath = ComponentUtil.getImageUrlWithTheme( facesContext, "sideSubTabMiddleBottom.png");
		} else {
			if( menuTabs.get( index + 1 ).isSelected() ) {
				imagePath = ComponentUtil.getImageUrlWithTheme( facesContext, "sideSubTabMiddleBelowUp.png" );
			} else {
				imagePath = ComponentUtil.getImageUrlWithTheme( facesContext, "sideSubTabMiddleAboveUp.png" );
			}
		}

		return imagePath;
	}

	public static String getVerticalTabRightImagePath( FacesContext facesContext, String theme, List<MenuTab> menuTabs, int index ) {
		String imagePath;
		MenuTab tempMenuTab = menuTabs.get( index );
		if( index == menuTabs.size() - 1 ) {
			if( tempMenuTab.isSelected()  ) {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabRightBottomUp.png");
			} else {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabRightBottomDown.png" );
			}
		} else {
			if( tempMenuTab.isSelected() ) {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabRightAboveUp.png" );
			} else if( menuTabs.get( index + 1 ).isSelected() ) {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabRightBelowUp.png" );
			} else {
				imagePath = ComponentUtil.getImageUrlWithThemeWithoutContext( facesContext, "sideSubTabRightBothDown.png" );
			}
		}

		return imagePath;
	}

	public String getTabStyle( MenuTab menuTab  ) {
		if( menuTab.isSelected() ) {
			return imgUrlTabExtenderUp;
		} else {
			return imgUrlTabExtenderDown;
		}
	}

}
