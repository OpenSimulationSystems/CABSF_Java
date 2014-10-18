package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.SimulationDistributedAgentManager;

/**
 * This API is only for use by developers of adapters to connect simulation tools (such as Repast)
 * and agent-based systems (such as JADE) into the common simulation framework. Simulation and Agent
 * developers using such systems should use the appropriate adapter(s). The following highlights the
 * where in the overall system this code sits:<br/>
 * <br/>
 * 
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * RepastSimphonySimulationAdapterAPI(s) --> Simulations and Agents (Such as Repast simulations and
 * JADE agents) --> End Users of Simulation<br/>
 * <br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the
 * "Repast Simulation RepastSimphonySimulationRunnerMain" Application, which is both an
 * RepastSimphonySimulationAdapterAPI into the common simulation framework and its own application
 * programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework RepastSimphonySimulationAdapterAPI JADE Agent".<br/>
 * <br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST SIMULATION WRAPPER
 * (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
public class SimulationAdapterAPI {
	protected String SIMULATION_TOOL_NAME;
	private static SimulationAdapterAPI instance = new SimulationAdapterAPI();
	private static SimulationAdapterInitializationHelper simulationAdapterInitializationHelper = null;

	// Use the getInstance() instead.
	protected SimulationAdapterAPI() {
	}

	/*
	 * This method should be called after SimulationAdapterAPI.getInstance() to initialize the
	 * common framework simulation.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	// TODO: Add the UUID for the simulation run group instance.
	public SimulationFrameworkContext initializeAPI(String frameworkConfigurationFileName,
			String simulationToolName, String fullyQualifiedClassNameForDistributedAgentManager) throws IOException {
		SIMULATION_TOOL_NAME = simulationToolName.toUpperCase();

		// Initialize the Simulation's Configuration Properties
		SimulationFrameworkContext simulationFrameworkContext = simulationAdapterInitializationHelper
				.initializeAPI(frameworkConfigurationFileName, fullyQualifiedClassNameForDistributedAgentManager);
		return simulationFrameworkContext;
	}

	/*
	 * Placeholder for future functionality. Adapter clients should call this first before any
	 * custom simulation run initialization is performed.
	 */
	public void initializeSimulationRun(Object simulationSideContext, SimulationFrameworkContext simulationFrameworkContext) {
		simulationAdapterInitializationHelper.initializeSimulationRun(simulationSideContext, simulationFrameworkContext);
	}

	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the common
	 * simulation framework
	 * 
	 */
	public static SimulationAdapterAPI getInstance() {
		if (simulationAdapterInitializationHelper == null)
			simulationAdapterInitializationHelper = new SimulationAdapterInitializationHelper(
					instance);
		return instance;
	}

	protected SimulationAdapterInitializationHelper getSimulationAdapterInitializationHelper() {
		return simulationAdapterInitializationHelper;
	}
	
	public void mapSimulationSideAgent(Object simulationAgent,
			SimulationFrameworkContext simulationFrameworkContext) {
		simulationAdapterInitializationHelper.mapSimulationSideAgent(simulationAgent, simulationFrameworkContext);
	}

}
