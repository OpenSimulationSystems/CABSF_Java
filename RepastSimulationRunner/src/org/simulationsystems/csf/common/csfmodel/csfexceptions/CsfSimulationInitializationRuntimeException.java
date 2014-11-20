package org.simulationsystems.csf.common.csfmodel.csfexceptions;

public class CsfSimulationInitializationRuntimeException extends RuntimeException {

	public CsfSimulationInitializationRuntimeException() {
		super();
	}

	public CsfSimulationInitializationRuntimeException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CsfSimulationInitializationRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CsfSimulationInitializationRuntimeException(String arg0) {
		super(arg0);
	}

	public CsfSimulationInitializationRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
