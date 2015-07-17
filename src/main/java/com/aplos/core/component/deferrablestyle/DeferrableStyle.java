package com.aplos.core.component.deferrablestyle;

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
 * The <code>&lt;o:deferrablestyle&gt;</code> is a component based on the standard <code>&lt;h:outputstyle&gt;</code>
 * which defers the loading of the given style resource to the window load event. In other words, the given style
 * resource is only loaded when the window is really finished with loading. So, the enduser can start working with the
 * webpage without waiting for the additional styles to be loaded. Usually, it are those kind of styles which are just
 * for progressive enhancement and thus not essential for the functioning of the webpage.
 * <p>
 * This will give bonus points with among others the Google PageSpeed tool, on the contrary to placing the style at
 * bottom of body, or using <code>defer="true"</code> or even <code>async="true"</code>.
 *
 * <h3>Usage</h3>
 * <p>
 * Just use it the same way as a <code>&lt;h:outputstyle&gt;</code>, with a <code>library</code> and <code>name</code>.
 * <pre>
 * &lt;o:deferrablestyle library="yourlibrary" name="styles/filename.js" /&gt;
 * </pre>
 * <p>
 * You can use the optional <code>onbegin</code>, <code>onsuccess</code> and <code>onerror</code> attributes
 * to declare Javastyle code which needs to be executed respectively right before the style is loaded,
 * right after the style is successfully loaded, and/or when the style loading failed.
 *
 * @author Bauke Scholtz
 * @since 1.8
 * @see DeferrableStyleRenderer
 */
@FacesComponent(DeferrableStyle.COMPONENT_TYPE)

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="components", name="components.js")
})
public class DeferrableStyle extends UIComponentBase {

	// Public constants -----------------------------------------------------------------------------------------------

	/** The standard component type. */
	public static final String COMPONENT_TYPE = "com.aplos.core.component.deferrablestyle.DeferrableStyle";
	public static final String COMPONENT_FAMILY = "com.aplos.core.component.deferrablestyle";

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Construct a new {@link DeferrableStyle} component whereby the renderer type is set to
	 * {@link DeferrableStyleRenderer#RENDERER_TYPE}.
	 */
	public DeferrableStyle() {
		setRendererType(DeferrableStyleRenderer.RENDERER_TYPE);
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
