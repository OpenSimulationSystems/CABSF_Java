package org.simulationsystems.csf.sim.api.configuration;

import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

public class SimulationRunConfiguration {
	private SimulationRunContext simulationRunContext;
	private SimulationDistributedSystemManager simulationDistributedSystemManager;
	private SimulationDistributedSystemManager agentManager;
	private String commonMessagingConcreateImplStr=null;
	private String redisConnectionString=null;

	// TODO: Read actual simulation-run-level properties
	public SimulationRunConfiguration() {
		// String to represent Redis or "WebServices"
		commonMessagingConcreateImplStr = "org.simulationsystems.csf.sim.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation";
		redisConnectionString="localhost";
	}

	public String getCommonMessagingConcreteImplStr() {
		return commonMessagingConcreateImplStr;
	}
	
	public String getRedisConnectionString() {
		return redisConnectionString;
	}

}
