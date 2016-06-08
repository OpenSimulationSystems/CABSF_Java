package org.opensimulationsystems.cabsf.common.model;

/**
 * Stores the mapping of a simulation agent such as Repast Simphony agent with
 * the CABSF-wide IDs which jointly uniquely identify an agent across the CABSF:
 * Distributed System ID (Such as for a JADE MAS), Distributed Autonomous Agent
 * ID (such as for a JADE Agent), and Distributed Autonomous Agent Model ID (ID
 * for the part of the JADE agent that corresponds to the RepastS
 * representational agent).
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class AgentMapping {

    private String distributedSystemID;
    private String softwareAgentID;
    private String agentModelID;
    private String fullyQualifiedSimulationAgent;
    private Object simulationAgent;
    private String fullyQualifiedSimulationAgentName;

    /**
     * Disabled constructor.
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
        this.softwareAgentID = distributedAutonomousAgentID;
        this.agentModelID = distributedAgentModelID;
        this.fullyQualifiedSimulationAgentName = fullyQualifiedSimulationAgentName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgentMapping other = (AgentMapping) obj;
        if (agentModelID == null) {
            if (other.agentModelID != null) {
                return false;
            }
        } else if (!agentModelID.equals(other.agentModelID)) {
            return false;
        }
        if (softwareAgentID == null) {
            if (other.softwareAgentID != null) {
                return false;
            }
        } else if (!softwareAgentID.equals(other.softwareAgentID)) {
            return false;
        }
        if (distributedSystemID == null) {
            if (other.distributedSystemID != null) {
                return false;
            }
        } else if (!distributedSystemID.equals(other.distributedSystemID)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the agent model id.
     *
     * @return the agent model id
     */
    public String getAgentModelID() {
        return agentModelID;
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

    /**
     * Gets the software agent id.
     *
     * @return the software agent id
     */
    public String getSoftwareAgentID() {
        return softwareAgentID;
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
        result = prime * result + ((agentModelID == null) ? 0 : agentModelID.hashCode());
        result = prime * result
                + ((softwareAgentID == null) ? 0 : softwareAgentID.hashCode());
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
        this.softwareAgentID = distributedAutonomousAgentID;
    }

    /**
     * Sets the distributed autonomous agent model id.
     *
     * @param distributedAgentModelID
     *            the new distributed autonomous agent model id
     */
    public void setDistributedAutonomousAgentModelID(final String distributedAgentModelID) {
        this.agentModelID = distributedAgentModelID;
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
