package org.simulationsystems.csf.distsys.api.distributedautonomousagents;

import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.simulationsystems.csf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.simulationsystems.csf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.simulationsystems.csf.distsys.api.DistSysRunContext;
import org.simulationsystems.csf.distsys.api.DistSysRunGroupContext;

/*
 * THis manager provides some utilities for the client (e.g. CSF JADE Controller Agent) to message
 * its own distributed autonomous agents. The client uses its own native messaging (such as FIPA ACL
 * for JADE). Unlike the SimulationEngineManager (for the distributed system to talk to the
 * simulation side), this class does not perform any message brokering.
 */
public class DistributedAutonomousAgentManager {

	private DistSysRunContext distSysRunContext;
	private CommonMessagingAbstraction commonMessagingAbstraction = null;
	private CommonMessagingImplementationAPI commonMessagingImplementationAPI;

	@SuppressWarnings("unused")
	private DistributedAutonomousAgentManager() {
	}

	/*
	 * @param simulationRuntimeID An optional ID to identify the simulation runtime instance for
	 * this distributed system to connect to. If it is to be used, it should be provided by the
	 * configuration on the distributed system side. If it is not provided, the Common Simulation
	 * Framework Distributed System API will look for the first simulation run group (when using
	 * Redis) and attach to that simulation run group instance.
	 */
	public DistributedAutonomousAgentManager(DistSysRunContext distSysRunContext) {
		this.distSysRunContext = distSysRunContext;
		// public DistributedAutonomousAgentManager(DistSysRunContext distSysRunContext,
		// String getCommonMessagingConcreteImplStr) {
		this.distSysRunContext = distSysRunContext;

		// Check which Bridge implementation we're going to use, based on what was specified in the
		// configuration.
		if (distSysRunContext
				.getDistSysSimulationRunConfiguration()
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
		/*
		 * try {// //Do we need reflection? Class<?> cl =
		 * Class.forName(getCommonMessagingConcreteImplStr); commonMessagingImplementationAPI =
		 * (CommonMessagingImplementationAPI) cl.newInstance(); } catch (InstantiationException e) {
		 * e.printStackTrace(); } catch (IllegalAccessException e) { e.printStackTrace(); } catch
		 * (IllegalArgumentException e) { e.printStackTrace(); } catch (SecurityException e) {
		 * e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
		 */

		commonMessagingAbstraction = new CommonMessagingRefinedAbstractionAPI(
				commonMessagingImplementationAPI, distSysRunContext
						.getDistSysSimulationRunConfiguration().getRedisConnectionString());

		// TODO: Move this configuration to the Simulation Run Group level?
		commonMessagingAbstraction
				.initializeSimulationFrameworkCommonMessagingInterface(distSysRunContext
						.getDistSysSimulationRunConfiguration().getRedisConnectionString());
	}

}
