package org.simulationsystems.csf.common.internal.messaging.dao;

import java.util.UUID;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.internal.messaging.interfaces.redis.RedisConnectionManager;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

public class DistributedAgentRedisDaoImpl implements DistributedAgentDao {
	static private DistributedAgentDao instance = new DistributedAgentRedisDaoImpl();
	// We're using a connection pool in Redis so it's ok to use a singleton.
	static private RedisConnectionManager redisConnectionManager = new RedisConnectionManager();

	/*
	 * Disable this constructor so we can use a Singleton
	 */
	@SuppressWarnings("unused")
	private void DistributedAgentDAO() {
	}

	static public DistributedAgentDao getInstance() {
		return instance;
	}

	/*
	 * Redis channel names follow this format: csf.commands.simToDistSystem.IDOFDISTRIBUTEDSYSTEM
	 */
	// TODO: configuration: add id of distributed system(s)
	private String createRedisChannelStr(SYSTEM_TYPE sourceSystemType,
			SYSTEM_TYPE targetSystemType, String ID) {
		// LOW: Make this configurable on the CSF side.
		String prefix = "csf.commands.";

		String prefix2 = null;
		if (sourceSystemType == SYSTEM_TYPE.SYSTEM_ADMINISTRATOR)
			prefix2 = "adminTo";
		else if (sourceSystemType == SYSTEM_TYPE.DISTRIBUTED_SYSTEM)
			prefix2 = "distTo";
		else
			prefix2 = "simTo";

		String prefix3 = null;
		if (targetSystemType == SYSTEM_TYPE.SYSTEM_ADMINISTRATOR)
			prefix3 = "admin";
		else if (targetSystemType == SYSTEM_TYPE.DISTRIBUTED_SYSTEM)
			prefix3 = "distSys";
		else
			prefix3 = "sim";

		String channel = prefix + prefix2 + prefix3 + ":" + ID;
		// LOW: Make this configurable
		return channel;
	}

	// LOW: Add functionality for handling multiple distributed agent systems
	// Call sendMessagesToDistributedAgents for a single distributed agent system from here.

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.DistributedAgentDao
	 * #sendMessagesToDistributedAgents(org.simulationsystems.csf.sim.api.
	 * messaging.FrameworkMessage,
	 * org.simulationsystems.csf.sim.api.messaging.DistributedSystemAgentSet,
	 * org.simulationsystems.csf.sim.api.SimulationRunContext)
	 */
	@Override
	public void sendMessagesToDistributedAgents(SimulationRunContext simulationRunContext,
			FrameworkMessage frameworkMessage, DistributedSystem distributedSystem) {

		String redisChannelStr = createRedisChannelStr(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, distributedSystem.getDistributedSystemID());
		redisConnectionManager.postMessage(redisChannelStr,
				frameworkMessage.transformToCommonMessagingXMLString(distributedSystem));

	}

	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface(
			String messagingConnectionString) {
		redisConnectionManager.initializeRedisConnection(messagingConnectionString);

	}

	@Override
	public void closeInterface() {
		redisConnectionManager.closePool();
	}

	@Override
	public void listenForCommandsFromSimulationAdministrator(String clientID) {
		String redisChannelStr = createRedisChannelStr(SYSTEM_TYPE.SYSTEM_ADMINISTRATOR, SYSTEM_TYPE.SIMULATION_ENGINE, clientID);
		
		//LOW: Improve performance, use publish/subscribe
		redisConnectionManager.redisSynchronousPolling(redisChannelStr, 1l, null);
		
	}
}
