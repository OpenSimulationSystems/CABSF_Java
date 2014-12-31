package org.simulationsystems.csf.distsys.core.api.distributedautonomousagents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.MockHumanJADE_Agent;
import org.simulationsystems.csf.distsys.adapters.jade.api.mocks.NativeJADEMockContext;
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
		distributedAutonomousAgentIDStoDistributedAutonomousAgents.put(
				distributedAutonomousAgentID, distributedAutonomousAgent);

		return distributedAutonomousAgent;
	}

	// TODO: Pull these from the configuration
	public void initializeDistributedAutonomousAgents(NativeJADEMockContext nativeJadeContextForThisRun) {
		// Create AgentMapping objects based on the configured type and number
		// of agents.
		// These objects will be populated with actual mapped simulation-side
		// and
		// distributed-agent-side data.
		// Mocking data for now;

		// TODO: Pull from configuration (actual JADE objects) For now just assuming one
		// model agent
		// per distributed autonomous software agent.
		HashSet<String> hs = new HashSet<String>();
		hs.add("DistributedAgentModel1");
		DistributedAutonomousAgent distributedAutonomousAgent = createDistributedAutonomousAgent(
				distSysRunContext, "DistributedSystemAutonomousAgent1", hs, "Human");
		MockHumanJADE_Agent mockHumanJADE_Agent = new MockHumanJADE_Agent("DistributedSystemAutonomousAgent1","DistributedAgentModel1");
		//distributedAutonomousAgent.setNativeDistributedAutonomousAgent(mockHumanJADE_Agent);
		nativeJadeContextForThisRun.addAgent(mockHumanJADE_Agent);
		
		hs = new HashSet<String>();
		hs.add("DistributedAgentModel2");
		DistributedAutonomousAgent distributedAutonomousAgent2 = createDistributedAutonomousAgent(
				distSysRunContext, "DistributedSystemAutonomousAgent2", hs, "Human");
		mockHumanJADE_Agent = new MockHumanJADE_Agent("DistributedSystemAutonomousAgent2","DistributedAgentModel2");
		//distributedAutonomousAgent2.setNativeDistributedAutonomousAgent(mockHumanJADE_Agent);
		nativeJadeContextForThisRun.addAgent(mockHumanJADE_Agent);
		
		hs = new HashSet<String>();
		hs.add("DistributedAgentModel3");
		DistributedAutonomousAgent distributedAutonomousAgent3 = createDistributedAutonomousAgent(
				distSysRunContext, "DistributedSystemAutonomousAgent3", hs, "Human");
		mockHumanJADE_Agent = new MockHumanJADE_Agent("DistributedSystemAutonomousAgent3","DistributedAgentModel3");
		//distributedAutonomousAgent3.setNativeDistributedAutonomousAgent(mockHumanJADE_Agent);
		nativeJadeContextForThisRun.addAgent(mockHumanJADE_Agent);
		
		hs = new HashSet<String>();
		hs.add("DistributedAgentModel4");
		DistributedAutonomousAgent distributedAutonomousAgent4 = createDistributedAutonomousAgent(
				distSysRunContext, "DistributedSystemAutonomousAgent4", hs, "Human");
		mockHumanJADE_Agent = new MockHumanJADE_Agent("DistributedSystemAutonomousAgent4","DistributedAgentModel4");
		//distributedAutonomousAgent4.setNativeDistributedAutonomousAgent(mockHumanJADE_Agent);
		nativeJadeContextForThisRun.addAgent(mockHumanJADE_Agent);
		
		hs.add("DistributedAgentModel5");
		DistributedAutonomousAgent distributedAutonomousAgent5 = createDistributedAutonomousAgent(
				distSysRunContext, "DistributedSystemAutonomousAgent5", hs, "Human");
		mockHumanJADE_Agent = new MockHumanJADE_Agent("DistributedSystemAutonomousAgent5","DistributedAgentModel5");
		//distributedAutonomousAgent5.setNativeDistributedAutonomousAgent(mockHumanJADE_Agent);
		nativeJadeContextForThisRun.addAgent(mockHumanJADE_Agent);
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
					.remove(nativeDistributedAutonomousAgent);

			System.out.println(this.getClass().getCanonicalName().toString()
					+ ": Successfully assigned native Distributed Autonomous Agent: "
					+ distributedAutonomousAgent
							.getDistributedAgentModelIDStoAgentModels()
					+ " native class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
		} catch (java.util.NoSuchElementException e) {
			System.out.println("exception:" + e.getMessage() + "  class: "
					+ nativeDistributedAutonomousAgent.getClass().getCanonicalName());
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
