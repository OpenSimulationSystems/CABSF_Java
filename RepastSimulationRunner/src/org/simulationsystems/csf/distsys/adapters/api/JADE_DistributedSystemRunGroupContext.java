package org.simulationsystems.csf.distsys.adapters.api;

import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;

public class JADE_DistributedSystemRunGroupContext {

	private SimulationRunGroupContext simulationRunGroupContext;
	
	public SimulationRunGroupContext getSimulationRunGroupContext() {
		return simulationRunGroupContext;
	}
	public JADE_DistributedSystemRunGroupContext(SimulationRunGroupContext simulationRunGroupContext) {
		this.simulationRunGroupContext = simulationRunGroupContext;
	}

}
