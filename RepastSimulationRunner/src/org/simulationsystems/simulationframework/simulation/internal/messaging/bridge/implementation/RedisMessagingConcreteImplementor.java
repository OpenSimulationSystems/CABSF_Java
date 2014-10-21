package org.simulationsystems.simulationframework.simulation.internal.messaging.bridge.implementation;

import org.simulationsystems.simulationframework.simulation.api.messaging.FrameworkMessage;
import org.simulationsystems.simulationframework.simulation.internal.messaging.dao.DistributedAgentDao;
import org.simulationsystems.simulationframework.simulation.internal.messaging.dao.DistributedAgentDaoImpl;

/*
 * The concrete implementor in the Bridge pattern for use of the Redis in-memory cache.
 * <br/>
 * Also acts as the client in the DAO pattern to retrieve the data.
 */
public class RedisMessagingConcreteImplementor implements CommonMessagingAPI {
	private DistributedAgentDao distributedAgentDao = DistributedAgentDaoImpl.getInstance();

	/*
	 * 
	 */
	@Override
	public void sendMessagesToDistributedAgents(FrameworkMessage frameworkMessage) {
		//TODO: Add the distributed systems ID
		distributedAgentDao.sendMessagesToDistributedAgents(frameworkMessage, distributedAgent, null, simulationRunContext);
	}

	@Override
	public void readMessagesFromDistributedAgents(FrameworkMessage frameworkMessage) {
		// TODO Auto-generated method stub

	}

}
