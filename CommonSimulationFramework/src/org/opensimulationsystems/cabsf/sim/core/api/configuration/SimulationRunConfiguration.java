package org.opensimulationsystems.cabsf.sim.core.api.configuration;

import org.opensimulationsystems.cabsf.common.model.SIMULATION_TYPE;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

/**
 * The configuration object for one simulation run.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class SimulationRunConfiguration {

	/** The simulation run context. */
	private SimulationRunContext simulationRunContext;

	/** The simulation distributed system manager. */
	private SimulationDistributedSystemManager simulationDistributedSystemManager;

	/** The agent manager. */
	private SimulationDistributedSystemManager agentManager;

	/** The common messaging concreate impl str. */
	private String commonMessagingConcreateImplStr = null;

	/** The redis connection string. */
	private String redisConnectionString = null;

	/** The simulation engine id. */
	private String simulationEngineID = null;
	// TODO: Read from the configuration
	/** The distributed system id. */
	private final String distributedSystemID = "DistSys1";

	/** The simulation type. */
	private SIMULATION_TYPE simulationType;

	// TODO: Read actual simulation-run-level properties
	/**
	 * Instantiates a new simulation run configuration.
	 */
	public SimulationRunConfiguration() {
		// String to represent Redis or "WebServices"
		commonMessagingConcreateImplStr = "org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation";
		redisConnectionString = "localhost";
		simulationEngineID = "19def3fa-a1d4-4996-a1ac-22c3a041e6ff";
	}

	/**
	 * Gets the common messaging concrete impl str.
	 * 
	 * @return the common messaging concrete impl str
	 */
	public String getCommonMessagingConcreteImplStr() {
		return commonMessagingConcreateImplStr;
	}

	/**
	 * Gets the distributed system id.
	 * 
	 * @return the distributed system id
	 */
	public String getDistributedSystemID() {
		return distributedSystemID;
	}

	/**
	 * Gets the Redis connection string.
	 * 
	 * @return the redis connection string
	 */
	public String getRedisConnectionString() {
		return redisConnectionString;
	}

	/**
	 * Gets the simulation engine id.
	 * 
	 * @return the simulation engine id
	 */
	public String getSimulationEngineID() {
		return simulationEngineID;
	}

	/**
	 * Gets the simulation type.
	 * 
	 * @return the simulation type
	 */
	public SIMULATION_TYPE getSimulationType() {
		return simulationType;
	}

	/**
	 * Sets the simulation type.
	 * 
	 * @param simulationType
	 *            the new simulation type
	 */
	public void setSimulationType(final SIMULATION_TYPE simulationType) {
		this.simulationType = simulationType;
	}

}
