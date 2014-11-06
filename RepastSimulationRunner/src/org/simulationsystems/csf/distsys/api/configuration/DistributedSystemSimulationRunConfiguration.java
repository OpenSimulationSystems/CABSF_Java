package org.simulationsystems.csf.distsys.api.configuration;

import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

public class DistributedSystemSimulationRunConfiguration {
	private SimulationRunContext simulationRunContext;
	private SimulationDistributedSystemManager simulationDistributedSystemManager;
	private SimulationDistributedSystemManager agentManager;
	private String commonMessagingConcreateImplStr=null;
	private String redisConnectionString=null;

	// TODO: Read actual simulation-run-level properties
	public DistributedSystemSimulationRunConfiguration() {
		// String to represent Redis or "WebServices"
		commonMessagingConcreateImplStr = "org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation";
		redisConnectionString="localhost";
	}

	public String getCommonMessagingConcreteImplStr() {
		return commonMessagingConcreateImplStr;
	}
	
	public String getRedisConnectionString() {
		return redisConnectionString;
	}

}
