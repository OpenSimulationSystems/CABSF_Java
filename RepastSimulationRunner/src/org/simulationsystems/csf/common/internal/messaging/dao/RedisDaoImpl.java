package org.simulationsystems.csf.common.internal.messaging.dao;

import java.util.UUID;

import org.simulationsystems.csf.common.csfmodel.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfSimulationCheckedException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfSimulationMessagingRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.interfaces.redis.RedisConnectionManager;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.sim.api.SimulationRunContext;

public class RedisDaoImpl implements CommonMessagingDao {
	static private CommonMessagingDao instance = new RedisDaoImpl();
	// We're using a connection pool in Redis so it's ok to use a singleton.
	static private RedisConnectionManager redisConnectionManager = new RedisConnectionManager();

	/*
	 * Disable this constructor so we can use a Singleton
	 */
	@SuppressWarnings("unused")
	private void DistributedAgentDAO() {
	}

	static public CommonMessagingDao getInstance() {
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
			prefix3 = "Admin";
		else if (targetSystemType == SYSTEM_TYPE.DISTRIBUTED_SYSTEM)
			prefix3 = "DistSystem";
		else
			prefix3 = "Sim";

		String channel = prefix + prefix2 + prefix3 + ":" + ID;
		// LOW: Make this configurable
		return channel;
	}

	// LOW: Add functionality for handling multiple distributed agent systems
	// Call sendMessagesToDistributedAgents for a single distributed agent system from here.

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao
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
				frameworkMessage.transformToCommonMessagingXMLString(true));

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
		String redisChannelStr = createRedisChannelStr(SYSTEM_TYPE.SYSTEM_ADMINISTRATOR,
				SYSTEM_TYPE.SIMULATION_ENGINE, clientID);

		// LOW: Improve performance, use publish/subscribe
		redisConnectionManager.redisSynchronousPolling(SYSTEM_TYPE.SIMULATION_ENGINE,
				redisChannelStr, 1l, null);

	}

	@Override
	public FRAMEWORK_COMMAND listenForCommandsFromSimulationEngine(SYSTEM_TYPE targetSystemType,
			String clientID) {
		String redisChannelStr = createRedisChannelStr(SYSTEM_TYPE.SYSTEM_ADMINISTRATOR,
				targetSystemType, clientID);

		// LOW: Improve performance, use publish/subscribe
		String messageXML = redisConnectionManager.redisSynchronousPolling(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, redisChannelStr, 1l, null);
		FrameworkMessage fm = null;
		try {
			fm = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
					SYSTEM_TYPE.DISTRIBUTED_SYSTEM, messageXML);
		} catch (CsfSimulationCheckedException e) {
			throw new CsfSimulationMessagingRuntimeException(e);
		}

		return fm.getFrameworkCommand();
	}
}
