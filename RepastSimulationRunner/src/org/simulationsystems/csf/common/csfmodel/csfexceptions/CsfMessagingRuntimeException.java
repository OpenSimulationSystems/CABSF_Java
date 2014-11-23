package org.simulationsystems.csf.common.csfmodel.csfexceptions;

public class CsfMessagingRuntimeException extends RuntimeException {

	public CsfMessagingRuntimeException() {
		super();
	}

	public CsfMessagingRuntimeException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CsfMessagingRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CsfMessagingRuntimeException(String arg0) {
		super(arg0);
	}

	public CsfMessagingRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
