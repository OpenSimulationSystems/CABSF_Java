package org.opensimulationsystems.cabsf.sim.core.api;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * This API is only for use by developers of adapters to connect simulation tools (such as
 * Repast) and agent-based systems (such as JADE) into the Common Agent-Based Simulation Framework.
 * Simulation and Agent developers using such systems should use the appropriate
 * adapter(s).
 * 
 * @author Jorge Calderon
 */
public class SimulationAPI {

	/** The simulation tool name. */
	protected String SIMULATION_TOOL_NAME;

	/** The instance. */
	private static SimulationAPI instance = new SimulationAPI();

	// TODO: Look into why have to do lazy initialization to get this to work. Need to
	// figure this
	// out for future multithreading
	/** The simulation initialization helper. */
	private static SimulationInitializationHelper simulationInitializationHelper = null;

	/**
	 * The API singleton for clients that are simulation systems adapters to into the
	 * Common Agent-Based Simulation Framework.
	 * 
	 * @return single instance of SimulationAPI
	 */
	public static SimulationAPI getInstance() {
		if (simulationInitializationHelper == null)
			simulationInitializationHelper = new SimulationInitializationHelper(instance);
		return instance;
	}

	// Use the getInstance() instead.
	/**
	 * Instantiates a new simulation api.
	 */
	protected SimulationAPI() {
	}

	/**
	 * Gets the simulation adapter initialization helper.
	 * 
	 * @return the simulation adapter initialization helper
	 */
	protected SimulationInitializationHelper getSimulationAdapterInitializationHelper() {
		return simulationInitializationHelper;
	}

	/*
	 * This method should be called after SimulationAPI.getInstance() to initialize the
	 * common framework simulation.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	// TODO: Add the UUID for the simulation run group instance.
	/*
	 * public DistSysRunContext initializeAPI(String frameworkConfigurationFileName,
	 * String simulationToolName, String
	 * fullyQualifiedClassNameForDistributedAgentManager) throws IOException {
	 */
	/**
	 * Initialize api.
	 * 
	 * @param cabsfConfigurationFileName
	 *            the framework configuration file name
	 * @param simulationToolName
	 *            the simulation tool name
	 * @return the simulation run group context
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimulationRunGroupContext initializeAPI(
			final String cabsfConfigurationFileName, final String simulationToolName)
			throws IOException {
		SIMULATION_TOOL_NAME = simulationToolName.toUpperCase();

		// Initialize the Simulation's Configuration Properties
		/*
		 * DistSysRunContext simulationFrameworkContext = simulationInitializationHelper
		 * .initializeAPI(frameworkConfigurationFileName,
		 * fullyQualifiedClassNameForDistributedAgentManager);
		 */
		final SimulationRunGroupContext simulationRunGroupContext = simulationInitializationHelper
				.initializeAPI(cabsfConfigurationFileName);

		return simulationRunGroupContext;
	}

	/**
	 * Initializes the simulation run. Adapter clients should call this first before any
	 * custom simulation run initialization is performed.
	 * 
	 * @param simulationSideContext
	 *            the simulation side context
	 * @param simulationRunGroupContext
	 *            the simulation run group context
	 * @return the simulation run context
	 */
	public SimulationRunContext initializeSimulationRun(
			final Object simulationSideContext,
			final SimulationRunGroupContext simulationRunGroupContext) {
		return simulationInitializationHelper.initializeSimulationRun(
				simulationSideContext, simulationRunGroupContext);
	}

	/**
	 * Map simulation side agent.
	 * 
	 * @param simulationAgent
	 *            the simulation agent
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	public void mapSimulationSideAgent(final Object simulationAgent,
			final SimulationRunContext simulationRunContext) {
		simulationInitializationHelper.mapSimulationSideAgent(simulationAgent,
				simulationRunContext);
	}

}
