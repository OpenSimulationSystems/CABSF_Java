package org.simulationsystems.csf.common.internal.messaging.bridge.implementation;

import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao;
import org.simulationsystems.csf.common.internal.messaging.dao.RedisDaoImpl;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

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
	public void listenForCommandsFromSimulationAdministrator(String clientID) {
		commonMessagingDao.listenForCommandsFromSimulationAdministrator(clientID);

	}

	@Override
	public FRAMEWORK_COMMAND listenForCommandsFromSimulationEngine(SYSTEM_TYPE targetSystemType,
			String clientID) {
		return commonMessagingDao.listenForCommandsFromSimulationEngine(targetSystemType, clientID);

	};

}
