package org.simulationsystems.csf.sim.api;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.csf.sim.api.distributedsystems.SimulationDistributedSystemManager;

/**
 * This API is only for use by developers of adapters to connect simulation tools (such as Repast)
 * and agent-based systems (such as JADE) into the common simulation framework. Simulation and Agent
 * developers using such systems should use the appropriate adapter(s). The following highlights the
 * where in the overall system this code sits:<br/>
 * <br/>
 * 
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * JADE_DistributedSystemAdapterAPI(s) --> Simulations and Agents (Such as Repast simulations and JADE
 * agents) --> End Users of Simulation<br/>
 * <br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the "Repast Simulation RepastS_SimulationRunnerMain"
 * Application, which is both an JADE_DistributedSystemAdapterAPI into the common simulation framework
 * and its own application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework JADE_DistributedSystemAdapterAPI JADE Agent".<br/>
 * <br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST SIMULATION WRAPPER
 * (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
public class SimulationAPI {
	protected String SIMULATION_TOOL_NAME;
	private static SimulationAPI instance = new SimulationAPI();
	
	// TODO: Look into why have to do lazy initialization to get this to work. Need to figure this
	// out for future multithreading
	private static SimulationInitializationHelper simulationInitializationHelper = null;

	// Use the getInstance() instead.
	protected SimulationAPI() {
	}

	/*
	 * This method should be called after SimulationAPI.getInstance() to initialize the
	 * common framework simulation.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	// TODO: Add the UUID for the simulation run group instance.
	/*
	 * public DistSysRunContext initializeAPI(String frameworkConfigurationFileName, String
	 * simulationToolName, String fullyQualifiedClassNameForDistributedAgentManager) throws
	 * IOException {
	 */
	public SimulationRunGroupContext initializeAPI(String frameworkConfigurationFileName,
			String simulationToolName) throws IOException {
		SIMULATION_TOOL_NAME = simulationToolName.toUpperCase();

		// Initialize the Simulation's Configuration Properties
		/*
		 * DistSysRunContext simulationFrameworkContext = simulationInitializationHelper
		 * .initializeAPI(frameworkConfigurationFileName,
		 * fullyQualifiedClassNameForDistributedAgentManager);
		 */
		SimulationRunGroupContext simulationRunGroupContext = simulationInitializationHelper
				.initializeAPI(frameworkConfigurationFileName);

		return simulationRunGroupContext;
	}

	/*
	 * Placeholder for future functionality. Adapter clients should call this first before any
	 * custom simulation run initialization is performed.
	 */
	public SimulationRunContext initializeSimulationRun(Object simulationSideContext,
			SimulationRunGroupContext simulationRunGroupContext) {
		return simulationInitializationHelper.initializeSimulationRun(simulationSideContext,
				simulationRunGroupContext);
	}

	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the common
	 * simulation framework
	 * 
	 */
	public static SimulationAPI getInstance() {
		if (simulationInitializationHelper == null)
			simulationInitializationHelper = new SimulationInitializationHelper(
					instance);
		return instance;
	}

	protected SimulationInitializationHelper getSimulationAdapterInitializationHelper() {
		return simulationInitializationHelper;
	}

	public void mapSimulationSideAgent(Object simulationAgent,
			SimulationRunContext simulationRunContext) {
		simulationInitializationHelper.mapSimulationSideAgent(simulationAgent,
				simulationRunContext);
	}

}
