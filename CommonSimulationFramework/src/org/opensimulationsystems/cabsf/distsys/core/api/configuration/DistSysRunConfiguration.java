package org.opensimulationsystems.cabsf.distsys.core.api.configuration;

import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

// TODO: Auto-generated Javadoc
/**
 * The Distributed System Run Configuration
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class DistSysRunConfiguration {

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

	/** The distributed system id. */
	private String distributedSystemID = null;

	/** The simulation engine id. */
	private String simulationEngineID = null;

	// TODO: Read actual simulation-run-level properties
	/**
	 * Instantiates a new DistSysRunConfiguration
	 */
	// TODO: Remove the hard-coding
	public DistSysRunConfiguration() {
		// String to represent Redis or "WebServices"
		commonMessagingConcreateImplStr = "org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation";
		redisConnectionString = "localhost";
		distributedSystemID = "DistSys1";
		simulationEngineID = "19def3fa-a1d4-4996-a1ac-22c3a041e6ff";
	}

	/**
	 * Gets the common messaging concrete impl string.
	 * 
	 * @return the common messaging concrete impl string
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
	 * Gets the redis connection string.
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

}
