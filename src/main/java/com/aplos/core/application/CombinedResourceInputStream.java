package com.aplos.core.application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.application.Resource;

import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

/**
 * This {@link InputStream} implementation takes care that all in the constructor given resources are been read in
 * sequence.
 * @author Bauke Scholtz
 */
final class CombinedResourceInputStream extends InputStream {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final byte[] CRLF = { '\r', '\n' };

	// Properties -----------------------------------------------------------------------------------------------------

	private List<InputStream> streams;
	private Iterator<InputStream> streamIterator;
	private InputStream currentStream;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Creates an instance of {@link CombinedResourceInputStream} based on the given resources. For each resource, the
	 * {@link InputStream} will be obtained and hold in an iterable collection.
	 * @param resources The resources to be read.
	 * @throws IOException If something fails at I/O level.
	 */
	public CombinedResourceInputStream(Set<Resource> resources) throws IOException {
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * For each resource, read until its {@link InputStream#read()} returns <code>-1</code> and then iterate to the
	 * {@link InputStream} of the next resource, if any available, else return <code>-1</code>.
	 */
	@Override
	public int read() throws IOException {
		int read = -1;

		while ((read = currentStream.read()) == -1) {
			if (streamIterator.hasNext()) {
				currentStream = streamIterator.next();
			}
			else {
				break;
			}
		}

		return read;
	}

	/**
	 * Closes the {@link InputStream} of each resource. Whenever the {@link InputStream#close()} throws an
	 * {@link IOException} for the first time, it will be caught and be thrown after all resources have been closed.
	 * Any {@link IOException} which is thrown by a subsequent close will be ignored by design.
	 */
	@Override
	public void close() throws IOException {
		IOException caught = null;

		for (InputStream stream : streams) {
			if( stream != null ) {
				IOException e = CommonUtil.close(stream);
	
				if (caught == null) {
					caught = e; // Don't throw it yet. We have to continue closing all other streams.
				}
			}
		}

		if (caught != null) {
			throw caught;
		}
	}

}
