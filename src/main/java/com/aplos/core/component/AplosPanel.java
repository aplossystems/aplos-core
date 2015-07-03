package com.aplos.core.component;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

@FacesComponent("com.aplos.core.component.AplosPanel")
public class AplosPanel extends org.primefaces.component.outputpanel.OutputPanel {

	private static final String DEFAULT_RENDERER = "com.aplos.core.component.AplosPanelRenderer";

	protected enum PropertyKeys {

		title;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}

	public AplosPanel() {
		super();
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public void processDecodes(FacesContext context) {
		super.processDecodes(context);
	}

	public java.lang.String getTitle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.title, null);
	}
	public void setTitle(java.lang.String title) {
		getStateHelper().put(PropertyKeys.title, title);
		handleAttribute("title", title);
	}

}
