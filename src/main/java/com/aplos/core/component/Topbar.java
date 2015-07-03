package com.aplos.core.component;

import java.io.IOException;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIForm;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.aplos.common.appconstants.ComponentConstants;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;

@FacesComponent("com.aplos.core.component.Topbar")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class Topbar extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.Topbar";
	public static final String FOR_DATATABLE_ATTRIBUTE_KEY = "forDataTable";
	private Object[] _state = null;


	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin(facesContext);
		this.getChildren().clear();
		ResponseWriter writer = facesContext.getResponseWriter();

		addAjaxChildren(facesContext);
	}

	public UIForm getCurrentForm(UIComponent component) {
		while (!(component instanceof UIForm)) {
			component = component.getParent();
		}
		return (UIForm) component;
	}

	@Override
	public void encodeEnd(FacesContext facesContext) throws IOException {
		super.encodeEnd(facesContext);

	}

	public void addAjaxChildren( FacesContext facesContext ) {
		HtmlPanelGroup panel = new HtmlPanelGroup();
		panel.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		//panel.setLayout("block");
		panel.setStyle("float:right;");

		HtmlPanelGroup logoutPanel = new HtmlPanelGroup();
		logoutPanel.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		logoutPanel.setLayout("block");
		HtmlOutputText logoutLabel = new HtmlOutputText();
		logoutLabel.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );

		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();
		SystemUser currentUser = JSFUtil.getLoggedInUser();
		if (currentUser != null) {
			ValueExpression logoutLabelExpression = expf.createValueExpression(facesContext.getELContext(),
					 "Logged in as " + currentUser.getDisplayName(), Object.class);
			logoutLabel.setValue(logoutLabelExpression.getValue(facesContext.getELContext()));

			AplosSmallBorder logoutButton = new AplosSmallBorder();
			logoutButton.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );

			HtmlCommandLink logoutLink = new HtmlCommandLink();
			logoutLink.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
			logoutLink.setActionExpression(expf.createMethodExpression(facesContext.getELContext(),
			"#{ loginPage.logoutAndHome }", String.class, new Class[] {}));
			logoutLink.setValue("Logout");
			logoutLink.setStyleClass("stdBtnCommand");
			logoutLink.setImmediate(true);


	// *****
	//		HtmlPanelGroup suggestionPanel = new HtmlPanelGroup();
	//		suggestionPanel.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
	//		suggestionPanel.setLayout("block");
	//
	//		HtmlOutputText suggestionLabel = new HtmlOutputText();
	//		logoutLabel.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
	//
	//		AplosSmallBorder suggestionButton = new AplosSmallBorder();
	//		suggestionButton.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
	//
	//		HtmlCommandLink suggestionLink = new HtmlCommandLink();
	//		suggestionLink.setId( ComponentUtil.getUniqueId(getClass().getSimpleName(), getId() ) );
	//		suggestionLink.setActionExpression(expf.createMethodExpression(facesContext.getELContext(), "#{ SuggestionBoxPage }", String.class, new Class[] {}));
	//		suggestionLink.setStyleClass("stdBtnCommand");
	//		suggestionLink.setValue("Suggestion");
	// *****

			logoutButton.getChildren().add(logoutLink);
			logoutPanel.getChildren().add(logoutLabel);
			logoutPanel.getChildren().add(logoutButton);
	//		suggestionButton.getChildren().add(suggestionLink);
	//		suggestionPanel.getChildren().add(suggestionLabel);
	//		suggestionPanel.getChildren().add(suggestionButton);
			panel.getChildren().add(logoutPanel);
		}
//		panel.getChildren().add(suggestionPanel);

		HtmlOutputText br = new HtmlOutputText();
		br.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		br.setValue("<br style='clear:both' />");
		br.setEscape(false);


		getChildren().add(panel);
		getChildren().add( br );
	}

	@Override
	public void restoreState(FacesContext _context, Object _state) {
		this._state = (Object[]) _state;
		super.restoreState(_context, this._state[0]);
		ComponentUtil.addStateToAttributes(this,
				ComponentConstants.WIDTH_ATTRIBUTE_KEY, this._state[1]);
		ComponentUtil.addStateToAttributes(this,
				ComponentConstants.STYLE_ATTRIBUTE_KEY, this._state[2]);
		ComponentUtil.addStateToAttributes(this,
				ComponentConstants.HEIGHT_ATTRIBUTE_KEY, this._state[3]);
	}

	@Override
	public Object saveState(FacesContext _context) {
		if (_state == null) {
			_state = new Object[6];
		}

		_state[0] = super.saveState(_context);

		_state[1] = this.getAttributes().get(
				ComponentConstants.WIDTH_ATTRIBUTE_KEY);
		_state[2] = this.getAttributes().get(
				ComponentConstants.STYLE_ATTRIBUTE_KEY);
		_state[3] = this.getAttributes().get(
				ComponentConstants.HEIGHT_ATTRIBUTE_KEY);

		return _state;
	}

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

}
