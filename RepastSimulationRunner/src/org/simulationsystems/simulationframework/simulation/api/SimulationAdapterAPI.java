package org.simulationsystems.simulationframework.simulation.api;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.simulationframework.simulation.api.distributedagents.SimulationDistributedAgentManager;

/**
 * This API is only for use by developers of adapters to connect simulation tools (such as Repast)
 * and agent-based systems (such as JADE) into the common simulation framework. Simulation and Agent
 * developers using such systems should use the appropriate adapter(s). The following highlights the
 * where in the overall system this code sits:<br/>
 * <br/>
 * 
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * RepastS_SimulationAdapterAPI(s) --> Simulations and Agents (Such as Repast simulations and JADE
 * agents) --> End Users of Simulation<br/>
 * <br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the "Repast Simulation RepastS_SimulationRunnerMain"
 * Application, which is both an RepastS_SimulationAdapterAPI into the common simulation framework
 * and its own application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework RepastS_SimulationAdapterAPI JADE Agent".<br/>
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
	/*
	 * public SimulationRunContext initializeAPI(String frameworkConfigurationFileName, String
	 * simulationToolName, String fullyQualifiedClassNameForDistributedAgentManager) throws
	 * IOException {
	 */
	public SimulationRunGroupContext initializeAPI(String frameworkConfigurationFileName,
			String simulationToolName) throws IOException {
		SIMULATION_TOOL_NAME = simulationToolName.toUpperCase();

		// Initialize the Simulation's Configuration Properties
		/*
		 * SimulationRunContext simulationFrameworkContext = simulationAdapterInitializationHelper
		 * .initializeAPI(frameworkConfigurationFileName,
		 * fullyQualifiedClassNameForDistributedAgentManager);
		 */
		SimulationRunGroupContext simulationRunContext = simulationAdapterInitializationHelper
				.initializeAPI(frameworkConfigurationFileName);

		return simulationRunContext;
	}

	/*
	 * Placeholder for future functionality. Adapter clients should call this first before any
	 * custom simulation run initialization is performed.
	 */
	public SimulationRunContext initializeSimulationRun(Object simulationSideContext,
			SimulationRunGroupContext simulationRunGroupContext, String simulationDistributedAgentMessagingManagerStr) {
		return simulationAdapterInitializationHelper.initializeSimulationRun(simulationSideContext,
				simulationRunGroupContext);
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
			SimulationRunContext simulationRunContext) {
		simulationAdapterInitializationHelper.mapSimulationSideAgent(simulationAgent,
				simulationRunContext);
	}

}
