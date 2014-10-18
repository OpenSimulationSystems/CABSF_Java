package org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api;

import java.io.IOException;

import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationFrameworkContext;
import org.simulationsystems.simulationframework.simulation.adapters.api.SimulationAdapterAPI;

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
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the
 * "Repast Simulation RepastSimphonySimulationRunnerMain" Application, which is both an
 * RepastSimphonyRepastSimphonySimulationAdapterAPI into the common simulation framework and its own
 * application programmatically running Repast as a library.<br/>
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
public class RepastSimphonySimulationAdapterAPI {
	private SimulationAdapterAPI simulationAdapterAPI = SimulationAdapterAPI.getInstance();
	private String simToolNameToSetInSimulationAPI = "REPAST_SIMPHONY";
	private String fullyQualifiedClassNameForDistributedAgentManager = "org.simulationsystems.simulationframework.simulation.adapters.simulationapps.api.distributedagents.RepastSimphonySimulationDistributedAgentManager";

	private static RepastSimphonySimulationAdapterAPI instance = new RepastSimphonySimulationAdapterAPI();

	/*
	 * Use RepastSimphonySimulationAdapterAPI.getInstance() instead.
	 */
	private RepastSimphonySimulationAdapterAPI() {
		super();
	}

	/*
	 * This method should be called afterRepastSimphonySimulationAdapterAPI.getInstance() to
	 * initialize the common framework simulation based on the supplied configuration properties.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	public RepastSimphonySimulationFrameworkContext initializeAPI(
			String frameworkConfigurationFileName) throws IOException {
		SimulationFrameworkContext simulationFrameworkContext = simulationAdapterAPI.initializeAPI(
				frameworkConfigurationFileName, simToolNameToSetInSimulationAPI,
				fullyQualifiedClassNameForDistributedAgentManager);

		// Set the Repast-Simphony-specific objects
		RepastSimphonySimulationFrameworkContext repastSimphonySimulationFrameworkContext = new RepastSimphonySimulationFrameworkContext(
				simulationFrameworkContext);

		return repastSimphonySimulationFrameworkContext;
	}

	// private SimulationFrameworkContext
	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the common
	 * simulation framework
	 * 
	 */
	public static RepastSimphonySimulationAdapterAPI getInstance() {
		return instance;
	}

	/*
	 * Initialize the simulation run in Repast Simphony. This method configures the (already-created
	 * in the simulation API initialization) AgentMapping objects. Repast Simphony-specific
	 * simulation run initialization
	 * 
	 * RepastSimphonySimulationFrameworkContext result from initializing the API is passed in, in
	 * this method.
	 */
	// LOW: Allow the same simulation agent class to be both distributed and non-distributed.
	public void initializeSimulationRun(Context<Object> repastContextForThisRun,
			RepastSimphonySimulationFrameworkContext repastSimphonySimulationFrameworkContext) {
		// Placeholder for any future functionality at the Framework-to-Adapter-API level
		simulationAdapterAPI.initializeSimulationRun(repastContextForThisRun,
				repastSimphonySimulationFrameworkContext.getCurrentSimulationFrameworkContext());

		repastSimphonySimulationFrameworkContext
				.setCurrentRepastContextForThisRun(repastContextForThisRun);
		repastSimphonySimulationFrameworkContext.getSimulationConfiguration().initializeAgentMappings();
		
		// Find all of the individual Repast agents to be mapped in the framework to distributed
		// agents
		@SuppressWarnings({ "rawtypes" })
		Iterable<Class> simulationAgentsClasses = repastContextForThisRun.getAgentTypes();
		// For each simulation agent class
		for (@SuppressWarnings("rawtypes")
		Class simulationAgentClass : simulationAgentsClasses) {
			// LOW: Allow individual simulation agent classes to be either simulation-only or
			// representations of distributed agents.
			if (repastSimphonySimulationFrameworkContext.getSimulationConfiguration()
					.isAgentClassDistributedType(simulationAgentClass)) {
				@SuppressWarnings("unchecked")
				Class<Object> simulationAgentClazz = simulationAgentClass;
				Iterable<Object> simulationAgentsInSingleClass = repastContextForThisRun
						.getAgentLayer(simulationAgentClazz);
				// For a distributed agent class type, for each individual simulation agent, map
				for (Object simulationAgent : simulationAgentsInSingleClass) {
					mapSimulationSideAgent(simulationAgent,
							repastSimphonySimulationFrameworkContext
									.getCurrentSimulationFrameworkContext());
				}
			} else
				continue; // Not an agent we need to map.
		}
	}

	/*
	 * @see mapSimulationSideAgent
	 */
	@SuppressWarnings("unused")
	private void mapSimulationSideAgents(Iterable<Object> agentsOfOneType,
			SimulationFrameworkContext simulationFrameworkContext) {
		for (Object simulationAgent : agentsOfOneType) {
			mapSimulationSideAgent(simulationAgent, simulationFrameworkContext);
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
			SimulationFrameworkContext simulationFrameworkContext) {
		simulationAdapterAPI.mapSimulationSideAgent(simulationAgent, simulationFrameworkContext);
	}

}
