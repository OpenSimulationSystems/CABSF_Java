package org.simulationsystems.csf.common.csfmodel;

/**
 * Stores the mapping of a simulation agent such as Repast Simphony agent with the
 * CSF-wide IDs which jointly uniquely identify an agent across the CSF: Distributed
 * System ID (Such as for a JADE MAS), Distributed Autonomous Agent ID (such as for a JADE
 * Agent), and Distributed Autonomous Agent Model ID (ID for the part of the JADE agent
 * that corresponds to the RepastS representational agent).
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class AgentMapping {

	/** The distributed system id. */
	private String distributedSystemID;

	/** The distributed autonomous agent id. */
	private String distributedAutonomousAgentID;

	/** The distributed autonomous agent model id. */
	private String distributedAutonomousAgentModelID;

	/** The fully qualified simulation agent. */
	private String fullyQualifiedSimulationAgent;

	/** The simulation agent. */
	Object simulationAgent;

	/** The fully qualified simulation agent name. */
	private String fullyQualifiedSimulationAgentName;

	/**
	 * Disabled constructor
	 */
	@SuppressWarnings("unused")
	private AgentMapping() {
	}

	// TODO: Remove fully qualified names
	/**
	 * Instantiates a new agent mapping.
	 * 
	 * @param distributedSystemID
	 *            the distributed system id
	 * @param distributedAutonomousAgentID
	 *            the distributed autonomous agent id
	 * @param distributedAgentModelID
	 *            the distributed agent model id
	 * @param fullyQualifiedSimulationAgentName
	 *            the fully qualified simulation agent name
	 */
	public AgentMapping(final String distributedSystemID,
			final String distributedAutonomousAgentID,
			final String distributedAgentModelID,
			final String fullyQualifiedSimulationAgentName) {
		this.distributedSystemID = distributedSystemID;
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
		this.distributedAutonomousAgentModelID = distributedAgentModelID;
		this.fullyQualifiedSimulationAgentName = fullyQualifiedSimulationAgentName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AgentMapping other = (AgentMapping) obj;
		if (distributedAutonomousAgentModelID == null) {
			if (other.distributedAutonomousAgentModelID != null)
				return false;
		} else if (!distributedAutonomousAgentModelID
				.equals(other.distributedAutonomousAgentModelID))
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

	/**
	 * Gets the distributed autonomous agent id.
	 * 
	 * @return the distributed autonomous agent id
	 */
	public String getDistributedAutonomousAgentID() {
		return distributedAutonomousAgentID;
	}

	/**
	 * Gets the distributed autonomous agent model id.
	 * 
	 * @return the distributed autonomous agent model id
	 */
	public String getDistributedAutonomousAgentModelID() {
		return distributedAutonomousAgentModelID;
	}

	/**
	 * Gets the distributed system id.
	 * 
	 * @return the distributed system id
	 */
	public String getDistributedSystemID() {
		return distributedSystemID;
	}

	/**
	 * Gets the fully qualified simulation agent.
	 * 
	 * @return the fully qualified simulation agent
	 */
	public String getFullyQualifiedSimulationAgent() {
		return fullyQualifiedSimulationAgent;
	}

	/**
	 * Gets the fully qualified simulation agent name.
	 * 
	 * @return the fully qualified simulation agent name
	 */
	public String getFullyQualifiedSimulationAgentName() {
		return fullyQualifiedSimulationAgentName;
	}

	/**
	 * Gets the simulation agent.
	 * 
	 * @return the simulation agent
	 */
	public Object getSimulationAgent() {
		return simulationAgent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((distributedAutonomousAgentModelID == null) ? 0
						: distributedAutonomousAgentModelID.hashCode());
		result = prime
				* result
				+ ((distributedAutonomousAgentID == null) ? 0
						: distributedAutonomousAgentID.hashCode());
		result = prime * result
				+ ((distributedSystemID == null) ? 0 : distributedSystemID.hashCode());
		return result;
	}

	/**
	 * Sets the distributed autonomous agent id.
	 * 
	 * @param distributedAutonomousAgentID
	 *            the new distributed autonomous agent id
	 */
	public void setDistributedAutonomousAgentID(final String distributedAutonomousAgentID) {
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;
	}

	/**
	 * Sets the distributed autonomous agent model id.
	 * 
	 * @param distributedAgentModelID
	 *            the new distributed autonomous agent model id
	 */
	public void setDistributedAutonomousAgentModelID(final String distributedAgentModelID) {
		this.distributedAutonomousAgentModelID = distributedAgentModelID;
	}

	/**
	 * Sets the simulation agent.
	 * 
	 * @param simulationAgent
	 *            the new simulation agent
	 */
	public void setSimulationAgent(final Object simulationAgent) {
		this.simulationAgent = simulationAgent;
	}
}
