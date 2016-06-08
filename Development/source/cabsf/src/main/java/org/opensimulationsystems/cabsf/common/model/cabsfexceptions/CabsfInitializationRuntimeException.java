package org.opensimulationsystems.cabsf.common.model.cabsfexceptions;

/**
 * A runtime exception related to initialization of the CABSF.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class CabsfInitializationRuntimeException extends RuntimeException {

	/**
	 * Instantiates a new cabsf initialization runtime exception.
	 */
	public CabsfInitializationRuntimeException() {
		super();
	}

	/**
	 * Instantiates a new cabsf initialization runtime exception.
	 *
	 * @param arg0
	 *            the arg0
	 */
	public CabsfInitializationRuntimeException(final String arg0) {
		super(arg0);
	}

	/**
	 * Instantiates a new cabsf initialization runtime exception.
	 *
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public CabsfInitializationRuntimeException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instantiates a new cabsf initialization runtime exception.
	 *
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 * @param arg2
	 *            the arg2
	 * @param arg3
	 *            the arg3
	 */
	public CabsfInitializationRuntimeException(final String arg0, final Throwable arg1,
			final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * Instantiates a new cabsf initialization runtime exception.
	 *
	 * @param arg0
	 *            the arg0
	 */
	public CabsfInitializationRuntimeException(final Throwable arg0) {
		super(arg0);
	}

}
