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
public class DistributedAutonomousAgent {
	private String id;
	private String name;
	private Object distributedAgentModel;
	private DistSysRunContext distSysRunContext;
	private Object nativeDistributedAutonomousAgent;

	private ConcurrentHashMap<String, DistributedAgentModel> distributedAgentModelIDStoAgentModels = new ConcurrentHashMap<String, DistributedAgentModel>();
	private HashSet<DistributedAutonomousAgent> agentsReadyForDistributedAgentMapping = new HashSet<DistributedAutonomousAgent>();
 
	@SuppressWarnings("unused")
	private DistributedAutonomousAgent() {
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
	 */
	// TODO: Clean this up. We need a specific manager for the type of client (JADE system, etc)
	public DistributedAutonomousAgent(DistSysRunContext distSysRunContext, String distributedAutonomousAgentID, String daaName) {
		// public DistributedAutonomousAgent(DistSysRunContext distSysRunContext,
		// String getCommonMessagingConcreteImplStr) {
		this.distSysRunContext = distSysRunContext;
		this.name = daaName;
		if (distributedAutonomousAgentID==null)
			this.id = UUID.randomUUID().toString();
		
		//LOW: Support multiple agent models within this software agent
		//numberOfAgentModels
	}

	public String getId() {
		return id;
	}

	protected ConcurrentHashMap<String, DistributedAgentModel> getDistributedAgentModelIDStoAgentModels() {
		return distributedAgentModelIDStoAgentModels;
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
	public DistributedAgentModel createDistributedAgentModel(
			DistSysRunContext distSysRunContext, String distributedAgentModelID, String damName) {
		if (distributedAgentModelID == null)
			distributedAgentModelID = UUID.randomUUID().toString();
		
		DistributedAgentModel distributedAgentModel = new DistributedAgentModel(
				distSysRunContext, distributedAgentModelID, damName);

		// TODO: Add validation
		distributedAgentModelIDStoAgentModels.put(
				distributedAgentModelID, distributedAgentModel);
		return distributedAgentModel;
	}

	public void setNativeDistributedAutonomousAgent(Object nativeDistributedAutonomousAgent) {
		this.nativeDistributedAutonomousAgent = nativeDistributedAutonomousAgent;
	}
	
	public Object logHelper() {
		return distributedAgentModelIDStoAgentModels;
	}

	public void messageDistributedAgents(FrameworkMessage frameworkMessage,
			SimulationRunContext simulationRunContext) {
		// TODO: Multiple Distributed systems
		commonMessagingAbstraction.sendMessageToDistributedAgents(frameworkMessage,
				distributedSystem, simulationRunContext);
	}


	

}
