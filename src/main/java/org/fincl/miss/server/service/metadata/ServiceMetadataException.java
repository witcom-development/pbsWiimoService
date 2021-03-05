package org.fincl.miss.server.service.metadata;

import com.dkitec.cfood.core.CfoodException;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class ServiceMetadataException extends CfoodException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1649317633275473960L;

	/**
	 * 
	 */
	private ServiceMetadata metadata;

	/**
	 * 
	 * @param code
	 * @param metadata
	 */
	protected ServiceMetadataException(final String code, final ServiceMetadata metadata) {

		super(code);

		this.metadata = metadata;
	}

	/**
	 * 
	 * @param code
	 * @param message
	 * @param metadata
	 */
	protected ServiceMetadataException(final String code, final String message,
			final ServiceMetadata metadata) {

		super(code, message);

		this.metadata = metadata;
	}

	/**
	 * 
	 * @param code
	 * @param cause
	 * @param metadata
	 */
	protected ServiceMetadataException(final String code, final Throwable cause,
			final ServiceMetadata metadata) {

		super(code, cause);

		this.metadata = metadata;
	}

	/**
	 * 
	 * @param code
	 * @param message
	 * @param cause
	 * @param metadata
	 */
	protected ServiceMetadataException(final String code, final String message,
			final Throwable cause, final ServiceMetadata metadata) {

		super(code, message, cause);

		this.metadata = metadata;
	}

	/**
	 * 
	 * @return
	 */
	public ServiceMetadata getServiceMetadata() {

		return metadata;
	}
}
