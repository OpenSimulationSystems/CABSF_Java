package org.simulationsystems.csf.distsys.api.configuration;

import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

public class DistSysRunConfiguration {
	private SimulationRunContext simulationRunContext;
	private SimulationDistributedSystemManager simulationDistributedSystemManager;
	private SimulationDistributedSystemManager agentManager;
	private String commonMessagingConcreateImplStr=null;
	private String redisConnectionString=null;
	private String distributedSystemID=null;

	// TODO: Read actual simulation-run-level properties
	public DistSysRunConfiguration() {
		// String to represent Redis or "WebServices"
		commonMessagingConcreateImplStr = "org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation";
		redisConnectionString="localhost";
		distributedSystemID="DistSys1";
	}

	public String getDistributedSystemID() {
		return distributedSystemID;
	}

	public String getCommonMessagingConcreteImplStr() {
		return commonMessagingConcreateImplStr;
	}
	
	public String getRedisConnectionString() {
		return redisConnectionString;
	}

}
