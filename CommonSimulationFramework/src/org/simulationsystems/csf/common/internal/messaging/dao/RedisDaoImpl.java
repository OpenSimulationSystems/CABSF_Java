package org.simulationsystems.csf.common.internal.messaging.dao;

import java.io.IOException;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfMessagingRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FRAMEWORK_COMMAND;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.interfaces.redis.RedisConnectionManager;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;

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
	 * Redis channel names follow this format:
	 * csf.commands.simToDistSystem.IDOFDISTRIBUTEDSYSTEM
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
	// Call sendMessagesToDistributedAgents for a single distributed agent
	// system from here.

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao
	 * #sendMessagesToDistributedAgents(org.simulationsystems.csf.sim.core.api.
	 * messaging.FrameworkMessage,
	 * org.simulationsystems.csf.sim.core.api.messaging.DistributedSystemAgentSet,
	 * org.simulationsystems.csf.sim.core.api.SimulationRunContext)
	 */
	@Override
	public void sendMessagesToDistributedAgents(
			SimulationRunContext simulationRunContext,
			FrameworkMessage frameworkMessage,
			DistributedSystem distributedSystem) {

		String redisChannelStr = createRedisChannelStr(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				distributedSystem.getDistributedSystemID());
		redisConnectionManager.postMessage(redisChannelStr,
				frameworkMessage.transformToCommonMessagingXMLString(true));

	}

	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface(
			String messagingConnectionString) {
		redisConnectionManager
				.initializeRedisConnection(messagingConnectionString);

	}

	@Override
	public void closeInterface() {
		redisConnectionManager.closePool();
	}

	@Override
	// FIXME: Pass back the actual value and handle it upstream
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(String clientID) {
		return retrieveFrameworkMessage(SYSTEM_TYPE.SYSTEM_ADMINISTRATOR, SYSTEM_TYPE.SIMULATION_ENGINE, clientID);
	}

	@Override
	// FIXME: Pass back the actual value and handle it upstream
	public FrameworkMessage readFrameworkMessageFromDistributedSystem(String clientID) {
		return retrieveFrameworkMessage(SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE, clientID);
	}

	@Override
	// FIXME: Pass back the actual value and handle it upstream
	public FrameworkMessage listenForMessageFromSimulationEngine(SYSTEM_TYPE targetSystemType, String clientID) {
		return retrieveFrameworkMessage(SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM, clientID);

	}
	
	private FrameworkMessage retrieveFrameworkMessage(SYSTEM_TYPE sourceSystemType, SYSTEM_TYPE targetSystemType, String clientID) {
		String redisChannelStr = createRedisChannelStr(
				sourceSystemType, targetSystemType,
				clientID);

		// LOW: Improve performance, use publish/subscribe
		String xmlString = redisConnectionManager.redisSynchronousPolling(
				targetSystemType, redisChannelStr, 0.001d, null);

		Document doc;
		try {
			doc = MessagingUtilities.createDocumentFromString(xmlString);
		} catch (JDOMException e) {
			// TODO: Find the rest of the exceptions and set more information
			// like the system IDs.
			throw new CsfMessagingRuntimeException(
					"Failed to understand message from the simulation engine to the distributed system: "
							+ clientID, e);
		} catch (IOException e) {
			throw new CsfMessagingRuntimeException(
					"Failed to understand message from the simulation engine to the distributed system: "
							+ clientID+ "Message: "+ xmlString,e );
		}
		//TODO: Maybe remove the boolean and use null on the doc to determine whether cloning is required or not.
		FrameworkMessage fm = new FrameworkMessageImpl(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				doc);
		return fm;
	}



	@Override
	public FrameworkMessage requestEnvironmentInformation(
			SYSTEM_TYPE targetSystemType, String clientID) {
		String redisChannelStr = createRedisChannelStr(
				SYSTEM_TYPE.SIMULATION_ENGINE, targetSystemType, clientID);

		// LOW: Improve performance, use publish/subscribe
		String messageXML = redisConnectionManager.redisSynchronousPolling(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, redisChannelStr, 0.001d, null);
		FrameworkMessage fm = null;
		try {
			fm = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
					SYSTEM_TYPE.DISTRIBUTED_SYSTEM, messageXML);
		} catch (CsfCheckedException e) {
			throw new CsfMessagingRuntimeException(e);
		}

		return fm;
	}

	@Override
	public void sendMessageToSimulationEngine(DistSysRunContext distSysRunContext,
			FrameworkMessage frameworkMessage, String simulationEngineID) {
		String redisChannelStr = createRedisChannelStr(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE,
				simulationEngineID);
		redisConnectionManager.postMessage(redisChannelStr,
				frameworkMessage.transformToCommonMessagingXMLString(true));

		
	}
}
