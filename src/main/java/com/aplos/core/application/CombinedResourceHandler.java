package com.aplos.core.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import com.aplos.common.beans.Website;
import com.aplos.common.enums.CombinedResourceStatus;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.component.deferredscript.DeferredScript;
import com.aplos.core.component.deferredscript.DeferredScriptRenderer;



public class CombinedResourceHandler extends ResourceHandlerWrapper implements SystemEventListener {

	// Constants ------------------------------------------------------------------------------------------------------

	/** The default library name of a combined resource. Make sure that this is never used for other libraries. */
	public static final String LIBRARY_NAME = "aplos.combinedresources";


	private static final String TARGET_HEAD = "head";
	private static final String TARGET_BODY = "body";
	
	private boolean isDisabled = false;

	// Properties -----------------------------------------------------------------------------------------------------

	private ResourceHandler wrapped;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Creates a new instance of this combined resource handler which wraps the given resource handler. This will also
	 * register this resource handler as a pre render view event listener, so that it can do the job of removing the
	 * CSS/JS resources and adding combined ones.
	 * @param wrapped The resource handler to be wrapped.
	 */
	public CombinedResourceHandler(ResourceHandler wrapped) {
		this.wrapped = wrapped;
		JSFUtil.getFacesContext().getApplication().subscribeToEvent(PreRenderViewEvent.class, this);
	}

//    @Override
//    public Resource createResource(String resourceName, String libraryName) {
//        Resource resource = super.createResource(resourceName, libraryName);
//
//        if(resource != null && libraryName != null ) {
//        	if( libraryName.equalsIgnoreCase("styles") ) {
//        		if( resourceName.equalsIgnoreCase("common.css") ) {
//            		return new AplosCssResource(resource, "1");
//        		} else if( resourceName.equalsIgnoreCase("modern.css") ) {
//            		return new AplosCssResource(resource, "1");
//        		} else {
//        			return new AplosCssResource(resource, null);
//        		}
//        	} else if( resourceName.equalsIgnoreCase("aploscommon.js") ) {
//        		return new AplosVersionedResource(resource, "1" );
//        	} else if( resourceName.equalsIgnoreCase("ckeditor/aplosckeditor.js") ) {
//        		return new AplosVersionedResource(resource, "1" );
//        	} else if( resourceName.equalsIgnoreCase("components.js") ) {
//        		return new AplosVersionedResource(resource, "1" );
//        	} else if( resourceName.equalsIgnoreCase("prettyphoto/css/prettyPhoto.css") ) {
//        		return new AplosCssResource(resource, null );
//        	}
//        } 
//        
//        return resource;
//    }

    @Override
    public ResourceHandler getWrapped() {
        return this.wrapped;
    }

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Returns true if the source is an instance of {@link UIViewRoot}.
	 */
	@Override
	public boolean isListenerForSource(Object source) {
		return (source instanceof UIViewRoot);
	}

	/**
	 * Before rendering of a freshly created view, perform the following actions:
	 * <ul>
	 * <li>Collect all component resources from the head.
	 * <li>Check and collect the script and stylesheet resources separately and remove them from the head.
	 * <li>If there are any resources in the collection of script and/or stylesheet resources, then create a
	 * component resource component pointing to the combined resource info and add it to the head at the location of
	 * the first resource.
	 * </ul>
	 */
	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		Website website = Website.getCurrentWebsiteFromTabSession();
//		website.setCombinedResourceStatus(CombinedResourceStatus.DISABLED);
		if( website != null ) { 
			repositionPackageCss(website);
			if( website.getCombinedResourceStatus() != null
				&& !CombinedResourceStatus.DISABLED.equals( website.getCombinedResourceStatus() ) ) {
				
				if( CombinedResourceStatus.FRONT_END_ONLY.equals( website.getCombinedResourceStatus() ) && !JSFUtil.determineIsFrontEnd() ) {
					return;
				} else if( CombinedResourceStatus.BACK_END_ONLY.equals( website.getCombinedResourceStatus() ) && JSFUtil.determineIsFrontEnd() ) {
					return;
				}
				
				FacesContext context = FacesContext.getCurrentInstance();
				UIViewRoot view = context.getViewRoot();
				CombinedResourceBuilder builder = new CombinedResourceBuilder();
		
				for (UIComponent componentResource : view.getComponentResources(context, TARGET_HEAD)) {
					if (componentResource.getAttributes().get("name") == null) {
						continue; // It's likely an inline script, they can't be combined as it might contain EL expressions.
					}
		
					builder.add(context, componentResource, TARGET_HEAD);
				}
		
				for (UIComponent componentResource : view.getComponentResources(context, TARGET_BODY)) {
					if (!(componentResource instanceof DeferredScript)) {
						continue; // We currently only head support deferred scripts. TODO: support body scripts as well?
					}
					builder.add(context, componentResource, TARGET_BODY);
				}
		
				builder.create(context);
			}
		}
	}
	
	public void repositionPackageCss( Website website ) {
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot view = context.getViewRoot();
		String packageCssName = website.getPackageName() + ".css";
		for (UIComponent componentResource : view.getComponentResources(context, TARGET_HEAD)) {
			if (componentResource.getAttributes().get("name").equals( packageCssName ) ) {
				view.removeComponentResource(context, componentResource);
				view.addComponentResource(context, componentResource);
				break;
			}
		}
	}
	
	/**
	 * Delegate to {@link #createResource(String, String, String)} with <code>null</code> as library name and content
	 * type.
	 */
	@Override
	public Resource createResource(String resourceName) {
		return createResource(resourceName, null, null);
	}

	/**
	 * Delegate to {@link #createResource(String, String, String)} with <code>null</code> as content type.
	 */
	@Override
	public Resource createResource(String resourceName, String libraryName) {
		return createResource(resourceName, libraryName, null);
	}

	@Override
	public Resource createResource(String resourceName, String libraryName, String contentType) {
		if (LIBRARY_NAME.equals(libraryName)) {
			return new CombinedResource(resourceName);
		} else {
			return super.createResource(resourceName, libraryName, contentType);
		}
	}

	// Inner classes --------------------------------------------------------------------------------------------------

	/**
	 * General builder to collect, exclude and suppress stylesheet and script component resources.
	 *
	 * @author Bauke Scholtz
	 */
	private final class CombinedResourceBuilder {

		// Constants --------------------------------------------------------------------------------------------------

		private static final String RENDERER_TYPE_CSS = "javax.faces.resource.Stylesheet";
		private static final String RENDERER_TYPE_JS = "javax.faces.resource.Script";
		private static final String EXTENSION_CSS = ".css";
		private static final String EXTENSION_JS = ".js";

		// General stylesheet/script builder --------------------------------------------------------------------------

		private CombinedResourceBuilder stylesheets;
		private CombinedResourceBuilder scripts;
		private Map<String, CombinedResourceBuilder> deferredScripts;
		private List<UIComponent> componentResourcesToRemove;

		public CombinedResourceBuilder() {
			stylesheets = new CombinedResourceBuilder(EXTENSION_CSS, TARGET_HEAD);
			scripts = new CombinedResourceBuilder(EXTENSION_JS, TARGET_HEAD);
			deferredScripts = new LinkedHashMap<String, CombinedResourceBuilder>(2);
			componentResourcesToRemove = new ArrayList<UIComponent>(3);
		}

		public void add(FacesContext context, UIComponent component, String target) {
			add(context, component, component.getRendererType(), new ResourceIdentifier(component), target);
		}

		private void add(FacesContext context, UIComponent component, String rendererType, ResourceIdentifier id, String target) {
			if (LIBRARY_NAME.equals(id.getLibrary())) {
				// Found an already combined resource. Extract and recombine it.
				String[] resourcePathParts = id.getName().split("\\.", 2)[0].split("/");
				String resourceId = resourcePathParts[resourcePathParts.length - 1];
				CombinedResourceInfo info = CombinedResourceInfo.getByCombinedResourceIds(resourceId);

				if (info != null) {
					for (ResourceIdentifier combinedId : info.getResourceIdentifiers()) {
						add(context, null, rendererType, combinedId, target);
					}
				}

				componentResourcesToRemove.add(component);
			}
			else if (rendererType.equals(RENDERER_TYPE_CSS)) {
				if (stylesheets.add(component, id)) {
					ResourceIdentifier.setMojarraResourceRendered(context, id); // Prevents future forced additions by libs.
				}
			}
			else if (rendererType.equals(RENDERER_TYPE_JS)) {
				if (ResourceIdentifier.isMojarraResourceRendered(context, id)) { // This is true when o:deferredScript is used.
					componentResourcesToRemove.add(component);
				}
				else if (scripts.add(component, id)) {
					ResourceIdentifier.setMojarraResourceRendered(context, id); // Prevents future forced additions by libs.
				}
			} else if (component instanceof DeferredScript) {
				String group = (String) component.getAttributes().get("group");
				CombinedResourceBuilder builder = deferredScripts.get(group);

				if (builder == null) {
					builder = new CombinedResourceBuilder(EXTENSION_JS, TARGET_BODY);
					deferredScripts.put(group, builder);
				}

				builder.add(component, id);
			}
			// --------------------------------------------------------------------------------------------------------
		}

		public void create(FacesContext context) {
			stylesheets.create(context, RENDERER_TYPE_CSS);
			scripts.create(context, RENDERER_TYPE_JS);

			for (CombinedResourceBuilder builder : deferredScripts.values()) {
				builder.create(context, DeferredScriptRenderer.RENDERER_TYPE);
			}

			removeComponentResources(context, componentResourcesToRemove, TARGET_HEAD);
		}

		// Specific stylesheet/script builder -------------------------------------------------------------------------

		private String extension;
		private String target;
		private CombinedResourceInfo.Builder infoBuilder;
		private UIComponent componentResource;

		private CombinedResourceBuilder(String extension, String target) {
			this.extension = extension;
			this.target = target;
			infoBuilder = new CombinedResourceInfo.Builder();
			componentResourcesToRemove = new ArrayList<UIComponent>(3);
		}

		private boolean add(UIComponent componentResource, ResourceIdentifier resourceIdentifier) {
			if (componentResource != null && !componentResource.isRendered()) {
				componentResourcesToRemove.add(componentResource);
			}
			else {
				infoBuilder.add(resourceIdentifier);

				if (this.componentResource == null) {
					this.componentResource = componentResource;
				} else {

					if (componentResource instanceof DeferredScript) {
						mergeAttribute(this.componentResource, componentResource, "onbegin");
						mergeAttribute(this.componentResource, componentResource, "onsuccess");
						mergeAttribute(this.componentResource, componentResource, "onerror");
					}
					
					componentResourcesToRemove.add(componentResource);
				}
			}
			return true;
		}

		private void mergeAttribute(UIComponent originalComponent, UIComponent newComponent, String name) {
			String originalAttribute = getAttribute(originalComponent, name);
			String newAttribute = getAttribute(newComponent, name);
			String separator = (originalAttribute.isEmpty() || originalAttribute.endsWith(";") ? "" : ";");
			originalComponent.getAttributes().put(name, originalAttribute + separator + newAttribute);
		}

		private String getAttribute(UIComponent component, String name) {
			String attribute = (String) component.getAttributes().get(name);
			return (attribute == null) ? "" : attribute.trim();
		}

		private void create(FacesContext context, String rendererType) {
			if (!infoBuilder.isEmpty()) {
				if (componentResource == null) {
					componentResource = new UIOutput();
					context.getViewRoot().addComponentResource(context, componentResource, target);
				}

				componentResource.getAttributes().put("library", LIBRARY_NAME);
				componentResource.getAttributes().put("name", infoBuilder.create(extension) + extension);
				componentResource.setRendererType(rendererType);
			}

			removeComponentResources(context, componentResourcesToRemove, target);
		}

	}

	private static void removeComponentResources(FacesContext context, List<UIComponent> componentResourcesToRemove, String target) {
		UIViewRoot view = context.getViewRoot();

		for (UIComponent resourceToRemove : componentResourcesToRemove) {
			if (resourceToRemove != null) {
				view.removeComponentResource(context, resourceToRemove, target);
			}
		}
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

}
