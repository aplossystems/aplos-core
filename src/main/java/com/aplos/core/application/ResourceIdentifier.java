package com.aplos.core.application;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class ResourceIdentifier {

	// Properties -----------------------------------------------------------------------------------------------------

	private String library;
	private String name;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Create a new instance based on given standard JSF resource identifier string format <code>library:name</code>.
	 * @param resourceIdentifier The standard JSF resource identifier.
	 */
	public ResourceIdentifier(String resourceIdentifier) {
		String[] parts = resourceIdentifier.split(":");
		library = (parts.length > 1) ? parts[0] : null;
		name = parts[parts.length -1 ];
	}
	
	public static void setMojarraResourceRendered(FacesContext context, ResourceIdentifier id) {
		context.getAttributes().put(id.getName() + id.getLibrary(), true);
	}

	public static boolean isMojarraResourceRendered(FacesContext context, ResourceIdentifier id) {
		return context.getAttributes().containsKey(id.getName() + id.getLibrary());
	}

	/**
	 * Create a new instance based on library and name attributes of the given component resource.
	 * @param componentResource The component resource.
	 */
	public ResourceIdentifier(UIComponent componentResource) {
		Map<String, Object> attributes = componentResource.getAttributes();
		library = (String) attributes.get("library");
		name = (String) attributes.get("name");
	}

	/**
	 * Create a new instance based on given resource library and name.
	 * @param library The resource lirbary.
	 * @param name The resource name.
	 */
	public ResourceIdentifier(String library, String name) {
		this.library = library;
		this.name = name;
	}

	// Getters --------------------------------------------------------------------------------------------------------

	/**
	 * Returns the resource library.
	 * @return The resource library.
	 */
	public String getLibrary() {
		return library;
	}

	/**
	 * Returns the resource name.
	 * @return The resource name.
	 */
	public String getName() {
		return name;
	}

	// Object overrides -----------------------------------------------------------------------------------------------

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;

		ResourceIdentifier other = (ResourceIdentifier) object;
        if (library == null ? other.library != null : !library.equals(other.library)) return false;
        if (name == null ? other.name != null : !name.equals(other.name)) return false;

        return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((library == null) ? 0 : library.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Returns the resource identifier as string in standard JSF resource identifier format <code>library:name</code>.
	 * If there is no library, then only the name is returned without the colon separator like so <code>name</code>.
	 */
	@Override
	public String toString() {
		return (library != null ? (library + ":") : "") + name;
	}

}
