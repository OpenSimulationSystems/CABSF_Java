package org.simulationsystems.csf.distsys.core.api.configuration;

import java.util.ArrayList;

import org.simulationsystems.csf.sim.core.api.SimulationRunGroupContext;

// TODO: Auto-generated Javadoc
/**
 * The Distributed System Run Group Configuration
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class DistSysRunGroupConfiguration {
	// LOW: Support Multiple Simulation Run Configurations
	// TODO: Get these from the configuration file
	/** The dist sys run configurations. */
	ArrayList<DistSysRunConfiguration> distSysRunConfigurations;

	/** The simulation run group context. */
	private SimulationRunGroupContext simulationRunGroupContext;

	/** The simulation engine id. */
	private final String simulationEngineID = "REPAST_SIMPHONY";

	/*
	 * DistributedSystemSimulationRunGroup Level.
	 */
	/**
	 * Instantiates a new DistSysRunGroupConfiguration
	 */
	public DistSysRunGroupConfiguration() {
		// TODO: Read the actual values from the configuration file. Add methods to get
		// into the
		// configuration values.

	}

	/**
	 * Gets the simulation engine id.
	 * 
	 * @return the simulation engine id
	 */
	public String getSimulationEngineID() {
		return simulationEngineID;
	}

	// LOW: see above
	/**
	 * Gets the simulation run configuration.
	 * 
	 * @return the simulation run configuration
	 */
	public DistSysRunConfiguration getSimulationRunConfiguration() {
		return distSysRunConfigurations.get(0);

	}

}
