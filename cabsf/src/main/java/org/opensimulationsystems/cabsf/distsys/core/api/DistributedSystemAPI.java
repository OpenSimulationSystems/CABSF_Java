package org.opensimulationsystems.cabsf.distsys.core.api;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.distributedsystems.SimulationDistributedSystemManager;

/**
 * This API is only for use by developers of adapters to connect simulation tools (such as Repast)
 * and agent-based systems (such as JADE) into the Common Agent-Based Simulation Framework. Simulation and Agent
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
 * Application, which is both an JADE_DistributedSystemAdapterAPI into the Common Agent-Based Simulation Framework
 * and its own application programmatically running Repast as a library.<br/>
 * <br/>
 * 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;JADE - Via the
 * "Common Agent-Based Simulation Framework JADE_DistributedSystemAdapterAPI JADE Agent".<br/>
 * <br/>
 * 
 * THIS PACKAGE WILL BE MOVED TO A SEPARATE JAR. TEMPORARILY HERE WIH THE REPAST SIMULATION WRAPPER
 * (RSW)<br/>
 * 
 * @author Jorge Calderon
 */
public class DistributedSystemAPI {
	protected String DISTRIBUTED_SYSTEM_NAME;

	private static DistributedSystemAPI instance = new DistributedSystemAPI();

	// TODO: Look into why have to do lazy initialization to get this to work. Need to figure this
	// out for future multithreading
	private static DistSysInitializationHelper distSysInitializationHelper = null;

	// Use the getInstance() instead.
	protected DistributedSystemAPI() {
	}

	/*
	 * This method should be called after SimulationAPI.getInstance() to initialize the common
	 * framework simulation.
	 * 
	 * @ param String The path to the Common Simulation Configuration File
	 */
	// TODO: Add the UUID for the simulation run group instance.
	/*
	 * public DistSysRunContext initializeAPI(String
	 * frameworkConfigurationFileName, String simulationToolName, String
	 * fullyQualifiedClassNameForDistributedAgentManager) throws IOException {
	 */
	public DistSysRunGroupContext initializeAPI(
			String frameworkConfigurationFileName, String distributedSystemName) throws IOException {
		DISTRIBUTED_SYSTEM_NAME = distributedSystemName.toUpperCase();

		// Initialize the Distributed System's Configuration Properties
		DistSysRunGroupContext distSysRunGroupContext = distSysInitializationHelper
				.initializeAPI(frameworkConfigurationFileName);

		return distSysRunGroupContext;
	}

	/*
	 * Placeholder for future functionality. Adapter clients should call this first before any
	 * custom simulation run initialization is performed.
	 * 
	 * @param Object distributedSystemSideContext An optional context for the distributed system,
	 * native to the distributed system, if such an object exists for said system.
	 */
	public DistSysRunContext initializeSimulationRun(
			Object distributedSystemSideContext,
			DistSysRunGroupContext distSysRunGroupContext) {
		return distSysInitializationHelper.initializeSimulationRun(
				distributedSystemSideContext, distSysRunGroupContext);
	}

	/**
	 * 
	 * The API singleton for clients that are simulation systems adapters to into the common
	 * simulation framework
	 * 
	 */
	public static DistributedSystemAPI getInstance() {
		if (distSysInitializationHelper == null)
			distSysInitializationHelper = new DistSysInitializationHelper(
					instance);
		return instance;
	}

	protected DistSysInitializationHelper getSimulationAdapterInitializationHelper() {
		return distSysInitializationHelper;
	}

	public void assignNativeSoftwareAgent(Object nativeSoftwareAgent,
			DistSysRunContext distSysRunContext) {
		distSysInitializationHelper.assignNativeSoftwareAgent(nativeSoftwareAgent,
				distSysRunContext);
	}

}
