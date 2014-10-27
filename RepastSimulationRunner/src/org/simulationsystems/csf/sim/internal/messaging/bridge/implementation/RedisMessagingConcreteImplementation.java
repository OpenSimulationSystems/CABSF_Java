package org.simulationsystems.csf.sim.internal.messaging.bridge.implementation;

import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;
import org.simulationsystems.csf.sim.internal.messaging.dao.DistributedAgentDao;
import org.simulationsystems.csf.sim.internal.messaging.dao.DistributedAgentRedisDaoImpl;

/*
 * The concrete implementor in the Bridge pattern for use of the Redis in-memory cache. <br/> Also
 * acts as the client in the DAO pattern (through composition), to retrieve the data from one of the
 * common data interfaces, in this case the in-memory database Redis.
 */
public class RedisMessagingConcreteImplementation implements CommonMessagingImplementationAPI {
	static private DistributedAgentDao distributedAgentDao = DistributedAgentRedisDaoImpl.getInstance();

	/*
	 * 
	 */
	@Override
	public void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem, SimulationRunContext simulationRunContext) {
		distributedAgentDao.sendMessagesToDistributedAgents(simulationRunContext, frameworkMessage,
				distributedSystem);
	}

	@Override
	public void readMessagesFromDistributedAgents(FrameworkMessage frameworkMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface(String messagingConnectionString) {
		distributedAgentDao.initializeSimulationFrameworkCommonMessagingInterface(messagingConnectionString);
		
	}

}
