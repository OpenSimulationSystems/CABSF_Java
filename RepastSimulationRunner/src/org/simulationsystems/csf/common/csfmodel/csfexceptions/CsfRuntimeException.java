package org.simulationsystems.csf.common.csfmodel.csfexceptions;

public class CsfRuntimeException extends RuntimeException {

	public CsfRuntimeException() {
		super();
	}

	public CsfRuntimeException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CsfRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CsfRuntimeException(String arg0) {
		super(arg0);
	}

	public CsfRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
