package com.aplos.core.component.deferredscript;

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
 * The <code>&lt;o:deferredScript&gt;</code> is a component based on the standard <code>&lt;h:outputScript&gt;</code>
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
 * &lt;o:deferredScript library="yourlibrary" name="scripts/filename.js" /&gt;
 * </pre>
 * <p>
 * You can use the optional <code>onbegin</code>, <code>onsuccess</code> and <code>onerror</code> attributes
 * to declare JavaScript code which needs to be executed respectively right before the script is loaded,
 * right after the script is successfully loaded, and/or when the script loading failed.
 *
 * @author Bauke Scholtz
 * @since 1.8
 * @see DeferredScriptRenderer
 */
@FacesComponent(DeferredScript.COMPONENT_TYPE)
@ResourceDependency(library="components", name="components.js", target="head")
@ListenerFor(systemEventClass=PostAddToViewEvent.class)
public class DeferredScript extends UIComponentBase {

	// Public constants -----------------------------------------------------------------------------------------------

	/** The standard component type. */
	public static final String COMPONENT_TYPE = "com.aplos.core.component.deferredscript.DeferredScript";
	public static final String COMPONENT_FAMILY = "com.aplos.core.component.deferredscript";

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Construct a new {@link DeferredScript} component whereby the renderer type is set to
	 * {@link DeferredScriptRenderer#RENDERER_TYPE}.
	 */
	public DeferredScript() {
		setRendererType(DeferredScriptRenderer.RENDERER_TYPE);
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

	// Actions --------------------------------------------------------------------------------------------------------

	@Override
	public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
		if (event instanceof PostAddToViewEvent) {
			FacesContext context = FacesContext.getCurrentInstance();
			PartialViewContext ajaxContext = context.getPartialViewContext();
			UIViewRoot view = context.getViewRoot();

			boolean ajaxRequest = ajaxContext.isAjaxRequest();
			boolean ajaxRenderAll = ajaxContext.isRenderAll();
			boolean alreadyAdded = view.getComponentResources(context, "body").contains(this);

			if (!(ajaxRequest && !ajaxRenderAll) || !alreadyAdded) {
				view.addComponentResource(context, this, "body");
				ResourceIdentifier.setMojarraResourceRendered(context, new ResourceIdentifier(this));
			}
		}
	}

}
