package com.aplos.core.component.deferrablescript;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;

import com.aplos.core.application.ResourceIdentifier;

/**
 * <p>
 * The <code>&lt;o:deferrableScript&gt;</code> is a component based on the standard <code>&lt;h:outputScript&gt;</code>
 * which defers the loading of the given script resource to the window load event. In other words, the given script
 * resource is only loaded when the window is really finished with loading. So, the enduser can start working with the
 * webpage without waiting for the additional scripts to be loaded. Usually, it are those kind of scripts which are just
 * for progressive enhancement and thus not essential for the functioning of the webpage.
 * <p>
 * This will give bonus points with among others the Google PageSpeed tool, on the contrary to placing the script at
 * bottom of body, or using <code>defer="true"</code> or even <code>async="true"</code>.
 *
 * <h3>Usage</h3>
 * <p>
 * Just use it the same way as a <code>&lt;h:outputScript&gt;</code>, with a <code>library</code> and <code>name</code>.
 * <pre>
 * &lt;o:deferrableScript library="yourlibrary" name="scripts/filename.js" /&gt;
 * </pre>
 * <p>
 * You can use the optional <code>onbegin</code>, <code>onsuccess</code> and <code>onerror</code> attributes
 * to declare JavaScript code which needs to be executed respectively right before the script is loaded,
 * right after the script is successfully loaded, and/or when the script loading failed.
 *
 * @author Bauke Scholtz
 * @since 1.8
 * @see DeferrableScriptRenderer
 */
@FacesComponent(DeferrableScript.COMPONENT_TYPE)

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="components", name="components.js")
})
public class DeferrableScript extends UIComponentBase {

	// Public constants -----------------------------------------------------------------------------------------------

	/** The standard component type. */
	public static final String COMPONENT_TYPE = "com.aplos.core.component.deferrablescript.DeferrableScript";
	public static final String COMPONENT_FAMILY = "com.aplos.core.component.deferrablescript";

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Construct a new {@link DeferrableScript} component whereby the renderer type is set to
	 * {@link DeferrableScriptRenderer#RENDERER_TYPE}.
	 */
	public DeferrableScript() {
		setRendererType(DeferrableScriptRenderer.RENDERER_TYPE);
	}
	
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	/**
	 * Returns <code>true</code>.
	 */
	@Override
	public boolean getRendersChildren() {
		return true;
	}

}
