package org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationFrameworkContext;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationAdapterAPI;

/*
 * Class to manage the distributed agents from other systems through the common simulation framework.  
 */
public class CommonFrameworkDistributedAgentManager {
	private SimulationFrameworkContext simulationFrameworkContext;
	ConcurrentHashMap<UUID, AgentMapping> agentMappings = new ConcurrentHashMap<UUID, AgentMapping>();
	HashSet<UUID> agentsReadyForSimulationSideMapping = new HashSet<UUID>();
	HashSet<UUID> agentsReadyForDistributedAgentMapping = new HashSet<UUID>();

	public enum CONFIGURATION_KEYS {
		DISTRIBUTED_AGENTS
	}

	// private agentMappingsDirectory;

	@SuppressWarnings("unused")
	private CommonFrameworkDistributedAgentManager() {
	}

	public CommonFrameworkDistributedAgentManager(
			SimulationFrameworkContext simulationFrameworkContext) {
		this.simulationFrameworkContext = simulationFrameworkContext;
	}

	public ConcurrentHashMap<UUID, AgentMapping> getAgentMappings() {
		return agentMappings;
	}

	/*
	 * Creates object to hold Agent Mappings between the simulation-side and
	 * distributed-agent-side agents. The actual setting of mapped objects occurs later
	 * on. This method should be called as part of the common simulation framework
	 * configuration.
	 */
	public AgentMapping createAgentMapping(String fullyQualifiedSimulationAgentName,
			String fullyQualifiedDistributedAgentName) {
		UUID uuid = UUID.randomUUID();
		AgentMapping am = new AgentMapping(uuid, fullyQualifiedSimulationAgentName,
				fullyQualifiedDistributedAgentName);
		agentMappings.put(uuid, am);
		agentsReadyForSimulationSideMapping.add(uuid);

		return am;
	}

	/*
	 * Assigns a Simulation Agent to an AgentMapping
	 */
	public AgentMapping addSimulationAgentToAgentMapping(Object agent) {
		// Add Validation to make sure mappings exist. / Throw exception

		// Take first available
		UUID agentMappingToAssign = agentsReadyForSimulationSideMapping.iterator().next();
		AgentMapping am = agentMappings.get(agentMappingToAssign);
		am.setSimulationAgent(agent);
		agentsReadyForSimulationSideMapping.remove(agentMappingToAssign);
		agentsReadyForDistributedAgentMapping.add(agentMappingToAssign);
		return am;

	}

}
