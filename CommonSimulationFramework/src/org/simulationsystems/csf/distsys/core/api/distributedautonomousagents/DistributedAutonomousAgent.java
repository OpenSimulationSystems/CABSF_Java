package org.simulationsystems.csf.distsys.core.api.distributedautonomousagents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;

/**
 * This is an object in the CSF that represents a distributed autonomous agent. It is not
 * the same as the distributed autonomous agent itself (such as a JADE agent). The
 * implementors of this API do not have direct references to the underlying distributed
 * autonomous agent.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class DistributedAutonomousAgent {

	/** The dist sys run context. */
	private DistSysRunContext distSysRunContext;

	/** The native distributed autonomous agent. */
	private Object nativeDistributedAutonomousAgent;

	/** The agents ready for distributed agent mapping. */
	private final HashSet<DistributedAutonomousAgent> agentsReadyForDistributedAgentMapping = new HashSet<DistributedAutonomousAgent>();

	/** The distributed agent model id sto agent models. */
	private final ConcurrentHashMap<String, DistributedAgentModel> distributedAgentModelIDStoAgentModels = new ConcurrentHashMap<String, DistributedAgentModel>();

	/** The distributed autonomous agent id. */
	private String distributedAutonomousAgentID;

	/**
	 * Instantiates a new distributed autonomous agent.
	 */
	@SuppressWarnings("unused")
	private DistributedAutonomousAgent() {
	}

	// TODO: Clean this up. We need a specific manager for the type of client (JADE
	// system, etc)
	/**
	 * The manager for a single distributed autonomous software agent (such as a JADE
	 * agent). This class manages the agent model mappings for that software agent.
	 * 
	 * @param distSysRunContext
	 *            the dist sys run context
	 * @param distributedAutonomousAgentID
	 *            the distributed autonomous agent id
	 * @param distributedAgentModelIDs
	 *            the distributed agent model i ds
	 * @param distributedAgentModelName
	 *            the distributed agent model name
	 */
	public DistributedAutonomousAgent(final DistSysRunContext distSysRunContext,
			final String distributedAutonomousAgentID,
			final Set<String> distributedAgentModelIDs,
			final String distributedAgentModelName) {
		// public DistributedAutonomousAgent(DistSysRunContext distSysRunContext,
		// String getCommonMessagingConcreteImplStr) {
		this.distSysRunContext = distSysRunContext;
		this.distributedAutonomousAgentID = distributedAutonomousAgentID;

		createDistributedAgentModels(distributedAgentModelIDs, distributedAgentModelName);
	}

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
	/**
	 * Creates the distributed agent models.
	 * 
	 * @param distributedAgentModelIDs
	 *            the distributed agent model i ds
	 * @param distributedAgentModelName
	 *            the distributed agent model name
	 * @return the concurrent hash map
	 */
	public ConcurrentHashMap<String, DistributedAgentModel> createDistributedAgentModels(
			final Set<String> distributedAgentModelIDs,
			final String distributedAgentModelName) {
		if (distributedAgentModelIDs.size() == 0) {
			distributedAgentModelIDs.add(UUID.randomUUID().toString());
		}

		final Set<DistributedAgentModel> hs = new HashSet<DistributedAgentModel>();
		for (final String distributedAgentModelID : distributedAgentModelIDs) {
			final DistributedAgentModel distributedAgentModel = new DistributedAgentModel(
					distSysRunContext, distributedAgentModelID, distributedAgentModelName);
			hs.add(distributedAgentModel);
			// TODO: Add validation
			distributedAgentModelIDStoAgentModels.put(distributedAgentModelID,
					distributedAgentModel);
		}

		return distributedAgentModelIDStoAgentModels;
	}

	/**
	 * Gets the maps that maps distributed agent model IDs to agent models.
	 * 
	 * @return the distributed agent model id sto agent models
	 */
	public ConcurrentHashMap<String, DistributedAgentModel> getDistributedAgentModelIDStoAgentModels() {
		return distributedAgentModelIDStoAgentModels;
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
	 * Gets the native distributed autonomous agent.
	 * 
	 * @return the native distributed autonomous agent
	 */
	public Object getNativeDistributedAutonomousAgent() {
		return nativeDistributedAutonomousAgent;
	}

	/**
	 * Log helper.
	 * 
	 * @return the object
	 */
	public Object logHelper() {
		return distributedAgentModelIDStoAgentModels;
	}

	/**
	 * Sets the native distributed autonomous agent.
	 * 
	 * @param nativeDistributedAutonomousAgent
	 *            the new native distributed autonomous agent
	 */
	public void setNativeDistributedAutonomousAgent(
			final Object nativeDistributedAutonomousAgent) {
		this.nativeDistributedAutonomousAgent = nativeDistributedAutonomousAgent;
		// TODO: Throw exception if null native object?

	}

	// TODO: Determine if this can be deleted
	/*
	 * public void messageDistributedAgents(FrameworkMessage frameworkMessage,
	 * SimulationRunContext simulationRunContext) { // TODO: Multiple Distributed systems
	 * commonMessagingAbstraction.sendMessageToDistributedAgents(frameworkMessage,
	 * distributedSystem, simulationRunContext); }
	 */

}
