package org.simulationsystems.csf.common.internal.systems;

import java.util.HashSet;
import java.util.UUID;

import org.simulationsystems.csf.common.csfmodel.AgentMapping;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;

// TODO: Auto-generated Javadoc
/**
 * The helper class for AgentMapping
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class AgentMappingHelper {

	/**
	 * Adds the native simulation agent (e.g. RepastS) to the AgentMapping object.
	 * 
	 * @param requestingSystem
	 *            the requesting system
	 * @param beforeMappingSet
	 *            the before mapping set
	 * @param afterMappingSet
	 *            the after mapping set
	 * @param agentObj
	 *            the agent obj
	 * @return the agent mapping
	 */
	public static AgentMapping addNativeSimulationToDistributedAutononmousAgentToAgentMapping(
			final String requestingSystem, final HashSet<AgentMapping> beforeMappingSet,
			final HashSet<AgentMapping> afterMappingSet, final Object agentObj) {
		AgentMapping am = null;
		try {
			// Take first available
			am = beforeMappingSet.iterator().next();
			am.setSimulationAgent(agentObj);
			beforeMappingSet.remove(am);
			afterMappingSet.add(am);
			System.out.println(requestingSystem + ": Successfully mapped Agent "
					+ am.getDistributedAutonomousAgentID() + " "
					+ am.getDistributedAutonomousAgentModelID() + " " + am.toString()
					+ " class: " + agentObj.getClass().getCanonicalName());
		} catch (final java.util.NoSuchElementException e) {
			throw new CsfInitializationRuntimeException("exception:" + e.getMessage()
					+ "  class: " + agentObj.getClass().getCanonicalName());
		}

		return am;

	}

	/**
	 * Creates objects to hold Agent Mappings between the simulation-side and CSF-wide
	 * string identifiers (to identify distributed autonomous agent models). The actual
	 * setting of mapped objects occurs later on. See
	 * org.simulationsystems.simulationframework
	 * .simulation.adapters.simulationapps.api.distributedagents
	 * .RepastSimphonySimulationDistributedAgentManager for reference;
	 * 
	 * @param hs
	 *            the hs
	 * @param distributedSystemID
	 *            the distributed system id
	 * @param distributedAutonomousAgentID
	 *            the distributed autonomous agent id
	 * @param distributedAgentModelID
	 *            the distributed agent model id
	 * @param fullyQualifiedSimulationAgentName
	 *            the fully qualified simulation agent name
	 * @return the agent mapping
	 */
	static public AgentMapping createAgentMapping(final HashSet<AgentMapping> hs,
			final String distributedSystemID, String distributedAutonomousAgentID,
			final String distributedAgentModelID,
			final String fullyQualifiedSimulationAgentName) {
		// TODO: How to handle agent ids not specified by the distributed system?
		if (distributedAutonomousAgentID == null)
			distributedAutonomousAgentID = UUID.randomUUID().toString();

		final AgentMapping am = new AgentMapping(distributedSystemID,
				distributedAutonomousAgentID, distributedAgentModelID,
				fullyQualifiedSimulationAgentName);
		hs.add(am);

		return am;
	}
}
