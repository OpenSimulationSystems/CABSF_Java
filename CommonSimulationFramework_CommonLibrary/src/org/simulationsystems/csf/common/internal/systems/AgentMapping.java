package org.simulationsystems.csf.common.internal.systems;

import java.util.UUID;

/*
 * A mapping of a Simulation Agent Prototype to a Concrete Distributed Agent.  For example, the Simulation Agent could be a Repast Agent, and the distributed again could be a JADE agent.
 */
public class AgentMapping {
	private String distributedSystemID;
	private String distributedAutonomousAgentID;
	private String distributedAgentModelID;
	private String fullyQualifiedSimulationAgent;

	Object simulationAgent;
	private String fullyQualifiedSimulationAgentName;

	public String getDistributedAutonomousAgentID() {
		return distributedAutonomousAgentID;
	}

	public void setDistributedAutonomousAgentID(String distributedAutonomousAgentID) {
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
	}

	public String getDistributedAgentModelID() {
		return distributedAgentModelID;
	}

	public void setDistributedAgentModelID(String distributedAgentModelID) {
		this.distributedAgentModelID = distributedAgentModelID;
	}

	// Disabled
	@SuppressWarnings("unused")
	private AgentMapping() {
	}

	// TODO: Remnove fully qualified names
	public AgentMapping(String distributedSystemID, String distributedAutonomousAgentID,
			String distributedAgentModelID, String fullyQualifiedSimulationAgentName) {
		this.distributedSystemID = distributedSystemID;
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
		this.distributedAgentModelID = distributedAgentModelID;
		this.fullyQualifiedSimulationAgentName = fullyQualifiedSimulationAgentName;
	}

	public String getFullyQualifiedSimulationAgentName() {
		return fullyQualifiedSimulationAgentName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((distributedAgentModelID == null) ? 0 : distributedAgentModelID
						.hashCode());
		result = prime
				* result
				+ ((distributedAutonomousAgentID == null) ? 0
						: distributedAutonomousAgentID.hashCode());
		result = prime * result
				+ ((distributedSystemID == null) ? 0 : distributedSystemID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentMapping other = (AgentMapping) obj;
		if (distributedAgentModelID == null) {
			if (other.distributedAgentModelID != null)
				return false;
		} else if (!distributedAgentModelID.equals(other.distributedAgentModelID))
			return false;
		if (distributedAutonomousAgentID == null) {
			if (other.distributedAutonomousAgentID != null)
				return false;
		} else if (!distributedAutonomousAgentID
				.equals(other.distributedAutonomousAgentID))
			return false;
		if (distributedSystemID == null) {
			if (other.distributedSystemID != null)
				return false;
		} else if (!distributedSystemID.equals(other.distributedSystemID))
			return false;
		return true;
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
