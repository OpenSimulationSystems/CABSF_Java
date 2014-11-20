package org.simulationsystems.csf.common.csfmodel.csfexceptions;

public class CsfSimulationRuntimeException extends RuntimeException {

	public CsfSimulationRuntimeException() {
		super();
	}

	public CsfSimulationRuntimeException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CsfSimulationRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CsfSimulationRuntimeException(String arg0) {
		super(arg0);
	}

	public CsfSimulationRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
