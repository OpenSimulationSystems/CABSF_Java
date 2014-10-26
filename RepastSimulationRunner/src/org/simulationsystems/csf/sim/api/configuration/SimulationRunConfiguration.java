package org.simulationsystems.csf.sim.api.configuration;

import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.distributedagents.SimulationDistributedAgentManager;

public class SimulationRunConfiguration {
	private SimulationRunContext simulationRunContext;
	private SimulationDistributedAgentManager simulationDistributedAgentManager;
	private SimulationDistributedAgentManager agentManager;
	private String simulationDistributedAgentMessagingManagerStr;

	// TODO: Read actual simultion-run-level properties
	public SimulationRunConfiguration() {
		// Redis or "WebServices"
		simulationDistributedAgentMessagingManagerStr = "org.simulationsystems.csf.sim.internal.messaging.bridge.abstraction.RedisMessagingConcreteImplementor";
	}

	public String getSimulationDistributedAgentMessagingManagerStr() {
		return simulationDistributedAgentMessagingManagerStr;
	}

}
