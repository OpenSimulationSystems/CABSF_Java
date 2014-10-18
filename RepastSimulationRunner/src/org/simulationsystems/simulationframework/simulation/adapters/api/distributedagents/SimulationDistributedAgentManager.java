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
public class SimulationDistributedAgentManager {
	private SimulationFrameworkContext simulationFrameworkContext;
	private ConcurrentHashMap<UUID, AgentMapping> agentMappings = new ConcurrentHashMap<UUID, AgentMapping>();
	private HashSet<UUID> agentsReadyForSimulationSideMapping = new HashSet<UUID>();
	private HashSet<UUID> agentsReadyForDistributedAgentMapping = new HashSet<UUID>();

	public enum CONFIGURATION_KEYS {
		DISTRIBUTED_AGENTS
	}

	// private agentMappingsDirectory;

	@SuppressWarnings("unused")
	private SimulationDistributedAgentManager() {
	}

	public SimulationDistributedAgentManager(SimulationFrameworkContext simulationFrameworkContext) {
		this.simulationFrameworkContext = simulationFrameworkContext;
	}

	protected ConcurrentHashMap<UUID, AgentMapping> getAgentMappings() {
		return agentMappings;
	}

	/*
	 * Creates objects to hold Agent Mappings between the simulation-side and distributed-agent-side
	 * agents. The actual setting of mapped objects occurs later on. See
	 * org.simulationsystems.simulationframework
	 * .simulation.adapters.simulationapps.api.distributedagents
	 * .RepastSimphonySimulationDistributedAgentManager for reference; <br/><br/>
	 * 
	 * It is preferred for Adapter authors to create a Simulation-Toolkit-specific class inheriting
	 * form this class. Its purpose is to convert generic "Object"s back to native
	 * Simulation-Toolkit-specific objects, which aids the API clients at compile time.
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
		AgentMapping am = null;
		try {
			UUID agentMappingToAssign = agentsReadyForSimulationSideMapping.iterator().next();
			am = agentMappings.get(agentMappingToAssign);
			am.setSimulationAgent(agent);
			agentsReadyForSimulationSideMapping.remove(agentMappingToAssign);
			agentsReadyForDistributedAgentMapping.add(agentMappingToAssign);
			System.out.println("Successfully mapped " + agentMappingToAssign.toString()
					+ " class: " + am.getClass().getCanonicalName());
		} catch (java.util.NoSuchElementException e) {
			System.out.println("exception:" + e.getMessage() + "  class: "
					+ agent.getClass().getCanonicalName());
		}

		return am;

	}
}
