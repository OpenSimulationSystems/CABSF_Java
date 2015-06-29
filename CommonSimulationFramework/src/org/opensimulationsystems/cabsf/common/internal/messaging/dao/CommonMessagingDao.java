package org.opensimulationsystems.cabsf.common.internal.messaging.dao;

import org.opensimulationsystems.cabsf.common.internal.systems.DistributedSystem;
import org.opensimulationsystems.cabsf.common.model.SYSTEM_TYPE;
import org.opensimulationsystems.cabsf.common.model.messaging.messages.FrameworkMessage;
import org.opensimulationsystems.cabsf.distsys.core.api.DistSysRunContext;
import org.opensimulationsystems.cabsf.sim.core.api.SimulationRunContext;

/**
 * The Interface for the DAO for reading and writing the CabsfMessageExchange XML
 * messages.
 *
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public interface CommonMessagingDao {

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
	 * @param simulationRunContext
	 *            the simulation run context
	 * @param frameworkMessage
	 *            the framework message
	 * @param distributedSystem
	 *            the distributed system
	 */
	public void sendMessagesToDistributedAgents(
			SimulationRunContext simulationRunContext, FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem);

	/**
	 * Send message to simulation engine.
	 *
	 * @param distSysRunContext
	 *            the dist sys run context
	 * @param frameworkMessage
	 *            the framework message
	 * @param simulationEngineID
	 *            the simulation engine id
	 */
	public void sendMessageToSimulationEngine(DistSysRunContext distSysRunContext,
			FrameworkMessage frameworkMessage, String simulationEngineID);
}