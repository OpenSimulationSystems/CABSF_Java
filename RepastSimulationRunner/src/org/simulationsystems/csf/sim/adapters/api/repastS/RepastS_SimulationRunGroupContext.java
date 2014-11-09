package org.simulationsystems.csf.sim.adapters.api.repastS;

import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;

public class RepastS_SimulationRunGroupContext {

	private SimulationRunGroupContext simulationRunGroupContext;
	
	public SimulationRunGroupContext getSimulationRunGroupContext() {
		return simulationRunGroupContext;
	}
	public RepastS_SimulationRunGroupContext(SimulationRunGroupContext simulationRunGroupContext) {
		this.simulationRunGroupContext = simulationRunGroupContext;
	}

}
