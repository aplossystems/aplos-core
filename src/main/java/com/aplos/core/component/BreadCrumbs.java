package com.aplos.core.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.convert.IntegerConverter;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.aplos.common.BackingPageUrl;
import com.aplos.common.MenuCacher;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.backingpage.BackingPageState;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DynamicBundleEntry;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.interfaces.DynamicMenuNode;
import com.aplos.common.module.AplosModule;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.tabpanels.MenuTab;
import com.aplos.common.tabpanels.NavigationStack;
import com.aplos.common.tabpanels.TabClass;
import com.aplos.common.tabpanels.TabPanel;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.ViewUtil;


@FacesComponent("com.aplos.core.component.BreadCrumbs")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class BreadCrumbs extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.BreadCrumbs";
	public static final String BREADCRUMBS = "breadCrumbs";
	public static final String HOME_LOCATION = "home";
	public static final String SHOW_LIST_PAGES = "showListPages";
	public static final String HISTORY_VARIABLE_BINDING = CommonUtil.getBinding(ViewUtil.class) + ".historyDropdownSelectedIndex";

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

	@Override
	public String getRendererType() {
		return null;
	}

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin( facesContext );

		UIComponent breadCrumbs = getFacet("breadCrumbs");

		SystemUser currentUser = JSFUtil.getLoggedInUser();
		if( currentUser != null && currentUser.isLoggedIn() ) {
			if (breadCrumbs == null) {
				createBreadCrumbsFacet(facesContext);
			}
			getFacet("breadCrumbs").encodeAll(facesContext);
		}
	}

	@SuppressWarnings("unchecked")
	private void createBreadCrumbsFacet(FacesContext facesContext) {
		boolean debug = false; //ApplicationUtil.getAplosContextListener().isDebugMode();
		HtmlPanelGroup breadCrumbsBarDiv = new HtmlPanelGroup();
		breadCrumbsBarDiv.setLayout( "block" );
		breadCrumbsBarDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "breadCrumbsBar" ) );
		HtmlPanelGroup breadCrumbsDiv = new HtmlPanelGroup();
		breadCrumbsDiv.setLayout( "block" );
		breadCrumbsDiv.setStyleClass( "breadCrumbsDiv" );
		breadCrumbsDiv.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "breadCrumbs" ) );
		MenuCacher menuCacher = ApplicationUtil.getAplosContextListener().getMenuCacher();
		if (menuCacher != null && JSFUtil.getAplosRequestContext().getMainTabPanel() != null) {
			
			ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
			String homeLocation = (String) getAttributes().get( HOME_LOCATION );
			if (homeLocation == null) {
				if (JSFUtil.getLoggedInUser() != null && JSFUtil.getLoggedInUser().isLoggedIn()) {
					
					AplosModule cmsModule = ApplicationUtil.getAplosContextListener().getAplosModuleByName("cms"); //com.aplos.cms.module.CmsModule
					if (cmsModule != null) {
						homeLocation = JSFUtil.getContextPath() + "/cms/";
					} else {
//						Website website = Website.getCurrentWebsiteFromTabSession();
//						if (website != null) {
//							homeLocation = JSFUtil.getContextPath() + "/" + website.getPackageName();
//						} else {
//							homeLocation = "/";
//						}
						homeLocation = JSFUtil.getContextPath() + "/";
					}

				} else {
					homeLocation = JSFUtil.getContextPath() + "/";
				}
			}
			if (!homeLocation.contains("?")) {
				homeLocation = homeLocation + "?lang=" + CommonUtil.getContextLocale().getLanguage();
			} else if (!homeLocation.contains("lang=")) {
				homeLocation = homeLocation.replaceFirst("?", "?lang=" + CommonUtil.getContextLocale().getLanguage());
			}
			HtmlCommandLink homeLink = new HtmlCommandLink();
			homeLink.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
			String home = "Home";
//			Website website = Website.getCurrentWebsiteFromTabSession();
//			if (website != null) {
//				home = website.getDisplayName();
//			} else {
				if (CommonConfiguration.getCommonConfiguration().getIsInternationalizedApplication()) {
					home = CommonUtil.translate("HOME");
				}
//			}
			ValueExpression homeVE = expf.createValueExpression(facesContext.getELContext(), home, String.class);
			homeLink.setValueExpression("value", homeVE);
			MethodExpression methodExpresssion = expf.createMethodExpression(facesContext.getELContext(), "#{viewUtil.menuRedirect('" + homeLocation + "', true, false)}", String.class, new Class[] {});
			homeLink.setActionExpression(methodExpresssion);
			homeLink.setImmediate(true);
			breadCrumbsDiv.getChildren().add(homeLink);	
			
			NavigationStack navStack = JSFUtil.getNavigationStack(); //used to create crumbs
			
			Stack<HtmlCommandLink> reverseOrderCrumbs = new Stack<HtmlCommandLink>();
			
			//go backwards up the tree, add the crumbs to a stack so we can use them in reverse order (ie: down the tree)

			TabPanel currentSubTabPanel = JSFUtil.getAplosRequestContext().getSubTabPanel();
			TabPanel mainTabPanel = menuCacher.getCurrentMainTabPanel(true);
			HtmlCommandLink extraListCrumb = null;
			if (currentSubTabPanel != null) {

				DynamicMenuNode currentObject = null;
				DynamicMenuNode previousObject = null;
				DynamicMenuNode previousPreviousObject = null;
				MenuTab selectedTab = currentSubTabPanel.getSelectedTab();
				if (selectedTab == null) {
					selectedTab = currentSubTabPanel.getDefaultTab();
				}
				currentObject = selectedTab;
				
				while (currentObject != null && currentObject.getParent() != null && !currentObject.getParent().equals(mainTabPanel) /*&& (currentObject.getParent().getParent() == null || !currentObject.getParent().getParent().equals(mainTabPanel))*/ ) {
				
					if (currentObject instanceof MenuTab) {
						
						//add an extra crumb if this tab is only selected because of an additional binding
						Class<? extends BackingPage> extraEditCrumbClassAdded = null;
						if (reverseOrderCrumbs.size() == 0) {
							/*
							 * check the last crumb we found against the current page, if it isn't assignable then it is an
							 * additional binding and needs an additional crumb.  We check assignable and not equals because
							 * of backingpage overriding.
							 */
							if (JSFUtil.getCurrentBackingPage() != null) {
								extraEditCrumbClassAdded = JSFUtil.getCurrentBackingPage().getClass();
								if (((MenuTab)currentObject).getTabActionClass() != null 
										&& ((MenuTab)currentObject).getTabActionClass().getBackingPageClass() != null
										&& !((MenuTab)currentObject).getTabActionClass().getBackingPageClass().isAssignableFrom( extraEditCrumbClassAdded )) {
									reverseOrderCrumbs.add(createCrumb(navStack, extraEditCrumbClassAdded, expf, facesContext, debug, "ExtraEditCrumbBinding"));
								} else {
									extraEditCrumbClassAdded = null;
								}
							}
						} 

						//we need this check but it cant be an if else with the above check otherwise list pages show an edit crumb before a bean is selected, when no previous (trailing) crumb
						if (reverseOrderCrumbs.size() != 0) {
							//eg internal projects should be followed by the project name (when not the final crumb already (ie when job selected)
							if ( ((MenuTab)currentObject).getTabActionClass() != null && ListPage.class.isAssignableFrom(((MenuTab)currentObject).getTabActionClass().getBackingPageClass()) ) {
								//display the selected requisite bean, by using the editpage class
								Class<? extends EditPage> editPage = (Class<? extends EditPage>) ApplicationUtil.getAplosContextListener().getEditPageClasses().get( ((MenuTab)currentObject).getTabActionClass().getBackingPageClass().getSimpleName().replace("ListPage", "") );
								/*
								 * Why was it trying to get the super class, this doesn't make sense when it's inheriting a common list page class 
								 * like clientBeanListPage in carbon profile.
								 */
//								if (editPage == null && ((MenuTab)currentObject).getTabActionClass().getBackingPageClass().getSuperclass() != null) {
//									editPage = (Class<? extends EditPage>) ApplicationUtil.getAplosContextListener().getEditPageClasses().get( ((MenuTab)currentObject).getTabActionClass().getBackingPageClass().getSuperclass().getSimpleName().replace("ListPage", "") );
//								}
								if (editPage != null) {
									//we need to check that if we have a crumb following this one, its not actually this one
									if (previousObject == null || ( ((MenuTab)previousObject).getTabActionClass() == null || !(((MenuTab)previousObject).getTabActionClass().getBackingPageClass().equals(editPage)) )) {
										if (extraEditCrumbClassAdded == null || !(extraEditCrumbClassAdded.equals(editPage)) ) {
											reverseOrderCrumbs.add(createCrumb(navStack, editPage, expf, facesContext, debug, "RequisiteBeanCrumb"));
										}
									}
								}
								
							}
						}
						
						//add the selected crumb
						reverseOrderCrumbs.add(createCrumb(navStack, (MenuTab)currentObject, expf, facesContext, debug, "SelectedCrumb"));
						
						previousPreviousObject = previousObject;
						previousObject = currentObject;
						
						//if its a nbtp, add default
						if (currentObject.getParent() != null && ((TabPanel)currentObject.getParent()).isLinkedToBean()) {
							if ( ((TabPanel)currentObject.getParent()).getDefaultTab() != null && !((TabPanel)currentObject.getParent()).getDefaultTab().equals(currentObject) ) {
								reverseOrderCrumbs.add(createCrumb(navStack, ((TabPanel)currentObject.getParent()).getDefaultTab(), expf, facesContext, debug, "NbtpCrumb"));								
								previousPreviousObject = currentObject;
								previousObject = ((TabPanel)currentObject.getParent()).getDefaultTab();
							}
						}
						
					}
					
					currentObject = currentObject.getParent();

				}
				
				if ( previousObject != null && ((MenuTab)previousObject).getTabActionClass() != null ) {
					if ( EditPage.class.isAssignableFrom(((MenuTab)previousObject).getTabActionClass().getBackingPageClass()) ) {
						Class<? extends ListPage> listPage = (Class<? extends ListPage>) ApplicationUtil.getAplosContextListener().getListPageClasses().get( ((MenuTab)previousObject).getTabActionClass().getBackingPageClass().getSimpleName().replace("EditPage", "") );
						if (listPage != null && (previousPreviousObject == null || (((MenuTab)previousPreviousObject).getTabActionClass() != null && 
							!(((MenuTab)previousPreviousObject).getTabActionClass().getBackingPageClass().equals(listPage))))) {
							extraListCrumb = createCrumb(navStack, listPage, expf, facesContext, debug, "ExtraListCrumb");
						}
					}
				}
				
			}
			
			MenuTab selectedTab = mainTabPanel.getSelectedTab();
			if (selectedTab == null) {
				selectedTab = mainTabPanel.getDefaultTab();
			}
			if (selectedTab != null) { 
				if (currentSubTabPanel == null) {
					//add an extra crumb if this tab is only selected because of an additional binding
					if (JSFUtil.getCurrentBackingPage() != null) {
						Class<? extends BackingPage> currentBackingPageClass = JSFUtil.getCurrentBackingPage().getClass();
						if (selectedTab.getTabActionClass() != null && !currentBackingPageClass.equals( selectedTab.getTabActionClass().getBackingPageClass() )) {
							reverseOrderCrumbs.add(createCrumb(navStack, currentBackingPageClass, expf, facesContext, debug, "ExtraMainCrumbBinding"));
						}
					}
				}
				reverseOrderCrumbs.add(createCrumb(navStack, selectedTab, expf, facesContext, debug, "MainSelectedTabCrumb"));
			}
			
			//PRINT OUT OUR CRUMBS HERE
			HtmlCommandLink crumb = null;
			
			while (reverseOrderCrumbs.size() > 0) {
				crumb = reverseOrderCrumbs.pop();
				if ( crumb != null ) {
					if (reverseOrderCrumbs.size() == 0 && extraListCrumb != null) {
						addNewSeparator(expf, facesContext, breadCrumbsDiv);
						breadCrumbsDiv.getChildren().add(extraListCrumb);
					}				
					addNewSeparator(expf, facesContext, breadCrumbsDiv);
					breadCrumbsDiv.getChildren().add(crumb);
				}
			}

			HtmlSelectOneMenu historyDropdown = new HtmlSelectOneMenu();
			//historyDropdown.setConverter(new AplosAbstractBeanConverter());
			historyDropdown.setConverter(new IntegerConverter());
			historyDropdown.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "BCHD_" + getId()) );
			historyDropdown.setImmediate(true);
			historyDropdown.setStyleClass("aplos-history");
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			List<BackingPageState> historyList = JSFUtil.getHistoryList();
			if (historyList.size() < 2) {
				String noHistory = ApplicationUtil.getAplosContextListener().translateByKey(CommonBundleKey.BREADCRUMBS_NO_HISTORY);
				selectItems.add(new SelectItem(null,"- " + noHistory + " -", "", false, true, true));
			} else {
				String history = ApplicationUtil.getAplosContextListener().translateByKey(CommonBundleKey.BREADCRUMBS_HISTORY);
				selectItems.add(new SelectItem(null,"- " + history + " -", "", false, true, true));
			}
			for (int index=historyList.size()-1; index > 0; index--) { //ignore current page
				selectItems.add(createHistorySelectItem( index, historyList.get(index-1), debug ));
			}
			UISelectItems items = new UISelectItems();
			items.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "BCHDSI_" + getId()) );
			items.setValue( selectItems );
			historyDropdown.getChildren().add(items);
			historyDropdown.setValueExpression("value",  expf.createValueExpression(facesContext.getELContext(), "#{" + HISTORY_VARIABLE_BINDING + "}", Integer.class));
			//why put the variable on viewutil and not on navigation stack? - http://stackoverflow.com/questions/4412171/numberformatexception-for-input-string
			ViewUtil viewUtil = ((ViewUtil)((HttpServletRequest) facesContext.getExternalContext().getRequest()).getSession().getAttribute(CommonUtil.getBinding(ViewUtil.class)));
			if (viewUtil != null) {
				viewUtil.setHistoryDropdownSelectedIndex(null);
			}
			historyDropdown.setOnchange( "submit()" );
			MethodExpression historyMethodExpresssion = expf.createMethodExpression(facesContext.getELContext(), "#{viewUtil.historyRedirect}", null, new Class[] { ValueChangeEvent.class });
			MethodExpressionValueChangeListener methodExpressionValueChangeListener = new MethodExpressionValueChangeListener( historyMethodExpresssion );
			historyDropdown.addValueChangeListener(methodExpressionValueChangeListener);
			breadCrumbsBarDiv.getChildren().add(breadCrumbsDiv);
			breadCrumbsBarDiv.getChildren().add(historyDropdown);
			HtmlPanelGroup breadCrumbsClearDiv = new HtmlPanelGroup();
			breadCrumbsClearDiv.setStyle( "clear:both" );
			breadCrumbsClearDiv.setLayout( "block" );
			breadCrumbsBarDiv.getChildren().add(breadCrumbsClearDiv);
		}
		getFacets().put( "breadCrumbs", breadCrumbsBarDiv );
	}

	private SelectItem createHistorySelectItem(int index, BackingPageState backingPageState, boolean debug) {
		String name = backingPageState.getTrailDisplayName().determineName();
		if (debug) {
			name = index + " -> " + name;
			if (index < 10) {
				name = "0" + name;
			}
		}
		//the words back-to looked too long so i used an arrow
		//name = "&#8629;&#160;&#160;" + name;
//		//make the entries pretty
//		int maxLen = "Subscription Channels".length();
//		while (name.length() < maxLen) { //20 total
//			name += " ";
//		}
//		if (name.length() > maxLen) {
//			name = name.substring(0, maxLen);
//		}
//		name = name.replaceAll(" ", "&#160;");
//		name += "&#160;&#8629;";
		return new SelectItem(index, name, "", false, false);
	}

	/**
	 * Tries to map a MenuTab to a BackingPageState in our history
	 * @param navStack
	 * @param menuTab
	 * @return
	 */
	private BackingPageState getBackingPageStateFromHistory(NavigationStack navStack, Class<? extends BackingPage> backingPageClass) {
		//go backwards as the last instace of the class is the most important
		//for (BackingPageState tempBackingPageState : navStack) {
		for (int i = navStack.size()-1; i >= 0; i--) {
			BackingPageState tempBackingPageState = navStack.get(i);
			if (tempBackingPageState.getBackingPageClass() != null && tempBackingPageState.getBackingPageClass().equals(backingPageClass)) {
				return tempBackingPageState;
			}
 		}
		return null;
	}

	private BackingPageState getBackingPageStateFromHistory(NavigationStack navStack, TabClass tabActionClass) {
		if (tabActionClass != null) {
			return getBackingPageStateFromHistory(navStack, tabActionClass.getBackingPageClass());
		} else {
			return null;
		}
	}

	/**
	 * Crumbs using this method need to load the correct state if its a page we've been to,
	 * otherwise just use a simple link
	 * @param navStack
	 * @param menuTab
	 * @param expf
	 * @param facesContext
	 * @return
	 */
	private HtmlCommandLink createCrumb(NavigationStack navStack, MenuTab menuTab, ExpressionFactory expf, FacesContext facesContext, boolean debug, String identifier) {
		if (menuTab == null) {
			return null;
		}
		BackingPageState backingPageState = getBackingPageStateFromHistory(navStack, menuTab.getTabActionClass());
		if (backingPageState != null) {
			return createCrumb(navStack.indexOf(backingPageState), backingPageState, expf, facesContext, debug, identifier);
		} else {
			//It's not somewhere we've been, so just create a simple link
			boolean clearDown = true;
			if (menuTab.getParent() != null && ((TabPanel)menuTab.getParent()).isLinkedToBean()) {
				clearDown = false;
			}
			if (menuTab.determineTabAction() != null) {
				return createCrumb(menuTab.getDisplayName(), menuTab.determineTabAction().toString(), clearDown, expf, facesContext, debug, identifier);
			} else {
				return null;
			}
		}
	}
	
	private HtmlCommandLink createCrumb(NavigationStack navStack, Class<? extends BackingPage> backingPageClass, ExpressionFactory expf, FacesContext facesContext, boolean debug, String identifier) {
		BackingPageState backingPageState = getBackingPageStateFromHistory(navStack, backingPageClass);
		if (backingPageState != null) {
			return createCrumb(navStack.indexOf(backingPageState), backingPageState, expf, facesContext, debug, identifier);
		} else {
			return createCrumb(BackingPage.getTrailDisplayNameFromClassName(backingPageClass).determineName(), new BackingPageUrl(backingPageClass).toString(), true, expf, facesContext, debug, identifier);
		}
	}

	private HtmlCommandLink createCrumb(String name, String redirectUrl, boolean clearDown, ExpressionFactory expf, FacesContext facesContext, boolean debug, String identifier) {
		DynamicBundleEntry dynamicBundleEntry = ApplicationUtil.getAplosContextListener().getDynamicBundleEntry( name );
		if( dynamicBundleEntry != null ) {
			name = dynamicBundleEntry.getEntryValue(); 
		}
		HtmlCommandLink crumbLink = new HtmlCommandLink();
		crumbLink.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		if (debug) {
			name += " (";
			if (identifier != null && !identifier.equals("")) {
				name += identifier + ", ";
			}
			name += redirectUrl.substring(redirectUrl.lastIndexOf("/") +1, redirectUrl.lastIndexOf(".")) + ")";
		}
		ValueExpression crumbVE = expf.createValueExpression(facesContext.getELContext(),(name),String.class);
		crumbLink.setValueExpression("value", crumbVE);
		crumbLink.setActionExpression( expf.createMethodExpression(facesContext.getELContext(), "#{viewUtil.menuRedirect('" + redirectUrl + "', " + clearDown + ", true)}", null, new Class[] {}) );
		crumbLink.setImmediate(true);
		return crumbLink;
	}

	private String getCrumbName(BackingPageState backingPageState, boolean debug, String identifier){
		String name = backingPageState.getTrailDisplayName().determineName();
		if (debug) {
			if (backingPageState.getBackingPageClass() != null) {
				name += " (";
				if (identifier != null && !identifier.equals("")) {
					name += identifier + ", ";
				}
				name += backingPageState.getBackingPageClass().getSimpleName() + ")";
			}
		}
		return name;
	}

	private HtmlCommandLink createCrumb(int index, BackingPageState backingPageState, ExpressionFactory expf, FacesContext facesContext, boolean debug, String identifier) {

		String name = getCrumbName(backingPageState, debug, identifier);
		//Return null if this is a 'Home' crumb
		if (name == null || name.toLowerCase().equals("home") || name.toLowerCase().equals("homepage")) {
			return null;
		}

		HtmlCommandLink crumbLink = new HtmlCommandLink();
		crumbLink.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		ValueExpression crumbVE = expf.createValueExpression(facesContext.getELContext(),(name),String.class);
		crumbLink.setValueExpression("value", crumbVE);
		MethodExpression methodExpresssion = expf.createMethodExpression(facesContext.getELContext(), "#{viewUtil.breadcrumbRedirect(" + index + ")}", String.class, new Class[] {});
		crumbLink.setActionExpression(methodExpresssion);
		crumbLink.setImmediate(true);
		return crumbLink;

	}

	public void addNewSeparator(ExpressionFactory expf, FacesContext facesContext, HtmlPanelGroup div) {
		UIOutput separatorText = new UIOutput();
		separatorText.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		String sep = " > ";
//		it seems it does this automatically, in chrome at least
//		if (CommonConfiguration.getCommonConfiguration().getIsInternationalizedApplication()) {
//			sep = CommonUtil.translate(" > ");
//		}
		ValueExpression separatorVE = expf.createValueExpression(facesContext.getELContext(), sep, String.class);
		separatorText.setValueExpression("value", separatorVE);

		div.getChildren().add(separatorText);
	}

}
