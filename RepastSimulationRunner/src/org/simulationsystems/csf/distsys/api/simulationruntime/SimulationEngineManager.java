package org.simulationsystems.csf.distsys.api.simulationruntime;

import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.api.DistributedSystemSimulationRunContext;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

public class SimulationEngineManager {

	private DistributedSystemSimulationRunContext distributedSystemSimulationRunContext;
	private CommonMessagingAbstraction commonMessagingAbstraction = null;
	private CommonMessagingImplementationAPI commonMessagingImplementationAPI;
	
	/*
	 * @param simulationRuntimeID An optional ID to identify the simulation runtime instance for
	 * this distributed system to connect to. If it is to be used, it should be provided by the
	 * configuration on the distributed system side. If it is not provided, the Common Simulation
	 * Framework Distributed System API will look for the first simulation run group (when using
	 * Redis) and attach to that simulation run group instance.
	 */

	public SimulationEngineManager(String simulationEngineID) {

	}

	public SimulationEngineManager(
			DistributedSystemSimulationRunContext distributedSystemSimulationRunContext,
			String getCommonMessagingConcreteImplStr) {
		this.distributedSystemSimulationRunContext = distributedSystemSimulationRunContext;

		// Check which Bridge implementation we're going to use, based on what was specified in the
		// configuration.
		if (distributedSystemSimulationRunContext
				.getDistributedSystemSimulationRunConfiguration()
				.getCommonMessagingConcreteImplStr()
				.equals("org.simulationsystems.csf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation")) {
		} else {
			// TODO: Handle this better
			throw new IllegalStateException(
					"Error: Redis not properly configured in the CSF configuration file.");
		}

		// commonMessagingAbstraction =
		// simulationRunContext.getSimulationRunConfiguration()
		// = new CommonMessagingRefinedAbstractionAPI(null);

		// Instantiate the correct manager for the common messaging interface (e.g., Redis or web
		// services) using reflection.
		// TODO: Handle exception when unable to instantiate class
		// TODO: ?Handle configuration/reflection for Bridge Refined Abstraction?
		try {
			Class<?> cl = Class.forName(getCommonMessagingConcreteImplStr);
			// Constructor<?> cons = cl.getConstructor(cl.getClass());
			// commonMessagingImplementationAPI =
			// (commonMessagingImplementationAPI) cons.newInstance();
			commonMessagingImplementationAPI = (CommonMessagingImplementationAPI) cl.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		commonMessagingAbstraction = new CommonMessagingRefinedAbstractionAPI(
				commonMessagingImplementationAPI, distributedSystemSimulationRunContext
						.getDistributedSystemSimulationRunConfiguration().getRedisConnectionString());

		// TODO: Move this configuration to the Simulation Run Group level?
		commonMessagingAbstraction
				.initializeSimulationFrameworkCommonMessagingInterface(distributedSystemSimulationRunContext
						.getDistributedSystemSimulationRunConfiguration().getRedisConnectionString());
	}

	public void sendMessage(FrameworkMessage frameworkMessage,
			DistributedSystemSimulationRunContext distributedSystemSimulationRunContext) {

	}

	public void messageDistributedAgents(FrameworkMessage frameworkMessage,
			SimulationRunContext simulationRunContext) {
		// TODO: Multiple Distributed systems
		// FIX
		// distributedAgentMessagingRefinedAbstraction.sendMessageToDistributedAgents(
		// frameworkMessage, distributedSystem, simulationRunContext);
	}

}
