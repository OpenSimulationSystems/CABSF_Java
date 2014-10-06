package org.simulationsystems.simulationframework.internal.api.distributedagents;

import java.util.UUID;

/*
 * A mapping of a Simulation Agent Prototype to a Concrete Distributed Agent.  For example, the Simulation Agent could be a Repast Agent, and the distributed again could be a JADE agent.
 */
public class AgentMapping {
	UUID uuid;
	private String fullyQualifiedSimulationAgent;
	Object simulationAgent;

	//Disabled
	private AgentMapping() {
	}
	
	public AgentMapping(UUID uuid, String fullyQualifiedSimulationAgent) {
		this.uuid = uuid;
		this.fullyQualifiedSimulationAgent = fullyQualifiedSimulationAgent;
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