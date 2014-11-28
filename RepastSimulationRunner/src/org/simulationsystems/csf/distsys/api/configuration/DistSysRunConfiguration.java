package org.simulationsystems.csf.distsys.api.configuration;

import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

public class DistSysRunConfiguration {
	public String getSimulationEngineID() {
		return simulationEngineID;
	}

	private SimulationRunContext simulationRunContext;
	private SimulationDistributedSystemManager simulationDistributedSystemManager;
	private SimulationDistributedSystemManager agentManager;
	private String commonMessagingConcreateImplStr=null;
	private String redisConnectionString=null;
	private String distributedSystemID=null;
	private String simulationEngineID=null;

	// TODO: Read actual simulation-run-level properties
	public DistSysRunConfiguration() {
		// String to represent Redis or "WebServices"
		commonMessagingConcreateImplStr = "org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation";
		redisConnectionString="localhost";
		distributedSystemID="DistSys1";
		simulationEngineID="19def3fa-a1d4-4996-a1ac-22c3a041e6ff";
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
