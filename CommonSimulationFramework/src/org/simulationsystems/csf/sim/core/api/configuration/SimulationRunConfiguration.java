package org.simulationsystems.csf.sim.core.api.configuration;

import org.simulationsystems.csf.common.csfmodel.SIMULATION_TYPE;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;
import org.simulationsystems.csf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

public class SimulationRunConfiguration {
	public SIMULATION_TYPE getSimulationType() {
		return simulationType;
	}

	public void setSimulationType(SIMULATION_TYPE simulationType) {
		this.simulationType = simulationType;
	}

	private SimulationRunContext simulationRunContext;
	private SimulationDistributedSystemManager simulationDistributedSystemManager;
	private SimulationDistributedSystemManager agentManager;
	private String commonMessagingConcreateImplStr=null;
	private String redisConnectionString=null;
	private String simulationEngineID=null;
	//TODO: Read from the configuration
	private String distributedSystemID="DistSys1";
	private SIMULATION_TYPE simulationType;
	
	public String getDistributedSystemID() {
		return distributedSystemID;
	}

	public String getSimulationEngineID() {
		return simulationEngineID;
	}

	// TODO: Read actual simulation-run-level properties
	public SimulationRunConfiguration() {
		// String to represent Redis or "WebServices"
		commonMessagingConcreateImplStr = "org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation";
		redisConnectionString="localhost";
		simulationEngineID="19def3fa-a1d4-4996-a1ac-22c3a041e6ff";
	}

	public String getCommonMessagingConcreteImplStr() {
		return commonMessagingConcreateImplStr;
	}
	
	public String getRedisConnectionString() {
		return redisConnectionString;
	}

}
