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
	public SimulationFrameworkContext initializeAPI(String frameworkConfigurationFileName) throws IOException {
		return simulationAdapterAPI.initializeAPI(frameworkConfigurationFileName,
				simToolNameToSetInSimulationAPI);
	}

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
	 * in the simulation API initialization) AgentMapping objects.
	 * Repast Simphony-specific simulation run initialization
	 */
	// TODO: 
	public void initializeSimulationRun(Context<Object> repastContextForThisRun, SimulationFrameworkContext simulationFrameworkContext) {
		simulationAdapterAPI.initializeSimulationRun(repastContextForThisRun, simulationFrameworkContext);

		@SuppressWarnings({ "rawtypes" })
		Iterable<Class> simulationAgentsInAllClasses = repastContextForThisRun.getAgentTypes();
		for (@SuppressWarnings("rawtypes")
		Class simulationAgentClass : simulationAgentsInAllClasses) {
			// Finish mapping all agents of this type
			@SuppressWarnings("unchecked")
			Class<Object> simulationAgentClazz = simulationAgentClass;
			Iterable<Object> simulationAgentsInSingleClass = repastContextForThisRun
					.getAgentLayer(simulationAgentClazz);

			for (Object simulationAgent : simulationAgentsInSingleClass) {
				@SuppressWarnings("unchecked")
				Class<Object> agentClass = (Class<Object>) simulationAgent.getClass();
				if (simulationFrameworkContext.getSimulationConfiguration().isAgentClassDistributedType(
						agentClass)) {
					simulationFrameworkContext.mapSimulationSideAgent(simulationAgent);
				} else
					continue;
			}

		}
	}

}
