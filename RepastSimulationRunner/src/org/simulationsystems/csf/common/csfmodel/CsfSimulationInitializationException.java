package org.simulationsystems.csf.common.csfmodel;

public class CsfSimulationInitializationException extends RuntimeException {

	public CsfSimulationInitializationException() {
		super();
	}

	public CsfSimulationInitializationException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CsfSimulationInitializationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CsfSimulationInitializationException(String arg0) {
		super(arg0);
	}

	public CsfSimulationInitializationException(Throwable arg0) {
		super(arg0);
	}

}
