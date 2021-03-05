package org.fincl.miss.server.service.metadata;

import org.fincl.miss.server.exeption.ErrorConstant;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class UnusableServiceException extends ServiceMetadataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5077631851565862771L;

	/**
	 * 
	 * @param metadata
	 */
	public UnusableServiceException(final ServiceMetadata metadata) {

		super(ErrorConstant.UNUSABLE_SERVICE_ERROR, metadata);
	}

	/**
	 * 
	 * @param message
	 * @param metadata
	 */
	public UnusableServiceException(final String message, final ServiceMetadata metadata) {

		super(ErrorConstant.UNUSABLE_SERVICE_ERROR, message, metadata);
	}

	/**
	 * 
	 * @param cause
	 * @param metadata
	 */
	public UnusableServiceException(final Throwable cause, final ServiceMetadata metadata) {

		super(ErrorConstant.UNUSABLE_SERVICE_ERROR, cause, metadata);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param metadata
	 */
	public UnusableServiceException(final String message, final Throwable cause,
			final ServiceMetadata metadata) {

		super(ErrorConstant.UNUSABLE_SERVICE_ERROR, message, cause, metadata);
	}
}
