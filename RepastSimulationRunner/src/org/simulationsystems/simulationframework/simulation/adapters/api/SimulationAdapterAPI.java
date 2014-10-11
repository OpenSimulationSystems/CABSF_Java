package org.simulationsystems.simulationframework.simulation.adapters.api;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.simulationsystems.simulationframework.simulation.adapters.api.distributedagents.CommonFrameworkDistributedAgentManager;

/**
 * This API is only for use by developers of adapters to connect simulation tools (such as
 * Repast) and agent-based systems (such as JADE) into the common simulation framework.
 * Simulation and Agent developers using such systems should use the appropriate
 * adapter(s). The following highlights the where in the overall system this code sits:<br/>
 * <br/>
 * 
 * Common Framework---> ***COMMON FRAMEWORK API*** --> Simulation and Agent
 * RepastSimphonySimulationAdapterAPI(s) --> Simulations and Agents (Such as Repast
 * simulations and JADE agents) --> End Users of Simulation<br/>
 * <br/>
 * 
 * Currently supported Adaptors (Implementors of this API):<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Repast - Via the
 * "Repast Simulation RepastSimphonySimulationRunnerMain" Application, which is both an
 * RepastSimphonySimulationAdapterAPI into the common simulation framework and its own
 * application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Simulation Framework RepastSimphonySimulationAdapterAPI JADE Agent".<br/>
 * <br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST
 * SIMULATION WRAPPER (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
public class SimulationAdapterAPI {
	protected String SIMULATION_TOOL_NAME;
	protected SimulationConfiguration simulationConfiguration;

	private static SimulationAdapterAPI instance = new SimulationAdapterAPI();
	private SimulationAdapterInitializationHelper simulationAdapterInitializationHelper = new SimulationAdapterInitializationHelper(
			instance);
	private CommonFrameworkDistributedAgentManager commonFrameworkDistributedAgentManager = new CommonFrameworkDistributedAgentManager(instance);
	private ConcurrentHashMap<String, Object> simulationOptions = new ConcurrentHashMap<String, Object>();

	// Use the getInstance() instead.
	protected SimulationAdapterAPI() {
	}

	/*
	 * This method should be called after SimulationAdapterAPI.getInstance() to initialize
	 * the common framework simulation.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	public void initializeAPI(String frameworkConfigurationFileName,
			String simulationToolName) throws IOException {
		SIMULATION_TOOL_NAME = simulationToolName.toUpperCase();

		// Initialize the Simulation's Configuration Properties
		simulationAdapterInitializationHelper
				.initializeAPI(frameworkConfigurationFileName);
	}

	public void initializeSimulationRun() {
		simulationAdapterInitializationHelper.initializeSimulationRun();
	}

	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the
	 * common simulation framework
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

	public SimulationConfiguration getSimulationConfiguration() {
		return simulationConfiguration;
	}

	public void setSimulationConfiguration(SimulationConfiguration simulationConfiguration) {
		this.simulationConfiguration = simulationConfiguration;
	}

	/*
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor
	 * API (or child class) is initialized, and prior to executing a simulation run, this
	 * method must be called to configure the simulation-side of the AgentMappings for one
	 * type (class) of simulation agent. If multiple agent classes are distributed, this
	 * method must be called for each type. This is done prior to the distributed
	 * agent-side mappings.
	 * 
	 * Use this method to send an Iterable collection of Agents, all members a particular
	 * class.
	 * 
	 * @see mapSimulationSideAgent
	 */
	public void mapSimulationSideAgents(Class item, Iterable<Object> agentsOfOneType) {
		for (Object simulationAgent : agentsOfOneType) {
			simulationAdapterInitializationHelper.mapSimulationSideAgent(simulationAgent);
		}
	}

	/*
	 * After the Simulation and Common Framework are initialized, the Simulation Adaptor
	 * API (or child class) is initialized, and prior to executing a simulation run, this
	 * method must be called to configure the simulation-side of the AgentMappings for one
	 * type (class) of simulation agent. If multiple agent classes are distributed, this
	 * method must be called for each type. This is done prior to the distributed
	 * agent-side mappings.
	 * 
	 * Use this method to send a single Simulation Agent object.
	 * 
	 * @see mapSimulationSideAgents
	 */
	public void mapSimulationSideAgent(Object simulationAgent) {
		simulationAdapterInitializationHelper.mapSimulationSideAgent(simulationAgent);
	}

}
