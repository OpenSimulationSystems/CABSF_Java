package org.simulationsystems.csf.sim.api.distributedagents;

import java.util.UUID;

/*
 * A mapping of a Simulation Agent Prototype to a Concrete Distributed Agent.  For example, the Simulation Agent could be a Repast Agent, and the distributed again could be a JADE agent.
 */
public class AgentMapping {
	UUID uuid;
	private String fullyQualifiedSimulationAgent;
	private String fullyQualifiedDistributedAgentName;

	Object simulationAgent;

	// Disabled
	@SuppressWarnings("unused")
	private AgentMapping() {
	}

	public AgentMapping(UUID uuid, String fullyQualifiedSimulationAgentName,
			String fullyQualifiedDistributedAgentName) {
		this.uuid = uuid;
		this.fullyQualifiedSimulationAgent = fullyQualifiedSimulationAgentName;
		this.fullyQualifiedDistributedAgentName = fullyQualifiedDistributedAgentName;
	}

	public String getFullyQualifiedSimulationAgent() {
		return fullyQualifiedSimulationAgent;
	}

	public Object getSimulationAgent() {
		return simulationAgent;
	}

	public void setSimulationAgent(Object simulationAgent) {
		this.simulationAgent = simulationAgent;
	}
}
