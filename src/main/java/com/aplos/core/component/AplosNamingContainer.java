package com.aplos.core.component;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

@FacesComponent("com.aplos.core.component.AplosNamingContainer")
public class AplosNamingContainer extends UINamingContainer {

	public AplosNamingContainer() {
		super();
	}

	@Override
	public void processDecodes(FacesContext context) {
		super.processDecodes(context);
	}

}
