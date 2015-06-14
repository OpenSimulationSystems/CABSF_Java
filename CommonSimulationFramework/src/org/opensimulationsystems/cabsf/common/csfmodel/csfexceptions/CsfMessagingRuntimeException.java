package org.opensimulationsystems.cabsf.common.csfmodel.csfexceptions;

// TODO: Auto-generated Javadoc
/**
 * A runtime exception in the CSF related to messaging. *
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class CsfMessagingRuntimeException extends RuntimeException {

	/**
	 * Instantiates a new csf messaging runtime exception.
	 */
	public CsfMessagingRuntimeException() {
		super();
	}

	/**
	 * Instantiates a new csf messaging runtime exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public CsfMessagingRuntimeException(final String arg0) {
		super(arg0);
	}

	/**
	 * Instantiates a new csf messaging runtime exception.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	public CsfMessagingRuntimeException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Instantiates a new csf messaging runtime exception.
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
	public CsfMessagingRuntimeException(final String arg0, final Throwable arg1,
			final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * Instantiates a new csf messaging runtime exception.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public CsfMessagingRuntimeException(final Throwable arg0) {
		super(arg0);
	}

}
