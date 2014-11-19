package org.simulationsystems.csf.distsys.api;

import java.io.IOException;

import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;

public class DistributedSystemAdapterAPI {

	private String DISTRIBUTED_SYSTEM_NAME = "JADE";

	public DistributedSystemAdapterAPI() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SimulationRunGroupContext initializeAPI(String distributedSytemConfigurationFileName,
			String simulationToolName) throws IOException {
		DISTRIBUTED_SYSTEM_NAME  = simulationToolName.toUpperCase();

		// Initialize the Simulation's Configuration Properties
		SimulationRunGroupContext simulationRunContext = DistributedSystemInitializationHelper
				.initializeAPI(distributedSytemConfigurationFileName);

		return simulationRunContext;
	}

}
