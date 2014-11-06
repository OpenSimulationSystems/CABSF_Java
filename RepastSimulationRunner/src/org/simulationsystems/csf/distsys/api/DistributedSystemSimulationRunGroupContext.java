package org.simulationsystems.csf.distsys.api;

import org.simulationsystems.csf.common.csfmodel.SimulationRunGroup;
import org.simulationsystems.csf.distsys.api.configuration.DistributedSystemSimulationRunGroupConfiguration;

public class DistributedSystemSimulationRunGroupContext {
	private SimulationRunGroup simulationRunGroup;
	private DistributedSystemSimulationRunGroupConfiguration distributedSystemSimulationRunGroupConfiguration;
	
	// LOW: Support Multiple simulation run groups/run contexts
	private DistributedSystemSimulationRunContext distributedSystemSimulationRunContext;

	public DistributedSystemSimulationRunGroupContext() {
	}

	public DistributedSystemSimulationRunGroupConfiguration getSimulationConfiguration() {
		return distributedSystemSimulationRunGroupConfiguration;
	}

	public SimulationRunGroup getSimulationRunGroup() {
		return simulationRunGroup;
	}

	public void setSimulationRunGroup(SimulationRunGroup distributedSystemSimulationRunGroup) {
		this.simulationRunGroup = distributedSystemSimulationRunGroup;
	}

	public void setSimulationConfiguration(DistributedSystemSimulationRunGroupConfiguration DistributedSystemSimulationRunGroupConfiguration) {
		this.distributedSystemSimulationRunGroupConfiguration = distributedSystemSimulationRunGroupConfiguration; 
		
	}

	public void setSimulationRunContext(DistributedSystemSimulationRunContext distributedSystemSimulationRunContext) {
		this.distributedSystemSimulationRunContext = distributedSystemSimulationRunContext;
		
	}

}
