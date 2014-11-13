package org.simulationsystems.csf.distsys.api.distributedautonomousagents;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.api.DistSysRunGroupContext;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

/*
 * THis manager provides some utilities for the client (e.g. CSF JADE Controller Agent) to message
 * its own distributed autonomous agents. The client uses its own native messaging (such as FIPA ACL
 * for JADE). Unlike the SimulationEngineManager (for the distributed system to talk to the
 * simulation side), this class does not perform any message brokering.
 */
public class DistributedAgentsManager {
	private String id;
	
	private DistSysRunContext distSysRunContext;
	private CommonMessagingAbstraction commonMessagingAbstraction = null;
	private CommonMessagingImplementationAPI commonMessagingImplementationAPI;

	private ConcurrentHashMap<String, DistributedAutonomousAgent> distributedAutonomousAgentIDStoDistributedAutonomousAgents = new ConcurrentHashMap<String, DistributedAutonomousAgent>();
	private HashSet<DistributedAutonomousAgent> agentsReadyforNativeDistributedAgentMapping = new HashSet<DistributedAutonomousAgent>();

	public enum CONFIGURATION_KEYS {
		DISTRIBUTED_AGENTS
	}

	@SuppressWarnings("unused")
	private DistributedAgentsManager() {
	}

	/*
	 * The manager for a single distributed autonomous software agent (such as a JADE agent). This
	 * class manages the agent model mappings for that software agent. In most cases, software
	 * agents in a programming game will represent a single agent model. In distributed ABM
	 * simulations, the software agent will often be set to contain multiple agent models, for
	 * performance reasons.
	 * 
	 * @param simulationRuntimeID An optional ID to identify the simulation runtime instance for
	 * this distributed system to connect to. If it is to be used, it should be provided by the
	 * configuration on the distributed system side. If it is not provided, the Common Simulation
	 * Framework Distributed System API will look for the first simulation run group (when using
	 * Redis) and attach to that simulation run group instance.
	 * 
	 * @param String id the id of this distributed system.
	 */
	// TODO: Clean this up. We need a specific manager for the type of client (JADE system, etc)
	public DistributedAgentsManager(String id, DistSysRunContext distSysRunContext) {
		// public DistributedAutonomousAgent(DistSysRunContext distSysRunContext,
		// String getCommonMessagingConcreteImplStr) {
		this.id = id;
		
		this.distSysRunContext = distSysRunContext;

		// Check which Bridge implementation we're going to use, based on what was specified in the
		// configuration.
		if (distSysRunContext
				.getDistSysRunConfiguration()
				.getCommonMessagingConcreteImplStr()
				.equals("org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation")) {
		} else {
			// TODO: Handle this better
			throw new IllegalStateException(
					"Error: Redis not properly configured in the CSF configuration file.");
		}

		commonMessagingAbstraction = new CommonMessagingRefinedAbstractionAPI(
				commonMessagingImplementationAPI, distSysRunContext.getDistSysRunConfiguration()
						.getRedisConnectionString());

		// TODO: Move this configuration to the Simulation Run Group level?
		commonMessagingAbstraction
				.initializeSimulationFrameworkCommonMessagingInterface(distSysRunContext
						.getDistSysRunConfiguration().getRedisConnectionString());
	}

	protected ConcurrentHashMap<String, DistributedAutonomousAgent> getAgentModelsToDistributedAutonomousAgentManagersMappings() {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents;
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
	// TODO: Add support for bulk mapping creation (array)
	public DistributedAutonomousAgent createDistributedAutonomousAgent(
			DistSysRunContext distSysRunContext, String distributedAutonomousAgentID,
			String daaName, Double numberOfAgentModels) {
		if (distributedAutonomousAgentID == null)
			distributedAutonomousAgentID = UUID.randomUUID().toString();
		if (numberOfAgentModels == null)
			numberOfAgentModels = 1.0;

		DistributedAutonomousAgent distributedAutonomousAgent = new DistributedAutonomousAgent(
				distSysRunContext, distributedAutonomousAgentID, daaName, numberOfAgentModels);

		// TODO: Add validation
		distributedAutonomousAgentIDStoDistributedAutonomousAgents.put(
				distributedAutonomousAgentID, distributedAutonomousAgent);
		
		return distributedAutonomousAgent;
	}

	// TODO: Pull these from the configuration
	public void initializeAgentModelMappings() {
		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;

		// TODO: Pull from configuration (actual JADE objects) For now just assuming one model agent
		// per distributed autonomous software agent.
		DistributedAutonomousAgent distributedAutonomousAgent = createDistributedAutonomousAgent(
				distSysRunContext, "1DistAgentModel", "Distributed Agent Model 1", null);
		DistributedAgentModel distributedAutonomousAgentModel = distributedAutonomousAgent.createDistributedAgentModel(distSysRunContext, "1DistAgentModel", "Distributed Agent Model 1");
		distributedAutonomousAgentModel.assignAgentModel(null);
		
		DistributedAutonomousAgent distributedAutonomousAgent2 = createDistributedAutonomousAgent(
				distSysRunContext, "2Dist", "Distributed Software Agent 1", null);
		distributedAutonomousAgentModel = distributedAutonomousAgent.createDistributedAgentModel(distSysRunContext, "2DistAgentModel", "Distributed Agent Model 2");
		distributedAutonomousAgentModel.assignAgentModel(null);
		
		DistributedAutonomousAgent distributedAutonomousAgent3 = createDistributedAutonomousAgent(
				distSysRunContext, "3Dist", "Distributed Software Agent 1", null);
		distributedAutonomousAgentModel = distributedAutonomousAgent.createDistributedAgentModel(distSysRunContext, "3DistAgentModel", "Distributed Agent Model 3");
		distributedAutonomousAgentModel.assignAgentModel(null);
		
		DistributedAutonomousAgent distributedAutonomousAgent4 = createDistributedAutonomousAgent(
				distSysRunContext, "4Dist", "Distributed Software Agent 1", null);
		distributedAutonomousAgentModel = distributedAutonomousAgent.createDistributedAgentModel(distSysRunContext, "4DistAgentModel", "Distributed Agent Model 4");
		distributedAutonomousAgentModel.assignAgentModel(null);
		
		DistributedAutonomousAgent distributedAutonomousAgent5 = createDistributedAutonomousAgent(
				distSysRunContext, "5Dist", "Distributed Software Agent 1", null);
		distributedAutonomousAgentModel = distributedAutonomousAgent.createDistributedAgentModel(distSysRunContext, "5DistAgentModel", "Distributed Agent Model 5");
		distributedAutonomousAgentModel.assignAgentModel(null);
	}

	/*
	 * Called after an actual distributed autonomous agent (e.g. JADE agent) comes online.
	 */
	public DistributedAutonomousAgent assignNativeDistributedAutonomousAgent(
			Object nativeDistributedAutonomousAgent) {
		// TODO: Add Validation to make sure mappings exist. / Throw exception
		// LOW: Support for matching distributed agent by id and/or changing status to "Ready"
		DistributedAutonomousAgent distributedAutonomousAgent = null;

		try {
			// Take first available
			distributedAutonomousAgent = agentsReadyforNativeDistributedAgentMapping.iterator().next();
			distributedAutonomousAgent
					.setNativeDistributedAutonomousAgent(nativeDistributedAutonomousAgent);

			agentsReadyforNativeDistributedAgentMapping.remove(nativeDistributedAutonomousAgent);
			System.out.println(this.getClass().getCanonicalName().toString()
					+ ": Successfully assigned native Distributed Autonomous Agent: "
					+ distributedAutonomousAgent.getDistributedAgentModelIDStoAgentModels()
					+ " native class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
		} catch (java.util.NoSuchElementException e) {
			System.out.println("exception:" + e.getMessage() + "  class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
		}

		return distributedAutonomousAgent;
	}
	
	public DistributedAgentModel assignAgentModelToDistributedAutonomousAgent(DistributedAutonomousAgent distributedAutonomousAgent,
			Object nativeAgentModel) {
		// TODO: Add Validation to make sure mappings exist. / Throw exception
		DistributedAgentModel distributedAgentModel = null;

		try {
			distributedAutonomousAgent
					.setNativeDistributedAutonomousAgent(nativeDistributedAutonomousAgent);

			agentsReadyforNativeDistributedAgentMapping.remove(nativeDistributedAutonomousAgent);
			System.out.println(this.getClass().getCanonicalName().toString()
					+ ": Successfully assigned native Distributed Autonomous Agent: "
					+ distributedAutonomousAgent.getDistributedAgentModelIDStoAgentModels()
					+ " native class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
		} catch (java.util.NoSuchElementException e) {
			System.out.println("exception:" + e.getMessage() + "  class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
		}

		return distributedAutonomousAgent;
	}

	public Object logHelper() {
		return distributedAutonomousAgentIDStoDistributedAutonomousAgents;
	}

	public void messageDistributedAgents(FrameworkMessage frameworkMessage,
			SimulationRunContext simulationRunContext) {
		// TODO: Multiple Distributed systems
		commonMessagingAbstraction.sendMessageToDistributedAgents(frameworkMessage,
				distributedSystem, simulationRunContext);
	}

}
