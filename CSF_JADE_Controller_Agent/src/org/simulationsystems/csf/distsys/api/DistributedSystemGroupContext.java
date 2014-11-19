package org.simulationsystems.csf.distsys.api;

import org.simulationsystems.csf.sim.api.configuration.SimulationRunGroupConfiguration;

public class DistributedSystemGroupContext {
	private SimulationRunGroup simulationRunGroup;
	private SimulationRunGroupConfiguration simulationRunGroupConfiguration;
	
	// LOW: Support Multiple simulation run groups/run contexts
	private SimulationRunContext simulationRunContext;

	public DistributedSystemGroupContext() {
	}

	public SimulationRunGroupConfiguration getSimulationConfiguration() {
		return simulationRunGroupConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	public void setSimulationRunGroup(SimulationRunGroup simulationRunGroup) {
		this.simulationRunGroup = simulationRunGroup;
	}

	public void setSimulationConfiguration(SimulationRunGroupConfiguration simulationRunGroupConfiguration) {
		this.simulationRunGroupConfiguration = simulationRunGroupConfiguration; 
		
	}

	public void setSimulationRunGroupContext(SimulationRunContext simulationRunContext) {
		this.simulationRunContext = simulationRunContext;
		
	}

}
