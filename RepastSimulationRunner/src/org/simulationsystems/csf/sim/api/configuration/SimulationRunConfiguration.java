package org.simulationsystems.csf.sim.api.configuration;

import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

public class SimulationRunConfiguration {
	private SimulationRunContext simulationRunContext;
	private SimulationDistributedSystemManager simulationDistributedSystemManager;
	private SimulationDistributedSystemManager agentManager;
	private String CommonMessagingConcreateImpl;

	// TODO: Read actual simulation-run-level properties
	public SimulationRunConfiguration() {
		// String to represent Redis or "WebServices"
		CommonMessagingConcreateImpl = "org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction.RedisMessagingConcreteImplementor";
	}

	public String getCommonMessagingConcreateImpl() {
		return CommonMessagingConcreateImpl;
	}

}
