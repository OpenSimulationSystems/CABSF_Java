package org.simulationsystems.simulationframework.simulation.api.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import org.simulationsystems.simulationframework.simulation.api.SimulationRunGroupContext;
import org.simulationsystems.simulationframework.simulation.api.distributedagents.SimulationDistributedAgentManager;
import org.simulationsystems.simulationframework.simulation.api.distributedagents.SimulationDistributedAgentManager.CONFIGURATION_KEYS;

public class SimulationRunGroupConfiguration {
	// LOW: Support Multiple Simulation Run Configurations
	ArrayList<SimulationRunConfiguration> simulationRunConfigurations;

	private SimulationRunGroupContext simulationRunGroupContext;

	/*
	 * SimulationRunGroup Level.
	 */
	public SimulationRunGroupConfiguration(SimulationRunGroupContext simulationRunGroupContext) {
		// TODO: Read the actual values from the configuration file. Add methods to get into the
		// configuration values.
		this.simulationRunGroupContext = simulationRunGroupContext;

	}
	
	// LOW: see above
	public SimulationRunConfiguration getSimulationRunConfiguration() {
		return simulationRunConfigurations.get(0);

	}

}