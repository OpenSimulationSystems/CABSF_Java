package org.simulationsystems.csf.common.internal.systems;

import java.util.HashSet;
import java.util.UUID;

public class AgentMappingHelper {

	/*
	 * Creates objects to hold Agent Mappings between the simulation-side and
	 * distributed-agent-side agents. The actual setting of mapped objects occurs later
	 * on. See org.simulationsystems.simulationframework
	 * .simulation.adapters.simulationapps.api.distributedagents
	 * .RepastSimphonySimulationDistributedAgentManager for reference; <br/><br/>
	 * 
	 * It is preferred for Adapter authors to create a Simulation-Toolkit-specific class
	 * inheriting form this class. Its purpose is to convert generic "Object"s back to
	 * native Simulation-Toolkit-specific objects, which aids the API clients at compile
	 * time.
	 */
	static public AgentMapping createAgentMapping(HashSet<AgentMapping> hs,
			String distributedSystemID, String distributedAutonomousAgentID,
			String distributedAgentModelID, String fullyQualifiedSimulationAgentName) {
		// TODO: How to handle agent ids not specified by the distributed system?
		if (distributedAutonomousAgentID == null)
			distributedAutonomousAgentID = UUID.randomUUID().toString();

		AgentMapping am = new AgentMapping(distributedSystemID,
				distributedAutonomousAgentID, distributedAgentModelID,
				fullyQualifiedSimulationAgentName);
		hs.add(am);

		return am;
	}

	public static AgentMapping addNativeSimulationToDistributedAutononmousAgentToAgentMapping(String requestingSystem,
			HashSet<AgentMapping> beforeMappingSet,
			HashSet<AgentMapping> afterMappingSet, Object agentObj) {
		AgentMapping am = null;
		try {
			// Take first available
			am = beforeMappingSet.iterator().next();
			am.setSimulationAgent(agentObj);
			beforeMappingSet.remove(am);
			afterMappingSet.add(am);
			System.out
					.println(requestingSystem + ": Successfully mapped Agent "
							+ am.toString() + " class: "
							+ agentObj.getClass().getCanonicalName());
		} catch (java.util.NoSuchElementException e) {
			System.out.println("exception:" + e.getMessage() + "  class: "
					+ agentObj.getClass().getCanonicalName());
		}

		return am;

	}
}
