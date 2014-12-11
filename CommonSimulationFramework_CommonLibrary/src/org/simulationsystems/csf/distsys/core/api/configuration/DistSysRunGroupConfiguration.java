package org.simulationsystems.csf.distsys.core.api.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager.CONFIGURATION_KEYS;

public class DistSysRunGroupConfiguration {
	// LOW: Support Multiple Simulation Run Configurations
	ArrayList<DistSysRunConfiguration> distSysRunConfigurations;

	private SimulationRunGroupContext simulationRunGroupContext;

	/*
	 * DistributedSystemSimulationRunGroup Level.
	 */
	public DistSysRunGroupConfiguration() {
		// TODO: Read the actual values from the configuration file. Add methods to get into the
		// configuration values.


	}
	
	// LOW: see above
	public DistSysRunConfiguration getSimulationRunConfiguration() {
		return distSysRunConfigurations.get(0);

	}

}
