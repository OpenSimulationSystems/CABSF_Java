package org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction;

import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.opensimulationsystems.cabsf.common.internal.systems.DistributedSystem;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;

// TODO: Auto-generated Javadoc
/**
 * The refined abstraction class in the Bridge Pattern to represent the
 * platform-independent interface to the Simulation-Toolkit side of the Common Simulation
 * Framework. The interface could use Redis or (in the future) web services. This class
 * uses the Bridge Pattern and acts as the refined abstraction, so the implementation for
 * the type of interface to common messaging part of the framework is hidden from this
 * class.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class CommonMessagingRefinedAbstractionAPI extends CommonMessagingAbstraction {

	/**
	 * Instantiates a new common messaging refined abstraction api.
	 *
	 * @param commonMessagingImplementationAPI
	 *            the common messaging implementation api
	 * @param connectionStr
	 *            the connection str
	 * @param commonMessagingClientId
	 *            the common messaging client id
	 */
	public CommonMessagingRefinedAbstractionAPI(
			final CommonMessagingImplementationAPI commonMessagingImplementationAPI,
			final String connectionStr, final String commonMessagingClientId) {
		super(commonMessagingImplementationAPI, connectionStr, commonMessagingClientId);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction.
	 * CommonMessagingAbstraction
	 * #initializeSimulationFrameworkCommonMessagingInterface(java.lang.String)
	 */
	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface(
			final String messagingConnectionString) {
		commonMessagingImplementationAPI
		.initializeSimulationFrameworkCommonMessagingInterface(messagingConnectionString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction.
	 * CommonMessagingAbstraction
	 * #sendMessageToDistributedAgents(org.opensimulationsystems.
	 * cabsf.common.model.messaging.messages.FrameworkMessage,
	 * org.opensimulationsystems.cabsf.common.internal.systems.DistributedSystem,
	 * org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext)
	 */
	@Override
	public void sendMessageToDistributedAgents(final FrameworkMessage frameworkMessage,
			final DistributedSystem distributedSystem,
			final SimulationRunContext simulationRunContext) {
		// commonMessagingImplementationAPI set in the RunGroup initialization using
		// reflection, as
		// specified in the common framework configuration.

		commonMessagingImplementationAPI.sendMessagesToDistributedAgents(
				frameworkMessage, distributedSystem, simulationRunContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction.
	 * CommonMessagingAbstraction
	 * #sendMessageToSimulationEngine(org.opensimulationsystems.cabsf
	 * .common.model.messaging.messages.FrameworkMessage,
	 * org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext,
	 * java.lang.String)
	 */
	@Override
	public void sendMessageToSimulationEngine(final FrameworkMessage frameworkMessage,
			final DistSysRunContext distSysRunContext, final String simulationEngineID) {
		// commonMessagingImplementationAPI set in the RunGroup initialization using
		// reflection, as
		// specified in the common framework configuration.

		commonMessagingImplementationAPI.sendMessageToSimulationEngine(frameworkMessage,
				distSysRunContext, simulationEngineID);
	}

}
