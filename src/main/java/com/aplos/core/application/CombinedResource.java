package com.aplos.core.application;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.aplos.common.utils.JSFUtil;

public class CombinedResource extends Resource {

	// Properties -----------------------------------------------------------------------------------------------------

	private long lastModified;
	private static final String PATTERN_RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";
	private static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone("GMT");
	
	private CombinedResourceInfo info;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Constructs a new combined resource based on the given resource name. This constructor is only used by
	 * {@link CombinedResourceHandler#createResource(String, String)}.
	 * @param resourceName The resource name of the combined resource.
	 */
	public CombinedResource(String resourceName) {
		this(resourceName, CombinedResourceHandler.LIBRARY_NAME, JSFUtil.getMimeType(JSFUtil.getFacesContext(),resourceName));
		String[] resourcePathParts = resourceName.split("\\.", 2)[0].split("/");
		String resourceId = resourcePathParts[resourcePathParts.length - 1];
		info = CombinedResourceInfo.getByAssignedId(resourceId);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	public long getLastModified() {
		return (info != null) ? info.getLastModified() : lastModified;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (info != null && !info.getResources().isEmpty()) {
			return info.getCombinedResourceStream();
		}
		else {
			return null;
		}
	}

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Constructs a new dynamic resource based on the given resource name, library name and content type.
	 * @param resourceName The resource name.
	 * @param libraryName The library name.
	 * @param contentType The content type.
	 */
	public CombinedResource(String resourceName, String libraryName, String contentType) {
		setResourceName(resourceName);
		setLibraryName(libraryName);
		setContentType(contentType);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	@Override
	public String getRequestPath() {
		String mapping = getMapping(JSFUtil.getExternalContext());
		String path = ResourceHandler.RESOURCE_IDENTIFIER + "/" + getResourceName();
		return JSFUtil.getExternalContext().getRequestContextPath()
			+ (isPrefixMapping(mapping) ? (mapping + path) : (path + mapping))
			+ "?ln=" + getLibraryName()
			+ "&v=" + getLastModified();
	}

	@Override
	public URL getURL() {
		try {
			// Yes, this returns a HTTP URL, not a classpath URL. There's no other way anyway as dynamic resources are not present in classpath..
			return new URL(getRequestDomainURL(JSFUtil.getRequest()) + getRequestPath());
		}
		catch (MalformedURLException e) {
			// This exception should never occur.
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, String> getResponseHeaders() {
		Map<String, String> responseHeaders = new HashMap<String, String>(4);
		responseHeaders.put("Last-Modified", formatRFC1123(new Date(getLastModified())));
		responseHeaders.put("Expires", formatRFC1123(new Date(System.currentTimeMillis() + getDefaultResourceMaxAge())));
		responseHeaders.put("Etag", String.format("W/\"%d-%d\"", getResourceName().hashCode(), getLastModified()));
		responseHeaders.put("Pragma", ""); // Explicitly set empty pragma to prevent some containers from setting it.
		return responseHeaders;
	}

	/**
	 * Sets the "last modified" timestamp of this resource.
	 * @param lastModified The "last modified" timestamp of this resource.
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public boolean userAgentNeedsUpdate(FacesContext context) {
		String ifModifiedSince = context.getExternalContext().getRequestHeaderMap().get("If-Modified-Since");

		if (ifModifiedSince != null) {
			try {
				return getLastModified() > parseRFC1123(ifModifiedSince).getTime() + 1000; // RFC1123 doesn't store millis.
			}
			catch (ParseException ignore) {
				return true;
			}
		}

		return true;
	}
	
	public static String formatRFC1123(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_RFC1123_DATE, Locale.UK);
		sdf.setTimeZone(TIMEZONE_GMT);
		return sdf.format(date);
	}
	
	public static Date parseRFC1123(String string) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_RFC1123_DATE, Locale.UK);
		return sdf.parse(string);
	}
	
	public static String getMapping(ExternalContext externalContext) {

		if (externalContext.getRequestPathInfo() == null) {
			String path = externalContext.getRequestServletPath();
			return path.substring(path.lastIndexOf('.'));
		}
		else {
			return externalContext.getRequestServletPath();
		}
	}
	
	public static boolean isPrefixMapping(String mapping) {
		return (mapping.charAt(0) == '/');
	}
	
	public static String getRequestDomainURL(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		return url.substring(0, url.length() - request.getRequestURI().length());
	}
	
	public static long getDefaultResourceMaxAge() {
		return 604800000L;
	}

}
