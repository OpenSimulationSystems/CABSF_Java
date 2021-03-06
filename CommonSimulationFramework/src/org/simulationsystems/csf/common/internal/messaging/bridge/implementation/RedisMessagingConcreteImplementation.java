package org.simulationsystems.csf.common.internal.messaging.bridge.implementation;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao;
import org.simulationsystems.csf.common.internal.messaging.dao.RedisDaoImpl;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;

/**
 * The concrete implementor in the Bridge pattern for use of the Redis in-memory cache. <br/>
 * Also acts as the client in the DAO pattern (through composition), to retrieve the data
 * from one of the common data interfaces, in this case the in-memory database Redis.
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RedisMessagingConcreteImplementation implements
		CommonMessagingImplementationAPI {

	/** The common messaging dao. */
	static private CommonMessagingDao commonMessagingDao = RedisDaoImpl.getInstance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI#closeInterface()
	 */
	@Override
	public void closeInterface() {
		commonMessagingDao.closeInterface();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #initializeSimulationFrameworkCommonMessagingInterface(java.lang.String)
	 */
	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface(
			final String messagingConnectionString) {
		commonMessagingDao
				.initializeSimulationFrameworkCommonMessagingInterface(messagingConnectionString);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #listenForMessageFromSimulationEngine(org.simulationsystems
	 * .csf.common.csfmodel.SYSTEM_TYPE, java.lang.String)
	 */
	@Override
	public FrameworkMessage listenForMessageFromSimulationEngine(
			final SYSTEM_TYPE targetSystemType, final String clientID) {
		return commonMessagingDao.listenForMessageFromSimulationEngine(targetSystemType,
				clientID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #readFrameworkMessageFromDistributedSystem(java.lang.String)
	 */
	@Override
	public FrameworkMessage readFrameworkMessageFromDistributedSystem(
			final String clientID) {
		return commonMessagingDao.readFrameworkMessageFromDistributedSystem(clientID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #readFrameworkMessageFromSimulationAdministrator(java.lang.String)
	 */
	@Override
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(
			final String clientID) {
		return commonMessagingDao
				.readFrameworkMessageFromSimulationAdministrator(clientID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #readMessagesFromDistributedAgents(org.simulationsystems
	 * .csf.common.csfmodel.messaging.messages.FrameworkMessage)
	 */
	@Override
	public void readMessagesFromDistributedAgents(final FrameworkMessage frameworkMessage) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #requestEnvironmentInformation(org.simulationsystems
	 * .csf.common.csfmodel.SYSTEM_TYPE, java.lang.String)
	 */
	@Override
	public FrameworkMessage requestEnvironmentInformation(
			final SYSTEM_TYPE targetSystemType, final String clientID) {
		return commonMessagingDao.requestEnvironmentInformation(targetSystemType,
				clientID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #sendMessagesToDistributedAgents(org.simulationsystems
	 * .csf.common.csfmodel.messaging.messages.FrameworkMessage,
	 * org.simulationsystems.csf.common.internal.systems.DistributedSystem,
	 * org.simulationsystems.csf.sim.core.api.SimulationRunContext)
	 */
	@Override
	public void sendMessagesToDistributedAgents(final FrameworkMessage frameworkMessage,
			final DistributedSystem distributedSystem,
			final SimulationRunContext simulationRunContext) {
		commonMessagingDao.sendMessagesToDistributedAgents(simulationRunContext,
				frameworkMessage, distributedSystem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.bridge.implementation.
	 * CommonMessagingImplementationAPI
	 * #sendMessageToSimulationEngine(org.simulationsystems
	 * .csf.common.csfmodel.messaging.messages.FrameworkMessage,
	 * org.simulationsystems.csf.distsys.core.api.DistSysRunContext, java.lang.String)
	 */
	@Override
	public void sendMessageToSimulationEngine(final FrameworkMessage frameworkMessage,
			final DistSysRunContext distSysRunContext, final String simulationEngineID) {
		commonMessagingDao.sendMessageToSimulationEngine(distSysRunContext,
				frameworkMessage, simulationEngineID);

	}
}
