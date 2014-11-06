package org.simulationsystems.csf.distsys.api.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager.CONFIGURATION_KEYS;

public class DistributedSystemSimulationRunGroupConfiguration {
	// LOW: Support Multiple Simulation Run Configurations
	ArrayList<DistributedSystemSimulationRunConfiguration> distributedSystemSimulationRunConfigurations;

	private SimulationRunGroupContext simulationRunGroupContext;

	/*
	 * DistributedSystemSimulationRunGroup Level.
	 */
	public DistributedSystemSimulationRunGroupConfiguration() {
		// TODO: Read the actual values from the configuration file. Add methods to get into the
		// configuration values.


	}
	
	// LOW: see above
	public DistributedSystemSimulationRunConfiguration getSimulationRunConfiguration() {
		return distributedSystemSimulationRunConfigurations.get(0);

	}

}
