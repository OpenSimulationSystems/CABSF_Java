package org.opensimulationsystems.cabsf.distsys.core.api.softwareagents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.opensimulationsystems.cabsf.common.model.cabsfexceptions.CabsfInitializationRuntimeException;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeSoftwareAgent;
import org.opensimulationsystems.cabsf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;
import org.opensimulationsystems.cabsf.distsys.core.api.configuration.DistSysRunConfiguration;

/**
 * THis manager provides some utilities for the client (e.g. CABSF JADE Controller Agent) to
 * message its own distributed autonomous agents. The client uses its own native messaging
 * (such as FIPA ACL for JADE). Unlike the SimulationEngineManager (for the distributed
 * system to talk to the simulation side), this class does not perform any message
 * brokering. <br/>
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class DistributedAgentsManager {

	/**
	 * The Enum CONFIGURATION_KEYS.
	 * 
	 * @author Jorge Calderon
	 * @version 0.1
	 * @since 0.1
	 */
	public enum CONFIGURATION_KEYS {

		/** The distributed agents. */
		DISTRIBUTED_AGENTS
	}

	/** The distributed system id. */
	private String distributedSystemID;

	/** The dist sys run configuration. */
	private DistSysRunConfiguration distSysRunConfiguration;

	/** The dist sys run context. */
	private DistSysRunContext distSysRunContext;

	/** The distributed autonomous agent id sto distributed autonomous agents. */
	private final ConcurrentHashMap<String, SoftwareAgent> distributedAutonomousAgentIDStoDistributedAutonomousAgents = new ConcurrentHashMap<String, SoftwareAgent>();

	/** The agents readyfor native distributed agent mapping. */
	private final HashSet<SoftwareAgent> agentsReadyforNativeDistributedAgentMapping = new HashSet<SoftwareAgent>();

	/**
	 * Instantiates a new distributed agents manager.
	 */
	@SuppressWarnings("unused")
	private DistributedAgentsManager() {
	}

	/**
	 * Instantiates the DistributedAgentsManager
	 * 
	 * @param distributedSystemID
	 *            the distributed system id
	 * @param distSysRunContext
	 *            the dist sys run context
	 * @param distSysRunConfiguration
	 *            the dist sys run configuration
	 */
	public DistributedAgentsManager(final String distributedSystemID,
			final DistSysRunContext distSysRunContext,
			final DistSysRunConfiguration distSysRunConfiguration) {
		// public SoftwareAgent(DistSysRunContext distSysRunContext,
		// String getCommonMessagingConcreteImplStr) {
		this.distributedSystemID = distributedSystemID;
		this.distSysRunConfiguration = distSysRunConfiguration;

		this.distSysRunContext = distSysRunContext;

		// Initialization specific to the distirbuted system
	}

	/**
	 * Assign native distributed autonomous agent. Called after an actual distributed
	 * autonomous agent (e.g. JADE agent) comes online.
	 * 
	 * @param nativeDistributedAutonomousAgent
	 *            the native distributed autonomous agent
	 * @return the distributed autonomous agent
	 */
	public SoftwareAgent assignNativeDistributedAutonomousAgent(
			final Object nativeDistributedAutonomousAgent) {
		// TODO: Add Validation to make sure mappings exist. / Throw exception
		// LOW: Support for matching distributed agent by id and/or changing status to
		// "Ready"
		SoftwareAgent softwareAgent = null;

		try {
			// Take first available
			softwareAgent = agentsReadyforNativeDistributedAgentMapping
					.iterator().next();
			softwareAgent
					.setNativeDistributedAutonomousAgent(nativeDistributedAutonomousAgent);
			agentsReadyforNativeDistributedAgentMapping
					.remove(softwareAgent);

			// TODO: Support multiple agent models
			System.out.println("[DISTRIBUTED_SYSTEM] "
					+ "Successfully assigned agent. Dist Aut. Agent ID: "
					+ softwareAgent.getDistributedAutonomousAgentID()
					+ "  Agent Model ID (1st): "
					+ softwareAgent
							.getDistributedAgentModelIDStoAgentModels().values()
							.iterator().next().getDistributedAgentModelID()
					+ " native class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
		} catch (final java.util.NoSuchElementException e) {
			throw new CabsfInitializationRuntimeException(
					"Error Assigning native JADE agent to mapping.", e);
		}

		return softwareAgent;
	}

	/**
	 * Creates objects to hold Agent Mappings between the distributed-agent-side agents
	 * and CABSF-wide string identifiers that are used in mapping on the simulation side.
	 * The actual setting of mapped distributed agent objects occurs later on.
	 * 
	 * @param distSysRunContext
	 *            the dist sys run context
	 * @param distributedAutonomousAgentID
	 *            the distributed autonomous agent id
	 * @param distributedAgentModelIDs
	 *            the distributed agent model i ds
	 * @param distributedAgentModelName
	 *            the distributed agent model name
	 * @return the distributed autonomous agent
	 */
	public SoftwareAgent createDistributedAutonomousAgent(
			final DistSysRunContext distSysRunContext,
			String distributedAutonomousAgentID,
			final Set<String> distributedAgentModelIDs,
			final String distributedAgentModelName) {
		if (distributedAutonomousAgentID == null)
			distributedAutonomousAgentID = UUID.randomUUID().toString();

		// TODO: Add support for multiple agent models
		final SoftwareAgent softwareAgent = new SoftwareAgent(
				distSysRunContext, distributedAutonomousAgentID,
				distributedAgentModelIDs, distributedAgentModelName);

		// TODO: Add validation
		agentsReadyforNativeDistributedAgentMapping.add(softwareAgent);
		distributedAutonomousAgentIDStoDistributedAutonomousAgents.put(
				distributedAutonomousAgentID, softwareAgent);

		return softwareAgent;
	}

	/**
	 * Gets the distributed autonomous agent.
	 * 
	 * @param ID
	 *            the id
	 * @return the distributed autonomous agent
	 */
	public SoftwareAgent getSoftwareAgent(final String ID) {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents.get(ID);
	}

	/**
	 * Gets the distributed autonomous agent id sto distributed autonomous agents.
	 * 
	 * @return the distributed autonomous agent id sto distributed autonomous agents
	 */
	public ConcurrentHashMap<String, SoftwareAgent> getDistributedAutonomousAgentIDStoDistributedAutonomousAgents() {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents;
	}

	// TODO: Pull these from the configuration
	/**
	 * Initialize distributed autonomous agents.
	 * 
	 * @param nativeJadeContextForThisRun
	 *            the native jade context for this run
	 * @param nativeAgentsSet
	 *            the native agents set
	 */
	public void initializeDistributedAutonomousAgents(
			final NativeJADEMockContext nativeJadeContextForThisRun,
			final Set<NativeSoftwareAgent> nativeAgentsSet) {
		/*
		 * Create AgentMapping objects based on the configured type and number of agents.
		 * These objects will be populated with actual mapped simulation-side and
		 * distributed-agent-side data. Mocking data for now;
		 */

		/*
		 * TODO: Pull from configuration (actual JADE objects) For now just assuming one
		 * model agent per distributed autonomous software agent.
		 */
		for (final NativeSoftwareAgent agent : nativeAgentsSet) {
			final HashSet<String> hs = new HashSet<String>();
			hs.add(agent.getDistributedAutonomousAgentModelID());
			final SoftwareAgent softwareAgent = createDistributedAutonomousAgent(
					distSysRunContext, agent.getDistributedAutonomousAgentID(), hs,
					agent.getModelName());
			assignNativeDistributedAutonomousAgent(agent);

			nativeJadeContextForThisRun.addAgent(agent);

		}
	}

	/**
	 * Log helper.
	 * 
	 * @return the object
	 */
	public Object logHelper() {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents;
	}

}
