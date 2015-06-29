package org.opensimulationsystems.cabsf.common.internal.messaging.bridge.abstraction;

import org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation.CommonMessagingImplementationAPI;
import org.opensimulationsystems.cabsf.common.internal.systems.DistributedSystem;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;

/**
 * The abstract class in the Bridge pattern that specifies the Common Simulation Framework
 * interface. This class uses the Bridge Pattern so that clients can switch the messaging
 * interface as needed. The actual interface is Redis or (in the future) web services.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public abstract class CommonMessagingAbstraction {

	/** The common messaging implementation api. */
	protected CommonMessagingImplementationAPI commonMessagingImplementationAPI;
	// LOW: Generalize this, maybe make it an object.
	/**
	 * The messaging connection string, such as the Redis connection string.
	 */
	protected String messagingConnectionString;

	/** The common messaging client id. */
	protected String commonMessagingClientId;

	/**
	 * Instantiates a new common messaging abstraction.
	 * 
	 * @param commonMessagingImplementationAPI
	 *            the common messaging implementation api
	 * @param messagingConnectionString
	 *            the messaging connection string
	 * @param commonMessagingClientId
	 *            the common messaging client id
	 */
	protected CommonMessagingAbstraction(
			final CommonMessagingImplementationAPI commonMessagingImplementationAPI,
			final String messagingConnectionString, final String commonMessagingClientId) {
		this.commonMessagingImplementationAPI = commonMessagingImplementationAPI;
		this.messagingConnectionString = messagingConnectionString;
		this.commonMessagingClientId = commonMessagingClientId;
	}

	/**
	 * Close interface. For example, used to close the Redis connection.
	 */
	public void closeInterface() {
		commonMessagingImplementationAPI.closeInterface();
	}

	/**
	 * Initialize simulation framework common messaging interface.
	 * 
	 * @param messagingConnectionString
	 *            the messaging connection string
	 */
	abstract public void initializeSimulationFrameworkCommonMessagingInterface(
			String messagingConnectionString);

	/**
	 * Listen for message from simulation engine.
	 * 
	 * @param targetSystemType
	 *            the target system type
	 * @param clientID
	 *            the client ID
	 * @return the framework message
	 */
	public FrameworkMessage listenForMessageFromSimulationEngine(
			final SYSTEM_TYPE targetSystemType, final String clientID) {
		return commonMessagingImplementationAPI.listenForMessageFromSimulationEngine(
				targetSystemType, clientID);

	}

	/**
	 * Read framework message from distributed system.
	 * 
	 * @param clientID
	 *            the client id
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromDistributedSystem(
			final String clientID) {
		return commonMessagingImplementationAPI
				.readFrameworkMessageFromDistributedSystem(clientID);

	}

	/**
	 * Read framework message from simulation administrator.
	 * 
	 * @param clientID
	 *            the client id
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(
			final String clientID) {
		return commonMessagingImplementationAPI
				.readFrameworkMessageFromSimulationAdministrator(clientID);

	}

	/**
	 * Request environment information.
	 * 
	 * @param targetSystemType
	 *            the target system type
	 * @param clientID
	 *            the client id
	 * @return the framework message
	 */
	public FrameworkMessage requestEnvironmentInformation(
			final SYSTEM_TYPE targetSystemType, final String clientID) {
		return commonMessagingImplementationAPI.requestEnvironmentInformation(
				targetSystemType, clientID);
	}

	/**
	 * Send message to distributed agents.
	 * 
	 * @param frameworkMessage
	 *            the framework message
	 * @param distributedSystem
	 *            the distributed system
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	abstract public void sendMessageToDistributedAgents(
			FrameworkMessage frameworkMessage, DistributedSystem distributedSystem,
			SimulationRunContext simulationRunContext);

	/**
	 * Send message to simulation engine.
	 * 
	 * @param frameworkMessage
	 *            the framework message
	 * @param distSysRunContext
	 *            the dist sys run context
	 * @param simulationEngineID
	 *            the simulation engine id
	 */
	public abstract void sendMessageToSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext, String simulationEngineID);

}
