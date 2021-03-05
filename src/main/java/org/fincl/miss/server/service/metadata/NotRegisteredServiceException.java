package org.fincl.miss.server.service.metadata;

import org.fincl.miss.server.exeption.ErrorConstant;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class NotRegisteredServiceException extends ServiceMetadataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3118725060162343843L;

	/**
	 * 
	 * @param metadata
	 */
	public NotRegisteredServiceException(final ServiceMetadata metadata) {

		super(ErrorConstant.NOT_REGISTERED_SERVICE_ERROR, metadata);
	}

	/**
	 * 
	 * @param message
	 * @param metadata
	 */
	public NotRegisteredServiceException(final String message, final ServiceMetadata metadata) {

		super(ErrorConstant.NOT_REGISTERED_SERVICE_ERROR, message, metadata);
	}

	/**
	 * 
	 * @param cause
	 * @param metadata
	 */
	public NotRegisteredServiceException(final Throwable cause, final ServiceMetadata metadata) {

		super(ErrorConstant.NOT_REGISTERED_SERVICE_ERROR, cause, metadata);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param metadata
	 */
	public NotRegisteredServiceException(final String message, final Throwable cause,
			final ServiceMetadata metadata) {

		super(ErrorConstant.NOT_REGISTERED_SERVICE_ERROR, message, cause, metadata);
	}
}
