package org.simulationsystems.simulationframework.simulation.api.configuration;

import org.simulationsystems.simulationframework.simulation.api.SimulationRunContext;
import org.simulationsystems.simulationframework.simulation.api.distributedagents.SimulationDistributedAgentManager;

public class SimulationRunConfiguration {
	private SimulationRunContext simulationRunContext;
	private SimulationDistributedAgentManager simulationDistributedAgentManager;
	private SimulationDistributedAgentManager agentManager;
	private String simulationDistributedAgentMessagingManagerStr;

	// TODO: Read actual simultion-run-level properties
	public SimulationRunConfiguration() {
		// Redis or "WebServices"
		simulationDistributedAgentMessagingManagerStr = "org.simulationsystems.simulationframework.simulation.internal.messaging.bridge.abstraction.RedisMessagingConcreteImplementor";
	}

	public String getSimulationDistributedAgentMessagingManagerStr() {
		return simulationDistributedAgentMessagingManagerStr;
	}

}
