package org.simulationsystems.csf.sim.adapters.api.repastS;

import java.io.IOException;

import org.simulationsystems.csf.sim.api.SimulationAPI;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.api.SimulationRunGroupContext;
import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

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
public class RepastS_SimulationAdapterAPI {
	private SimulationAPI simulationAPI = SimulationAPI.getInstance();
	private String simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";
	// private String fullyQualifiedClassNameForDistributedAgentManager =
	// "org.simulationsystems.csf.sim.adapters.api.repastS.distributedagents.RepastSimphonySimulationDistributedAgentManager";

	private static RepastS_SimulationAdapterAPI instance = new RepastS_SimulationAdapterAPI();

	/*
	 * Use JADE_DistributedSystemAdapterAPI.getInstance() instead.
	 */
	private RepastS_SimulationAdapterAPI() {
		super();
	}

	/*
	 * This method should be called afterRepastSimphonySimulationAdapterAPI.getInstance() to
	 * initialize the common framework simulation based on the supplied configuration properties.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	public RepastS_SimulationRunGroupContext initializeAPI(String frameworkConfigurationFileName)
			throws IOException {

		SimulationRunGroupContext simulationRunGroupContext = simulationAPI.initializeAPI(
				frameworkConfigurationFileName, simToolNameToSetInSimulationAPI);

		// Set the Repast-Simphony-specific objects, using the Decorator Pattern
		RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext = new RepastS_SimulationRunGroupContext(
				simulationRunGroupContext);

		return repastS_SimulationRunGroupContext;
	}

	// private DistSysRunContext
	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the common
	 * simulation framework
	 * 
	 */
	public static RepastS_SimulationAdapterAPI getInstance() {
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
	public RepastS_SimulationRunContext initializeSimulationRun(
			Context<Object> repastContextForThisRun,
			RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {

		SimulationRunContext simulationRunContext = simulationAPI.initializeSimulationRun(
				repastContextForThisRun,
				repastS_SimulationRunGroupContext.getSimulationRunGroupContext());

		// User Decorator Pattern for JADE_DistSysRunContext
		RepastS_SimulationRunContext repastS_SimulationRunContext = new RepastS_SimulationRunContext(
				simulationRunContext);
		repastS_SimulationRunContext.setRepastContextForThisRun(repastContextForThisRun);

		// LOW: Support multiple Simulation Run Groups. For now just assume that there's one.
		// LOW: Handle multiple distributed systems
		repastS_SimulationRunContext.getSimulationDistributedSystemManagers().iterator().next()
				.initializeAgentMappings();

		// Find all of the individual Repast agents to be mapped in the framework to distributed
		// agents
		//TODO: Move all of this to the main simulation API to simplify Adapter code.  (Same for JADE API side)
		@SuppressWarnings({ "rawtypes" })
		Iterable<Class> simulationAgentsClasses = repastContextForThisRun.getAgentTypes();
		// For each simulation agent class
		for (@SuppressWarnings("rawtypes")
		Class simulationAgentClass : simulationAgentsClasses) {
			// LOW: Allow individual simulation agent classes to be either simulation-only or
			// representations of distributed agents.
			// TODO: Handle multiple distributed systems
			if (repastS_SimulationRunContext.getSimulationDistributedSystemManagers().iterator()
					.next().isAgentClassDistributedType(simulationAgentClass)) {
				@SuppressWarnings("unchecked")
				Class<Object> simulationAgentClazz = simulationAgentClass;
				Iterable<Object> simulationAgentsInSingleClass = repastContextForThisRun
						.getAgentLayer(simulationAgentClazz);
				// For a distributed agent class type, for each individual simulation agent, map to
				// an existing free AgentMapping object
				for (Object simulationAgent : simulationAgentsInSingleClass) {
					mapSimulationSideAgent(simulationAgent,
							repastS_SimulationRunContext.getSimulationRunContext());
				}
			} else
				continue; // Not an agent we need to map.
		}

		return repastS_SimulationRunContext;
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	@SuppressWarnings("unused")
	private void mapSimulationSideAgents(Iterable<Object> agentsOfOneType,
			SimulationRunContext simulationRunContext) {
		for (Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent, simulationRunContext);
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
			SimulationRunContext simulationRunContext) {
		simulationAPI.mapSimulationSideAgent(simulationAgent, simulationRunContext);
	}

	public void logHelper(RepastS_SimulationRunContext repastS_SimulationRunContext) {
		// TODO: handle multiple distributed systems
		System.out.println(repastS_SimulationRunContext.getSimulationDistributedSystemManagers()
				.iterator().next().logHelper());
	}

}