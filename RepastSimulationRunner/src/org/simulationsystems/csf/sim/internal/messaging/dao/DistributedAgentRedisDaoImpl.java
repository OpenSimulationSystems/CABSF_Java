package org.simulationsystems.csf.sim.internal.messaging.dao;

import java.util.UUID;

import org.simulationsystems.csf.common.internal.messaging.interfaces.redis.RedisConnectionManager;
import org.simulationsystems.csf.common.internal.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

public class DistributedAgentRedisDaoImpl implements DistributedAgentDao {
	static private RedisConnectionManager redisConnectionManager = new RedisConnectionManager();

	/*
	 * Disable this constructor so we can use a Singleton
	 */
	@SuppressWarnings("unused")
	private void DistributedAgentDAO() {
	}

	static public DistributedAgentDao getInstance() {
		return (DistributedAgentDao) redisConnectionManager;
	}

	/*
	 * Redis channel names follow this format: csf.commands.simToDistSystem.IDOFDISTRIBUTEDSYSTEM
	 */
	// TODO: configuration: add id of distributed system(s)
	private String createRedisChannelStr(SimulationRunContext simulationRunContext,
			DistributedSystem distributedSystem) {
		// LOW: Make this configurable on the CSF side.
		String csfPrefix = "csf.commands";
		// LOW: Make this configurable
		return csfPrefix + "simToDistSystem" + distributedSystem.getDistributedSystemID();
	}

	// LOW: Add functionality for handling multiple distributed agent systems
	// Call sendMessagesToDistributedAgents for a single distributed agent system from here.

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.sim.internal.messaging.dao.DistributedAgentDao
	 * #sendMessagesToDistributedAgents(org.simulationsystems.csf.sim.api.
	 * messaging.FrameworkMessage,
	 * org.simulationsystems.csf.sim.api.messaging.DistributedSystemAgentSet,
	 * org.simulationsystems.csf.sim.api.SimulationRunContext)
	 */
	@Override
	public void sendMessagesToDistributedAgents(SimulationRunContext simulationRunContext,
			FrameworkMessage frameworkMessage, DistributedSystem distributedSystem) {

		String redisChannelStr = createRedisChannelStr(simulationRunContext, distributedSystem);
		redisConnectionManager.postMessage(redisChannelStr,
				frameworkMessage.transformToCommonMessagingXMLString(distributedSystem));

	}

}
