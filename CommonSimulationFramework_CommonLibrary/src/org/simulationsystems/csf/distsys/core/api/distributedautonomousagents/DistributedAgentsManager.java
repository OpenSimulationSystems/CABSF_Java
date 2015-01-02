package org.simulationsystems.csf.distsys.core.api.distributedautonomousagents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfInitializationRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeDistributedAutonomousAgent;
import org.simulationsystems.csf.distsys.adapters.jade.api.nativeagents.NativeJADEMockContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.core.api.DistSysRunGroupContext;
import org.simulationsystems.csf.distsys.core.api.configuration.DistSysRunConfiguration;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;

/*
 * THis manager provides some utilities for the client (e.g. CSF JADE Controller Agent) to message
 * its own distributed autonomous agents. The client uses its own native messaging (such as FIPA ACL
 * for JADE). Unlike the SimulationEngineManager (for the distributed system to talk to the
 * simulation side), this class does not perform any message brokering.
 */
public class DistributedAgentsManager {
	private String distributedSystemID;
	private DistSysRunConfiguration distSysRunConfiguration;

	private DistSysRunContext distSysRunContext;

	private ConcurrentHashMap<String, DistributedAutonomousAgent> distributedAutonomousAgentIDStoDistributedAutonomousAgents = new ConcurrentHashMap<String, DistributedAutonomousAgent>();
	private HashSet<DistributedAutonomousAgent> agentsReadyforNativeDistributedAgentMapping = new HashSet<DistributedAutonomousAgent>();

	public enum CONFIGURATION_KEYS {
		DISTRIBUTED_AGENTS
	}

	@SuppressWarnings("unused")
	private DistributedAgentsManager() {
	}

	/*
	 * The manager for a single distributed autonomous software agent (such as a JADE
	 * agent). This class manages the agent model mappings for that software agent. In
	 * most cases, software agents in a programming game will represent a single agent
	 * model. In distributed ABM simulations, the software agent will often be set to
	 * contain multiple agent models, for performance reasons.
	 * 
	 * @param simulationRuntimeID An optional ID to identify the simulation runtime
	 * instance for this distributed system to connect to. If it is to be used, it should
	 * be provided by the configuration on the distributed system side. If it is not
	 * provided, the Common Simulation Framework Distributed System API will look for the
	 * first simulation run group (when using Redis) and attach to that simulation run
	 * group instance.
	 * 
	 * @param String id the id of this distributed system.
	 */
	// TODO: Clean this up. We need a specific manager for the type of client (JADE
	// system, etc)
	public DistributedAgentsManager(String distributedSystemID,
			DistSysRunContext distSysRunContext,
			DistSysRunConfiguration distSysRunConfiguration) {
		// public DistributedAutonomousAgent(DistSysRunContext distSysRunContext,
		// String getCommonMessagingConcreteImplStr) {
		this.distributedSystemID = distributedSystemID;
		this.distSysRunConfiguration = distSysRunConfiguration;

		this.distSysRunContext = distSysRunContext;

		// Initialization specific to the distirbuted system
	}

	public ConcurrentHashMap<String, DistributedAutonomousAgent> getDistributedAutonomousAgentIDStoDistributedAutonomousAgents() {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents;
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
	// TODO: Add support for bulk mapping creation (array)
	public DistributedAutonomousAgent createDistributedAutonomousAgent(
			DistSysRunContext distSysRunContext, String distributedAutonomousAgentID,
			Set<String> distributedAgentModelIDs, String distributedAgentModelName) {
		if (distributedAutonomousAgentID == null)
			distributedAutonomousAgentID = UUID.randomUUID().toString();

		// TODO: Add support for multiple agent models
		DistributedAutonomousAgent distributedAutonomousAgent = new DistributedAutonomousAgent(
				distSysRunContext, distributedAutonomousAgentID,
				distributedAgentModelIDs, distributedAgentModelName);

		// TODO: Add validation
		agentsReadyforNativeDistributedAgentMapping.add(distributedAutonomousAgent);
		distributedAutonomousAgentIDStoDistributedAutonomousAgents.put(
				distributedAutonomousAgentID, distributedAutonomousAgent);

		return distributedAutonomousAgent;
	}

	// TODO: Pull these from the configuration
	public void initializeDistributedAutonomousAgents(
			NativeJADEMockContext nativeJadeContextForThisRun,
			Set<NativeDistributedAutonomousAgent> nativeAgentsSet) {
		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;

		// TODO: Pull from configuration (actual JADE objects) For now just assuming one
		// model agent
		// per distributed autonomous software agent.
		for (NativeDistributedAutonomousAgent agent : nativeAgentsSet) {
			HashSet<String> hs = new HashSet<String>();
			hs.add(agent.getDistributedAutonomousAgentID() + "MODEL");
			DistributedAutonomousAgent distributedAutonomousAgent = createDistributedAutonomousAgent(
					distSysRunContext, agent.getDistributedAutonomousAgentID(), hs,
					agent.getModelName());
			nativeJadeContextForThisRun.addAgent(agent);

		}
	}

	/*
	 * Called after an actual distributed autonomous agent (e.g. JADE agent) comes online.
	 */
	public DistributedAutonomousAgent assignNativeDistributedAutonomousAgent(
			Object nativeDistributedAutonomousAgent) {
		// TODO: Add Validation to make sure mappings exist. / Throw exception
		// LOW: Support for matching distributed agent by id and/or changing status to
		// "Ready"
		DistributedAutonomousAgent distributedAutonomousAgent = null;

		try {
			// Take first available
			distributedAutonomousAgent = agentsReadyforNativeDistributedAgentMapping
					.iterator().next();
			distributedAutonomousAgent
					.setNativeDistributedAutonomousAgent(nativeDistributedAutonomousAgent);
			agentsReadyforNativeDistributedAgentMapping
					.remove(distributedAutonomousAgent);

			System.out.println("[DISTRIBUTED_SYSTEM] "
					+ " Agent ID: "
					+ distributedAutonomousAgent.getDistributedAutonomousAgentID()
					+ " Successfully assigned native Distributed Autonomous Agent: "
					+ distributedAutonomousAgent
							.getDistributedAgentModelIDStoAgentModels().hashCode()
					+ " native class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
		} catch (java.util.NoSuchElementException e) {
			throw new CsfInitializationRuntimeException(
					"Error Assigning native JADE agent to mapping.", e);
		}

		return distributedAutonomousAgent;
	}

	public DistributedAutonomousAgent getDistributedAutonomousAgent(String ID) {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents.get(ID);
	}

	public Object logHelper() {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents;
	}

}
