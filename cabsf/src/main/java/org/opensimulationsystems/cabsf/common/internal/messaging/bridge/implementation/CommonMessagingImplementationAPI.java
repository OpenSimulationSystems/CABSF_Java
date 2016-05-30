package org.opensimulationsystems.cabsf.common.internal.messaging.bridge.implementation;

import org.opensimulationsystems.cabsf.common.internal.systems.DistributedSystem;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;

/**
 * The implementor in the Bridge pattern, for the Common Agent-Based Simulation Framework's messaging
 * 
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public interface CommonMessagingImplementationAPI {

	/**
	 * Close interface.
	 */
	public void closeInterface();

	/**
	 * Initialize simulation framework common messaging interface.
	 * 
	 * @param messagingConnectionString
	 *            the messaging connection string
	 */
	public void initializeSimulationFrameworkCommonMessagingInterface(
			String messagingConnectionString);

	/**
	 * Listen for message from simulation engine.
	 * 
	 * @param targetSystemType
	 *            the target system type
	 * @param clientID
	 *            the client id
	 * @return the framework message
	 */
	FrameworkMessage listenForMessageFromSimulationEngine(SYSTEM_TYPE targetSystemType,
			String clientID);

	/**
	 * Read framework message from distributed system.
	 * 
	 * @param clientID
	 *            the client id
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromDistributedSystem(String clientID);

	/**
	 * Read framework message from simulation administrator.
	 * 
	 * @param clientID
	 *            the client id
	 * @return the framework message
	 */
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(
			String clientID);

	/**
	 * Read messages from distributed agents.
	 * 
	 * @param frameworkMessage
	 *            the framework message
	 */
	public void readMessagesFromDistributedAgents(FrameworkMessage frameworkMessage);

	/**
	 * Request environment information.
	 * 
	 * @param targetSystemType
	 *            the target system type
	 * @param clientID
	 *            the client id
	 * @return the framework message
	 */
	public FrameworkMessage requestEnvironmentInformation(SYSTEM_TYPE targetSystemType,
			String clientID);

	/**
	 * Send messages to distributed agents.
	 * 
	 * @param frameworkMessage
	 *            the framework message
	 * @param distributedSystem
	 *            the distributed system
	 * @param simulationRunContext
	 *            the simulation run context
	 */
	void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext);

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
	public void sendMessageToSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext, String simulationEngineID);

}
