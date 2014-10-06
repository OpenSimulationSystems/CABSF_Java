package org.simulationsystems.simulationframework.internal.api.distributedagents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.simulationframework.internal.api.SimulationAdapterAPI;

/*
 * Class to manage the distributed agents from other systems through the common simulation framework.  
 */
public class CommonFrameworkDistributedAgentManager {
	SimulationAdapterAPI simulationAdapterAPI;
	ConcurrentHashMap<UUID, AgentMapping> agentMappings = new ConcurrentHashMap<UUID, AgentMapping>();
	
	public CommonFrameworkDistributedAgentManager() {
	}
	
	public ConcurrentHashMap<UUID, AgentMapping> getAgentMappings() {
		return agentMappings;
	}

	/*
	 * Creates a new agent mapping
	 */
	public AgentMapping createAgentMapping(String fullyQualifiedSimulationAgent) {
		UUID uuid = UUID.randomUUID();
		AgentMapping am = new AgentMapping(uuid,fullyQualifiedSimulationAgent);
		agentMappings.put(uuid,am);
		
		return am;
	}

}
