package com.aplos.core.application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;

import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;


final class CombinedResourceInfo {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final Logger logger = Logger.getLogger(CombinedResourceHandler.class.getName());

	// ConcurrentHashMap was considered, but duplicate inserts technically don't harm and a HashMap is faster on read.
	private static final Map<String, CombinedResourceInfo> CACHE_BY_RESOURCE_IDS = new HashMap<String, CombinedResourceInfo>();
	private static final Map<String, CombinedResourceInfo> CACHE_BY_ASSIGNED_ID = new HashMap<String, CombinedResourceInfo>();

	private static final int DEFAULT_STREAM_BUFFER_SIZE = 10240;
	private static final String LOG_RESOURCE_NOT_FOUND = "CombinedResourceHandler: The resource %s cannot be found"
			+ " and therefore a 404 will be returned for the combined resource ID %s";

	// Properties -----------------------------------------------------------------------------------------------------

	private String assignedId;
	private String combinedResourcesId;
	private Set<ResourceIdentifier> resourceIdentifiers;
	private Set<Resource> resources;
	private int contentLength;
	private long lastModified;
	private String extension;
	
	private static long globalCombinedResourceId = 1;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Creates an instance of combined resource info based on the given ID and ordered set of resource identifiers.
	 * @param resourceIdentifiers Ordered set of resource identifiers, which are to be combined in a single resource.
	 */
	private CombinedResourceInfo(String combinedResourcesId, Long assignedId, Set<ResourceIdentifier> resourceIdentifiers) {
		this.combinedResourcesId = combinedResourcesId;
		this.assignedId = String.valueOf( assignedId );
		this.resourceIdentifiers = resourceIdentifiers;
	}

	/**
	 * Use this builder to create an instance of combined resource info and put it in the cache if absent.
	 * @author Bauke Scholtz
	 */
	public static final class Builder {

		// Constants --------------------------------------------------------------------------------------------------

		private static final String ERROR_EMPTY_RESOURCES =
			"There are no resources been added. Use add() method to add them or use isEmpty() to check beforehand.";

		// Properties -------------------------------------------------------------------------------------------------

		private Set<ResourceIdentifier> resourceIdentifiers = new LinkedHashSet<ResourceIdentifier>();

		// Actions ----------------------------------------------------------------------------------------------------

		/**
		 * Add the resource represented by the given resource identifier resources of this combined resource info. The
		 * insertion order is maintained and duplicates are filtered.
		 * @param resourceIdentifier The resource identifier of the resource to be added.
		 * @return This builder.
		 */
		public Builder add(ResourceIdentifier resourceIdentifier) {
			resourceIdentifiers.add(resourceIdentifier);
			return this;
		}

		/**
		 * Returns true if there are no resources been added. Use this method before {@link #create()} if it's unknown
		 * if there are any resources been added.
		 * @return True if there are no resources been added, otherwise false.
		 */
		public boolean isEmpty() {
			return resourceIdentifiers.isEmpty();
		}

		/**
		 * Creates the CombinedResourceInfo instance in cache if absent and return its ID.
		 * @return The ID of the CombinedResourceInfo instance.
		 * @throws IllegalStateException If there are no resources been added. So, to prevent it beforehand, use
		 * the {@link #isEmpty()} method to check if there are any resources been added.
		 */
		public String create(String extension, String assignedId) {
			if (resourceIdentifiers.isEmpty()) {
				throw new IllegalStateException(ERROR_EMPTY_RESOURCES);
			}
				
			String combinedResourcesId = getCombinedResourcesId(resourceIdentifiers);
			CombinedResourceInfo combinedResourceInfo = getByCombinedResourceIds(combinedResourcesId);

			if (combinedResourceInfo == null) {
				if (resourceIdentifiers != null) {
					combinedResourceInfo = new CombinedResourceInfo(combinedResourcesId, globalCombinedResourceId++, Collections.unmodifiableSet(resourceIdentifiers));
					combinedResourceInfo.setExtension(extension);
					if( CommonUtil.isNullOrEmpty( assignedId ) ) {
						assignedId = combinedResourceInfo.getAssignedId().toString();
					} else {
						combinedResourceInfo.setAssignedId(assignedId);
					}
					CACHE_BY_ASSIGNED_ID.put(assignedId, combinedResourceInfo);
					CACHE_BY_RESOURCE_IDS.put(combinedResourcesId, combinedResourceInfo);
				}
			}
			
			return combinedResourceInfo.getAssignedId();
		}

	}

	/**
	 * Returns the combined resource info identified by the given ID from the cache. A new one will be created based on
	 * the given ID if absent in cache.
	 * @param id The ID of the combined resource info to be returned from the cache.
	 * @return The combined resource info identified by the given ID from the cache.
	 */
	public static CombinedResourceInfo getByCombinedResourceIds(String resourceIds) {
		return CACHE_BY_RESOURCE_IDS.get(resourceIds);
	}
	
	public static CombinedResourceInfo getByAssignedId(String assignedId) {
		return CACHE_BY_ASSIGNED_ID.get(assignedId);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Lazily load the combined resources so that the set of resources, the total content length and the last modified
	 * are been initialized. If one of the resources cannot be resolved, then this will log a WARNING and leave the
	 * resources empty.
	 */
	private synchronized void loadResources() {
		if (!CommonUtil.isEmpty(resources)) {
			return;
		}

		FacesContext context = FacesContext.getCurrentInstance();
		ResourceHandler handler = context.getApplication().getResourceHandler();
		resources = new LinkedHashSet<Resource>();
		contentLength = 0;
		lastModified = 0;

		for (ResourceIdentifier resourceIdentifier : resourceIdentifiers) {
			Resource resource = handler.createResource(resourceIdentifier.getName(), resourceIdentifier.getLibrary());

			if (resource == null) {
				ApplicationUtil.handleError( new Exception( "Resource not found : " + resourceIdentifier.toString() ), false);
				continue;
			}

			resources.add(resource);

			try {
				URLConnection connection;

				connection = resource.getURL().openConnection();

				contentLength += connection.getContentLength();
				long lastModified = connection.getLastModified();

				if (lastModified > this.lastModified) {
					this.lastModified = lastModified;
				}
			}
			catch (IOException e) {
				// Can't and shouldn't handle it at this point.
				// It would be thrown during resource streaming anyway which is a better moment.
			}
		}
	}
	
	public InputStream getCombinedResourceStream() throws IOException {
		File viewFile = new File(CommonWorkingDirectory.COMBINED_RESOURCES.getDirectoryPath(true) + getAssignedId() + getExtension());
		
		if( !viewFile.exists() ) {
			StringBuffer combinedResourceStrBuf = new StringBuffer();
			boolean firstResource = true;
			
			for (Resource resource : resources) {
				if( firstResource ) {
					firstResource = false;
				} else {
					if( getExtension().equals( ".js" ) ) {
						combinedResourceStrBuf.append( ";" );
					}
					combinedResourceStrBuf.append( "\r\n" );
				}
				
				StringWriter writer = new StringWriter();
				
				
				IOUtils.copy(resource.getInputStream(), writer, "UTF-8");
				String resourceStr = writer.toString();
				if( resourceStr.contains( "../" ) ) {
					resourceStr = resourceStr.replace( "../", JSFUtil.getRequest().getContextPath() + "/resources/" + resource.getLibraryName() + "/" );
				}
				
//				if( getExtension().equals( ".js" ) ) {
//					String resourceName = resource.getResourceName().replace( "/", "_" );
//					File tempFile = new File(CommonWorkingDirectory.COMBINED_RESOURCES.getDirectoryPath(true) + resourceName );
//					CommonUtil.writeStringToFile( resourceStr, tempFile, true, false );
//					UglifyJs uglifyJs = new UglifyJs();
//					String[] args = new String[] { "-nc", tempFile.getAbsolutePath() };
//					String result = uglifyJs.uglify( args );
//					if( result != null ) {
//						resourceStr = result + ";";
//					}
//				}
				
				combinedResourceStrBuf.append( resourceStr );
			}
			
			String combinedResourceStr = combinedResourceStrBuf.toString();
			CommonUtil.writeStringToFile( combinedResourceStr, viewFile, true, false );
			return new StringInputStream( combinedResourceStr );
		}
		return new FileInputStream( viewFile );
	}

	/**
	 * Returns true if the given object is also an instance of {@link CombinedResourceInfo} and its ID equals to the
	 * ID of the current combined resource info instance.
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof CombinedResourceInfo)
			? ((CombinedResourceInfo) other).getAssignedId().equals(getAssignedId())
			: false;
	}

	/**
	 * Returns the sum of the hash code of this class and the ID.
	 */
	@Override
	public int hashCode() {
		return getClass().hashCode() + getAssignedId().hashCode();
	}

	/**
	 * Returns the string representation of this combined resource info in the format of
	 * <pre>CombinedResourceInfo[id,resourceIdentifiers]</pre>
	 * Where <code>id</code> is the unique ID and <code>resourceIdentifiers</code> is the ordered set of all resource
	 * identifiers as is been created with the builder.
	 */
	@Override
	public String toString() {
		return String.format("CombinedResourceInfo[%s,%s]", getAssignedId(), resourceIdentifiers);
	}

	// Getters --------------------------------------------------------------------------------------------------------

	/**
	 * Returns the ordered set of resource identifiers of this combined resource info.
	 * @return the ordered set of resource identifiers of this combined resource info.
	 */
	public Set<ResourceIdentifier> getResourceIdentifiers() {
		return resourceIdentifiers;
	}

	/**
	 * Returns the ordered set of resources of this combined resource info.
	 * @return The ordered set of resources of this combined resource info.
	 */
	public Set<Resource> getResources() {
		loadResources();
		return resources;
	}

	/**
	 * Returns the content length in bytes of this combined resource info.
	 * @return The content length in bytes of this combined resource info.
	 */
	public int getContentLength() {
		loadResources();
		return contentLength;
	}

	/**
	 * Returns the last modified timestamp in milliseconds of this combined resource info.
	 * @return The last modified timestamp in milliseconds of this combined resource info.
	 */
	public long getLastModified() {
		loadResources();
		return lastModified;
	}
	
	public static String getCombinedResourcesId(Set<ResourceIdentifier> resourceIdentifiers) {
		return StringUtils.join(resourceIdentifiers, "|");
	}
	

	/**
	 * Create an ordered set of resource identifiers based on the given unique ID. This does the reverse of
	 * {@link #toUniqueId(Map)}.
	 * @param id The unique ID of the set of resource identifiers.
	 * @return The set of resource identifiers based on the given unique ID, or <code>null</code> if the ID is not
	 * valid.
	 */
	private static Set<ResourceIdentifier> fromUniqueId(String id) {
		String resourcesId;

		try {
			String base64 = id.replace('-', '+').replace('_', '/') + "===".substring(0, id.length() % 4);
			ByteArrayInputStream bais = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(base64));
	        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        GZIPInputStream is = new GZIPInputStream(bais);
	        byte[] tmp = new byte[256];
	        while (true)
	        {
	            int r = is.read(tmp);
	            if (r < 0)
	            {
	                break;
	            }
	            buffer.write(tmp, 0, r);
	        }
	        is.close();

	        byte[] content = buffer.toByteArray();
	        resourcesId = new String(content, 0, content.length, "UTF-8");
		}
		catch (IOException e) {
			// This will occur when the ID has purposefully been manipulated for some reason.
			// Just return null then so that it will end up in a 404.
			return null;
		}

		Set<ResourceIdentifier> resourceIdentifiers = new LinkedHashSet<ResourceIdentifier>();

		for (String resourceIdentifier : resourcesId.split("\\|")) {
			resourceIdentifiers.add(new ResourceIdentifier(resourceIdentifier));
		}

		return resourceIdentifiers;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getAssignedId() {
		return assignedId;
	}

	public void setAssignedId(String assignedId) {
		this.assignedId = assignedId;
	}

	public String getCombinedResourcesId() {
		return combinedResourcesId;
	}

	public void setCombinedResourcesId(String combinedResourcesId) {
		this.combinedResourcesId = combinedResourcesId;
	}

}
