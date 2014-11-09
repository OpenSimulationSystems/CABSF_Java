package org.simulationsystems.csf.distsys.adapters.api.jade;

import java.io.IOException;

import org.simulationsystems.csf.distsys.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.api.DistSysRunGroupContext;
import org.simulationsystems.csf.distsys.api.DistributedSystemAPI;

import repast.simphony.context.Context;

/**
 * This API is only for use by developers of adapters to connect simulation tools (such as Repast)
 * and agent-based systems (such as JADE) into the common simulation framework. Simulation and Agent
 * developers using such systems should use the appropriate adapter(s). The following highlights the
 * where in the overall system this code sits:<br/>
 * <br/>
 * 
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * RepastSimphonyRepastSimphonySimulationAdapterAPI(s) --> Simulations and Agents (Such as Repast
 * simulations and JADE agents) --> End Users of Simulation<br/>
 * <br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the "Repast Simulation RepastS_SimulationRunnerMain"
 * Application, which is both an RepastSimphonyRepastSimphonySimulationAdapterAPI into the common
 * simulation framework and its own application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework RepastSimphonyRepastSimphonySimulationAdapterAPI JADE Agent" .<br/>
 * <br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST SIMULATION WRAPPER
 * (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
public class JADE_MAS_AdapterAPI {
	private DistributedSystemAPI distributedSystemAPI = DistributedSystemAPI.getInstance();
	private String simToolNameToSetInDistributedSystemAPI = "REPAST_SIMPHONY";
	// private String fullyQualifiedClassNameForDistributedAgentManager =
	// "org.simulationsystems.csf.sim.adapters.api.repastS.distributedagents.RepastSimphonySimulationDistributedAgentManager";

	private static JADE_MAS_AdapterAPI instance = new JADE_MAS_AdapterAPI();

	/*
	 * Use JADE_DistributedSystemAdapterAPI.getInstance() instead.
	 */
	private JADE_MAS_AdapterAPI() {
		super();
	}

	/*
	 * This method should be called afterRepastSimphonySimulationAdapterAPI.getInstance() to
	 * initialize the common framework simulation based on the supplied configuration properties.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	public JADE_MAS_RunGroupContext initializeAPI(String frameworkConfigurationFileName)
			throws IOException {

		DistSysRunGroupContext distSysRunGroupContext = distributedSystemAPI.initializeAPI(
				frameworkConfigurationFileName, simToolNameToSetInDistributedSystemAPI);

		// Set the JADE-specific objects, using the Decorator Pattern
		JADE_MAS_RunGroupContext jade_MAS_RunGroupContext = new JADE_MAS_RunGroupContext(
				distSysRunGroupContext);

		return jade_MAS_RunGroupContext;
	}

	// private DistSysRunContext
	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the common
	 * simulation framework
	 * 
	 */
	public static JADE_MAS_AdapterAPI getInstance() {
		return instance;
	}

	/*
	 * Initialize the simulation run in Repast Simphony. This method configures the (already-created
	 * in the simulation API initialization) AgentMapping objects. Repast Simphony-specific
	 * simulation run initialization
	 * 
	 * JADE_DistSysRunContext result from initializing the API is passed in, in this method.
	 */
	// LOW: Allow the same simulation agent class to be both distributed and non-distributed.
	public JADE_MAS_RunContext initializeSimulationRun(
			Context<Object> jadeContextForThisRun,
			JADE_MAS_RunGroupContext jade_MAS_RunGroupContext) {

		DistSysRunContext distSysRunContext = distributedSystemAPI.initializeSimulationRun(
				jadeContextForThisRun,
				jade_MAS_RunGroupContext.getDistSysRunGroupContext());

		// User Decorator Pattern for JADE_DistSysRunContext
		JADE_MAS_RunContext jADE_MAS_RunContext = new JADE_MAS_RunContext(
				distSysRunContext);
		jADE_MAS_RunContext.setRepastContextForThisRun(jadeContextForThisRun);

		// LOW: Support multiple Simulation Run Groups. For now just assume that there's one.
		// LOW: Handle multiple distributed systems
		jADE_MAS_RunContext getSimulationDistributedSystemManagers().iterator().next()
				.initializeAgentMappings();

		// Find all of the individual Repast agents to be mapped in the framework to distributed
		// agents
		@SuppressWarnings({ "rawtypes" })
		Iterable<Class> simulationAgentsClasses = jadeContextForThisRun.getAgentTypes();
		// For each simulation agent class
		for (@SuppressWarnings("rawtypes")
		Class simulationAgentClass : simulationAgentsClasses) {
			// LOW: Allow individual simulation agent classes to be either simulation-only or
			// representations of distributed agents.
			// TODO: Handle multiple distributed systems
			if (jADE_MAS_RunContext.getSimulationDistributedSystemManagers().iterator()
					.next().isAgentClassDistributedType(simulationAgentClass)) {
				@SuppressWarnings("unchecked")
				Class<Object> simulationAgentClazz = simulationAgentClass;
				Iterable<Object> simulationAgentsInSingleClass = jadeContextForThisRun
						.getAgentLayer(simulationAgentClazz);
				// For a distributed agent class type, for each individual simulation agent, map to
				// an existing free AgentMapping object
				for (Object simulationAgent : simulationAgentsInSingleClass) {
					mapSimulationSideAgent(simulationAgent,
							jADE_MAS_RunContext.getDistSysRunContext());
				}
			} else
				continue; // Not an agent we need to map.
		}

		return jADE_MAS_RunContext;
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	@SuppressWarnings("unused")
	private void mapSimulationSideAgents(Iterable<Object> agentsOfOneType,
			DistSysRunContext distSysRunContext) {
		for (Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent, distSysRunContext);
		}
	}

	/*
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor API (or
	 * child class) is initialized, and prior to executing a simulation run, this method must be
	 * called to configure the simulation-side of the AgentMappings for one type (class) of
	 * simulation agent. If multiple agent classes are distributed, this method must be called for
	 * each type. This is done prior to the distributed agent-side mappings.
	 * 
	 * Use this method to send a single Simulation Agent object.
	 * 
	 * @see mapSimulationSideAgents
	 */
	private void mapSimulationSideAgent(Object simulationAgent,
			DistSysRunContext distSysRunContext) {
		distributedSystemAPI.mapSimulationSideAgent(simulationAgent, distSysRunContext);
	}

	public void logHelper(JADE_MAS_RunContext jADE_MAS_RunContext) {
		// TODO: handle multiple distributed systems
		System.out.println(jADE_MAS_RunContext.getSimulationDistributedSystemManagers()
				.iterator().next().logHelper());
	}

}
