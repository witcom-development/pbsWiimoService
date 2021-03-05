package org.fincl.miss.server.service.metadata;

import org.fincl.miss.server.exeption.ErrorConstant;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class UnserviceableTimeException extends ServiceMetadataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3330924892400491434L;

	/**
	 * 
	 * @param metadata
	 */
	public UnserviceableTimeException(final ServiceMetadata metadata) {

		super(ErrorConstant.UNSERVICEABLE_TIME_ERROR, metadata);
	}

	/**
	 * 
	 * @param message
	 * @param metadata
	 */
	public UnserviceableTimeException(final String message, final ServiceMetadata metadata) {

		super(ErrorConstant.UNSERVICEABLE_TIME_ERROR, message, metadata);
	}

	/**
	 * 
	 * @param cause
	 * @param metadata
	 */
	public UnserviceableTimeException(final Throwable cause, final ServiceMetadata metadata) {

		super(ErrorConstant.UNSERVICEABLE_TIME_ERROR, cause, metadata);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param metadata
	 */
	public UnserviceableTimeException(final String message, final Throwable cause,
			final ServiceMetadata metadata) {

		super(ErrorConstant.UNSERVICEABLE_TIME_ERROR, message, cause, metadata);
	}
}
