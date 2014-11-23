package org.simulationsystems.csf.common.csfmodel.csfexceptions;

public class CsfInitializationRuntimeException extends RuntimeException {

	public CsfInitializationRuntimeException() {
		super();
	}

	public CsfInitializationRuntimeException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CsfInitializationRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CsfInitializationRuntimeException(String arg0) {
		super(arg0);
	}

	public CsfInitializationRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
