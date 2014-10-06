package org.simulationsystems.simulationframework.internal.api;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.simulationframework.internal.api.distributedagents.CommonFrameworkDistributedAgentManager;

/**
 * This API is only for use by developers of adapters to connect simulation
 * tools (such as Repast) and agent-based systems (such as JADE) into the common
 * simulation framework. Simulation and Agent developers using such systems
 * should use the appropriate adapter(s). The following highlights the where in
 * the overall system this code sits:<br/>
 * <br/>
 * 
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * Adapter(s) --> Simulations and Agents (Such as Repast simulations and JADE
 * agents) --> End Users of Simulation<br/>
 * <br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the "Repast Simulation Wrapper"
 * Application, which is both an Adapter into the common simulation framework
 * and its own application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework Adapter JADE Agent".<br/><br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST
 * SIMULATION WRAPPER (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
public class SimulationAdapterAPI {
	private static SimulationAdapterAPI instance = new SimulationAdapterAPI();
	private SimulationAdapterInitializationHelper simulationAdapterInitializationHelper = new SimulationAdapterInitializationHelper(
			instance);
	private CommonFrameworkDistributedAgentManager commonFrameworkDistributedAgentManager = new CommonFrameworkDistributedAgentManager();
	private ConcurrentHashMap<String, Object> simulationOptions = new ConcurrentHashMap<String, Object>();

	// Disabled
	private SimulationAdapterAPI() {
	}

	/*
	 * This method should be called after SimulationAdapterAPI.getInstance() to
	 * initialize the common framework simulation.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	public void initializeAPI(String frameworkConfigurationFileName) throws IOException {

		// Initialize the Simulation
		simulationAdapterInitializationHelper.initializeAPI(frameworkConfigurationFileName);
	}

	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to
	 * into the common simulation framework
	 * 
	 */
	public static SimulationAdapterAPI getInstance() {
		return instance;
	}

	protected SimulationAdapterInitializationHelper getSimulationAdapterInitializationHelper() {
		return simulationAdapterInitializationHelper;
	}

	protected CommonFrameworkDistributedAgentManager getCommonFrameworkDistributedAgentManager() {
		return commonFrameworkDistributedAgentManager;
	}

}
