package com.aplos.core.component;

import java.io.File;
import java.io.IOException;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.appconstants.ComponentConstants;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.utils.JSFUtil;

@FacesComponent("com.aplos.core.component.ScriptsAndStylesWriter")
@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="components", name="components.css"),
	@ResourceDependency(library="styles", name="common.css"),
	@ResourceDependency(library="styles", name="#{ themeManager.theme }.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="scripts", name="aploscommon.js"),
	@ResourceDependency(library="scripts", name="#{ contextListener.implementationModule.packageName }.js")
})
public class ScriptsAndStylesWriter extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.ScriptsAndStylesWriter";

	@Override
	public void encodeBegin( FacesContext facesContext ) throws IOException {
		AplosContextListener aplosContextListener = (AplosContextListener) facesContext.getExternalContext().getApplicationMap().get( AplosScopedBindings.CONTEXT_LISTENER );
		if (aplosContextListener.isDebugMode())  {
			String filename = aplosContextListener.getImplementationModule().getPackageName() + ".js";
			//facesContext.getExternalContext().getResource( ComponentConstants.SCRIPT_PATH + "aploscommon.js" );
			String realPath = "/resources" + ComponentConstants.SCRIPT_PATH + filename;
			File file = new File( facesContext.getExternalContext().getRealPath( realPath ) );
			//file.getAbsolutePath(); //relative to eclipse, not to the tomcat work dir
			if (!file.exists()) {
				JSFUtil.addMessageForError( "Debug: " + realPath + " does not exist, this will be causing RES_NOT_FOUND to be displayed in your browser console." );
			}
		}
	}

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

}
