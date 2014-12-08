package org.simulationsystems.csf.common.internal.messaging.bridge.implementation;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao;
import org.simulationsystems.csf.common.internal.messaging.dao.RedisDaoImpl;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;

/*
 * The concrete implementor in the Bridge pattern for use of the Redis in-memory cache. <br/> Also
 * acts as the client in the DAO pattern (through composition), to retrieve the data from one of the
 * common data interfaces, in this case the in-memory database Redis.
 */
public class RedisMessagingConcreteImplementation implements CommonMessagingImplementationAPI {
	static private CommonMessagingDao commonMessagingDao = RedisDaoImpl.getInstance();

	/*
	 * 
	 */
	@Override
	public void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext) {
		commonMessagingDao.sendMessagesToDistributedAgents(simulationRunContext, frameworkMessage,
				distributedSystem);
	}

	@Override
	public void readMessagesFromDistributedAgents(FrameworkMessage frameworkMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface(
			String messagingConnectionString) {
		commonMessagingDao
				.initializeSimulationFrameworkCommonMessagingInterface(messagingConnectionString);

	}

	@Override
	public void closeInterface() {
		commonMessagingDao.closeInterface();
	}

	@Override
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(String clientID) {
		return commonMessagingDao.readFrameworkMessageFromSimulationAdministrator(clientID);

	}
	
	@Override
	public FrameworkMessage readFrameworkMessageFromDistributedSystem(String clientID) {
		return commonMessagingDao.readFrameworkMessageFromDistributedSystem(clientID);

	}
	
	@Override
	public FrameworkMessage listenForMessageFromSimulationEngine(SYSTEM_TYPE targetSystemType, String clientID) {
		return commonMessagingDao.listenForMessageFromSimulationEngine(targetSystemType, clientID);
		
	}

	@Override
	public FrameworkMessage requestEnvironmentInformation(
			SYSTEM_TYPE targetSystemType, String clientID) {
		return commonMessagingDao.requestEnvironmentInformation(targetSystemType, clientID);
	}

	@Override
	public void sendMessageToSimulationEngine(FrameworkMessage frameworkMessage,
			DistSysRunContext distSysRunContext, String simulationEngineID) {
		commonMessagingDao.sendMessageToSimulationEngine(distSysRunContext, frameworkMessage, simulationEngineID);
		
	};

}
