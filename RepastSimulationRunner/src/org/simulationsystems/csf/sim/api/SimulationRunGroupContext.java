package org.simulationsystems.csf.sim.api;

import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.sim.api.configuration.SimulationRunGroupConfiguration;

public class SimulationRunGroupContext {
	private SimulationRunGroup simulationRunGroup;
	private SimulationRunGroupConfiguration simulationRunGroupConfiguration;
	
	// LOW: Support Multiple simulation run groups/run contexts
	private SimulationRunContext simulationRunContext;

	public SimulationRunGroupContext() {
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

	public void setSimulationRunContext(SimulationRunContext simulationRunContext) {
		this.simulationRunContext = simulationRunContext;
		
	}

}