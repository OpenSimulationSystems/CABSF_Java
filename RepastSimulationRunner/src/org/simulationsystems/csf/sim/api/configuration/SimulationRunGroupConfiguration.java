package org.simulationsystems.csf.sim.api.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager.CONFIGURATION_KEYS;

public class SimulationRunGroupConfiguration {
	// TODO: Actually populate these values
	// LOW: Support Multiple Simulation Run Configurations
	ArrayList<SimulationRunConfiguration> simulationRunConfigurations;

	/*
	 * DistributedSystemSimulationRunGroup Level.
	 */
	public SimulationRunGroupConfiguration() {
		// TODO: Read the actual values from the configuration file. Add methods to get into the
		// configuration values.


	}
	
	// LOW: see above
	public SimulationRunConfiguration getSimulationRunConfiguration() {
		return simulationRunConfigurations.get(0);

	}

}