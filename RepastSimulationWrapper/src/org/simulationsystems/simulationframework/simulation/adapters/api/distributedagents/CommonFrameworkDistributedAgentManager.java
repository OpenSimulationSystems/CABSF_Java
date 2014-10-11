package org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationAdapterAPI;

/*
 * Class to manage the distributed agents from other systems through the common simulation framework.  
 */
public class CommonFrameworkDistributedAgentManager {
	SimulationAdapterAPI simulationAdapterAPI;
	ConcurrentHashMap<UUID, AgentMapping> agentMappings = new ConcurrentHashMap<UUID, AgentMapping>();
	
	private agentMappingsDirectory;
	
	public CommonFrameworkDistributedAgentManager() {
	}

	public ConcurrentHashMap<UUID, AgentMapping> getAgentMappings() {
		return agentMappings;
	}

	/*
	 * Creates object to hold Agent Mappings between the simulation-side and
	 * distributed-agent-side agents. The actual setting of mapped objects occurs later.
	 * This method is best called during the initialization phase after reading the
	 * configuration.
	 */
	public AgentMapping createUninitializedAgentMapping(String fullyQualifiedSimulationAgentName,
			String fullyQualifiedDistributedAgentName) {
		UUID uuid = UUID.randomUUID();
		AgentMapping am = new AgentMapping(uuid, fullyQualifiedSimulationAgentName,
				fullyQualifiedDistributedAgentName);
		agentMappings.put(uuid, am);

		return am;
	}

}
