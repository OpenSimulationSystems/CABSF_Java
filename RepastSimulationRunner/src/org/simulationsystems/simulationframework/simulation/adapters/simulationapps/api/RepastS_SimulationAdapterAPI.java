package org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api;

import java.io.IOException;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationRunContext;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationAdapterAPI;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationRunGroupContext;
import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager;

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
	private SimulationAdapterAPI simulationAdapterAPI = SimulationAdapterAPI.getInstance();
	private String simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";
	private String fullyQualifiedClassNameForDistributedAgentManager = "org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api.distributedagents.RepastSimphonySimulationDistributedAgentManager";

	private static RepastS_SimulationAdapterAPI instance = new RepastS_SimulationAdapterAPI();

	/*
	 * Use RepastS_SimulationAdapterAPI.getInstance() instead.
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
		/*
		 * SimulationRunContext simulationFrameworkContext = simulationAdapterAPI.initializeAPI(
		 * frameworkConfigurationFileName, simToolNameToSetInSimulationAPI,
		 * fullyQualifiedClassNameForDistributedAgentManager);
		 */
		SimulationRunGroupContext simulationRunGroupContext = simulationAdapterAPI.initializeAPI(
				frameworkConfigurationFileName, simToolNameToSetInSimulationAPI);

		// Set the Repast-Simphony-specific objects, using the Decorator Pattern
		RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext = new RepastS_SimulationRunGroupContext(
				simulationRunGroupContext);

		return repastS_SimulationRunGroupContext;
	}

	// private SimulationRunContext
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
	 * RepastS_SimulationRunContext result from initializing the API is passed in, in this method.
	 */
	// LOW: Allow the same simulation agent class to be both distributed and non-distributed.
	public RepastS_SimulationRunContext initializeSimulationRun(Context<Object> repastContextForThisRun,
			RepastS_SimulationRunGroupContext repastS_SimulationRunGroupContext) {

		SimulationRunContext simulationRunContext = simulationAdapterAPI.initializeSimulationRun(
				repastContextForThisRun,
				repastS_SimulationRunGroupContext.getSimulationRunGroupContext());

		// User Decorator Pattern for RepastS_SimulationRunContext
		RepastS_SimulationRunContext repastS_SimulationRunContext = new RepastS_SimulationRunContext(
				simulationRunContext);
		repastS_SimulationRunContext.setRepastContextForThisRun(repastContextForThisRun);

		// TODO: Support multiple Simulation Run Groups. For now just assume that there's one.
		repastS_SimulationRunContext.getSimulationDistributedAgentManager()
				.initializeAgentMappings();

		// Find all of the individual Repast agents to be mapped in the framework to distributed
		// agents
		@SuppressWarnings({ "rawtypes" })
		Iterable<Class> simulationAgentsClasses = repastContextForThisRun.getAgentTypes();
		// For each simulation agent class
		for (@SuppressWarnings("rawtypes")
		Class simulationAgentClass : simulationAgentsClasses) {
			// LOW: Allow individual simulation agent classes to be either simulation-only or
			// representations of distributed agents.
			if (repastS_SimulationRunContext.getSimulationDistributedAgentManager()
					.isAgentClassDistributedType(simulationAgentClass)) {
				@SuppressWarnings("unchecked")
				Class<Object> simulationAgentClazz = simulationAgentClass;
				Iterable<Object> simulationAgentsInSingleClass = repastContextForThisRun
						.getAgentLayer(simulationAgentClazz);
				// For a distributed agent class type, for each individual simulation agent, map
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
		simulationAdapterAPI.mapSimulationSideAgent(simulationAgent, simulationRunContext);
	}
	
	public void logHelper(RepastS_SimulationRunContext repastS_SimulationRunContext) {
		System.out.println(repastS_SimulationRunContext.getSimulationDistributedAgentManager().logHelper());
	}

}
