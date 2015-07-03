package com.aplos.core.component;

import java.io.IOException;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;

import com.aplos.common.appconstants.ComponentConstants;
import com.aplos.common.utils.ComponentUtil;

@FacesComponent("com.aplos.core.component.ThemeChanger")
@ResourceDependencies({
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css")
})
public class ThemeChanger extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.ThemeChanger";
	public static final String THEME_CHANGER = "themeChanger";

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin( facesContext );

		if( this.getAttributes().get( ComponentConstants.RENDERED_ATTRIBUTE_KEY ).equals( "false" ) ) {
			this.setRendered( false );
		} else {
			this.setRendered( true );
		}

		UIComponent themeChanger = getFacet("ThemeChanger");
		if (themeChanger == null) {
			createThemeChangerFacet(facesContext);
		}
		getFacet("themeChanger").encodeAll(facesContext);
	}


	private void createThemeChangerFacet(FacesContext facesContext) {
		ExpressionFactory expf = facesContext.getApplication().getExpressionFactory();

		HtmlPanelGroup div = new HtmlPanelGroup();
		div.setLayout( "block" );
		div.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), "themeChanger" ) );


		UIOutput themeText = new UIOutput();
		themeText.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		ValueExpression themeVE = expf.createValueExpression(facesContext.getELContext(), "Theme:", String.class);
		themeText.setValueExpression("value", themeVE);

		div.getChildren().add(themeText);

		//addCarriageReturn(expf, facesContext, div);


		UIOutput aploraText = new UIOutput();
		aploraText.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		ValueExpression aploraVE = expf.createValueExpression(facesContext.getELContext(), "Aplora ", String.class);
		aploraText.setValueExpression("value", aploraVE);

		HtmlCommandLink aploraLink = new HtmlCommandLink();
		aploraLink.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );

		aploraLink.setActionExpression(expf.createMethodExpression(facesContext.getELContext(),
				"#{currentUser.setThemeAndSetInThemeManager(\"aplora\")}", String.class, new Class[] {}));

		aploraLink.getChildren().add(aploraText);

		div.getChildren().add(aploraLink);


		//addCarriageReturn(expf, facesContext, div);


		UIOutput classicText = new UIOutput();
		classicText.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		ValueExpression classicVE = expf.createValueExpression(facesContext.getELContext(), "Classic ", String.class);
		classicText.setValueExpression("value", classicVE);

		HtmlCommandLink classicLink = new HtmlCommandLink();
		classicLink.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );

		classicLink.setActionExpression(expf.createMethodExpression(facesContext.getELContext(),
				"#{currentUser.setThemeAndSetInThemeManager(\"classic\")}", String.class, new Class[] {}));

		classicLink.getChildren().add(classicText);

		div.getChildren().add(classicLink);


		//addCarriageReturn(expf, facesContext, div);


		UIOutput modernText = new UIOutput();
		modernText.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		ValueExpression modernVE = expf.createValueExpression(facesContext.getELContext(), "Modern", String.class);
		modernText.setValueExpression("value", modernVE);

		HtmlCommandLink modernLink = new HtmlCommandLink();
		modernLink.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );

		modernLink.setActionExpression(expf.createMethodExpression(facesContext.getELContext(),
				"#{currentUser.setThemeAndSetInThemeManager(\"modern\")}", String.class, new Class[] {}));

		modernLink.getChildren().add(modernText);

		div.getChildren().add(modernLink);



		getFacets().put( "themeChanger", div );
	}

	public void addCarriageReturn(ExpressionFactory expf, FacesContext facesContext, HtmlPanelGroup div) {
		UIOutput separatorText = new UIOutput();
		separatorText.setId( ComponentUtil.getUniqueId( getClass().getSimpleName(), getId() ) );
		ValueExpression separatorVE = expf.createValueExpression(facesContext.getELContext(), " <br /> ", String.class);
		separatorText.setValueExpression("value", separatorVE);

		div.getChildren().add(separatorText);
	}
}
