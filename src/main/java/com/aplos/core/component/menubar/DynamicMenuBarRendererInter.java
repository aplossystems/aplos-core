package com.aplos.core.component.menubar;

import javax.faces.context.FacesContext;

public interface DynamicMenuBarRendererInter {
	public void createVerticalMenuList(FacesContext facesContext, String theme, DynamicMenuBar menuBar );
	public void createHorizontalMenuList(FacesContext facesContext, String theme, DynamicMenuBar menuBar);
	public void updateComponentVariables( DynamicMenuBar menuBar );
}
