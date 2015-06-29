package org.opensimulationsystems.cabsf.distsys.core.api.simulationruntime;

import java.util.HashSet;

import org.opensimulationsystems.cabsf.common.csfmodel.AgentMapping;
import org.opensimulationsystems.cabsf.common.csfmodel.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction.CommonMessagingAbstraction;
import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction.CommonMessagingRefinedAbstractionAPI;
import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;

/**
 * The Class SimulationEngineManager.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class SimulationEngineManager {

	/** The dist sys run context. */
	private final DistSysRunContext distSysRunContext;

	/** The common messaging abstraction. */
	private CommonMessagingAbstraction commonMessagingAbstraction = null;

	/** The common messaging implementation api. */
	private CommonMessagingImplementationAPI commonMessagingImplementationAPI;

	/** The this distributed system id. */
	private String thisDistributedSystemID = null;

	/** The agents ready for distributed autonomous agent mapping. */
	private final HashSet<AgentMapping> agentsReadyForDistributedAutonomousAgentMapping = new HashSet<AgentMapping>();

	/** The fully initialized agent mappings. */
	private final HashSet<AgentMapping> fullyInitializedAgentMappings = new HashSet<AgentMapping>();

	/*
	 * @param simulationRuntimeID An optional ID to identify the simulation runtime
	 * instance for this distributed system to connect to. If it is to be used, it should
	 * be provided by the configuration on the distributed system side. If it is not
	 * provided, the Common Simulation Framework Distributed System API will look for the
	 * first simulation run group (when using Redis) and attach to that simulation run
	 * group instance.
	 */

	/**
	 * Instantiates a new simulation engine manager.
	 *
	 * @param distSysRunContext
	 *            the dist sys run context
	 * @param getCommonMessagingConcreteImplStr
	 *            the get common messaging concrete impl str
	 * @param thisDistributedSystemID
	 *            the this distributed system id
	 */
	public SimulationEngineManager(final DistSysRunContext distSysRunContext,
			final String getCommonMessagingConcreteImplStr,
			final String thisDistributedSystemID) {
		this.distSysRunContext = distSysRunContext;
		this.thisDistributedSystemID = thisDistributedSystemID;

		// Check which Bridge implementation we're going to use, based on what was
		// specified in the
		// configuration.
		if (distSysRunContext
				.getDistSysRunConfiguration()
				.getCommonMessagingConcreteImplStr()
				.equals("org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.RedisMessagingConcreteImplementation")) {
		} else {
			// TODO: Handle this better
			throw new IllegalStateException(
					"Error: Redis not properly configured in the CSF configuration file.");
		}

		// commonMessagingAbstraction =
		// simulationRunContext.getSimulationRunConfiguration()
		// = new CommonMessagingRefinedAbstractionAPI(null);

		// Instantiate the correct manager for the common messaging interface (e.g., Redis
		// or web
		// services) using reflection.
		// TODO: Handle exception when unable to instantiate class
		// TODO: ?Handle configuration/reflection for Bridge Refined Abstraction?
		try {
			final Class<?> cl = Class.forName(getCommonMessagingConcreteImplStr);
			// Constructor<?> cons = cl.getConstructor(cl.getClass());
			// commonMessagingImplementationAPI =
			// (commonMessagingImplementationAPI) cons.newInstance();
			commonMessagingImplementationAPI = (CommonMessagingImplementationAPI) cl
					.newInstance();
		} catch (final InstantiationException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}

		commonMessagingAbstraction = new CommonMessagingRefinedAbstractionAPI(
				commonMessagingImplementationAPI, distSysRunContext
				.getDistSysRunConfiguration().getRedisConnectionString(),
				distSysRunContext.getDistSysRunConfiguration().getDistributedSystemID());

		// TODO: Move this configuration to the Simulation Run Group level?
		commonMessagingAbstraction
		.initializeSimulationFrameworkCommonMessagingInterface(distSysRunContext
				.getDistSysRunConfiguration().getRedisConnectionString());
	}

	/*
	 *
	 * public FrameworkMessage listenForMessageFromSimulationEngine() { return
	 * commonMessagingAbstraction.listenForMessageFromSimulationEngine(
	 * SYSTEM_TYPE.DISTRIBUTED_SYSTEM, thisDistributedSystemID);
	 *
	 * }
	 *
	 * public FrameworkMessage requestEnvironmentInformation() { return
	 * commonMessagingAbstraction.requestEnvir
	 * .getDistSysRunConfiguration().getRedisConnectionString()); }
	 *
	 * public void sendMessage(final FrameworkMessage frameworkMessage, final
	 * DistSysRunContext distSysRunContext, final String simulationEngineID) {
	 * commonMessagingAbstraction.sendMess
	 */
	/**
	 * Close interface.
	 */
	public void closeInterface() {
		commonMessagingAbstraction.closeInterface();
	}

	public FrameworkMessage listenForMessageFromSimulationEngine() {
		return commonMessagingAbstraction.listenForMessageFromSimulationEngine(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, thisDistributedSystemID);

	}

	public FrameworkMessage requestEnvironmentInformation() {
		return commonMessagingAbstraction.requestEnvironmentInformation(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, thisDistributedSystemID);
	}

	public void sendMessage(final FrameworkMessage frameworkMessage,
			final DistSysRunContext distSysRunContext, final String simulationEngineID) {
		commonMessagingAbstraction.sendMessageToSimulationEngine(frameworkMessage,
				distSysRunContext, simulationEngineID);

	}

}
