package org.simulationsystems.csf.common.internal.messaging.dao;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfCheckedException;
import org.simulationsystems.csf.common.csfmodel.csfexceptions.CsfMessagingRuntimeException;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage;
import org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessageImpl;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.interfaces.redis.RedisConnectionManager;
import org.simulationsystems.csf.common.internal.systems.DistributedSystem;
import org.simulationsystems.csf.distsys.core.api.DistSysRunContext;
import org.simulationsystems.csf.sim.core.api.SimulationRunContext;

/**
 * The DAO implementation class for Redis
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RedisDaoImpl implements CommonMessagingDao {

	/** The instance. */
	static private CommonMessagingDao instance = new RedisDaoImpl();
	// We're using a connection pool in Redis so it's ok to use a singleton.
	/** The redis connection manager. */
	static private RedisConnectionManager redisConnectionManager = new RedisConnectionManager();

	/**
	 * Gets the single instance of RedisDaoImpl.
	 * 
	 * @return single instance of RedisDaoImpl
	 */
	static public CommonMessagingDao getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao#
	 * closeInterface()
	 */
	@Override
	public void closeInterface() {
		redisConnectionManager.closePool();
	}

	// TODO: configuration: add id of distributed system(s)
	/**
	 * Creates the redis channel string. Redis channel names follow this format:
	 * csf.commands.simToDistSystem.IDofDISTRIBUTEDSYSTEM <br/>
	 * <br/>
	 * Note: Whenever we use the term "Redis Channel"it does not necessarily mean we are
	 * using the publish/subscribe model. It could be a Redis key. Eventually the code
	 * will be switched over to publish/subscribe.
	 * 
	 * @param sourceSystemType
	 *            the source system type
	 * @param targetSystemType
	 *            the target system type
	 * @param ID
	 *            the id
	 * @return the string
	 */
	private String createRedisChannelStr(final SYSTEM_TYPE sourceSystemType,
			final SYSTEM_TYPE targetSystemType, final String ID) {
		// LOW: Make this configurable on the CSF side.
		final String prefix = "csf.commands.";

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

		final String channel = prefix + prefix2 + prefix3 + ":" + ID;
		// LOW: Make this configurable
		return channel;
	}

	// LOW: Add functionality for handling multiple distributed agent systems
	// Call sendMessagesToDistributedAgents for a single distributed agent
	// system from here.
	/**
	 * Disabled constructor. Use the singleton instaed.
	 */
	@SuppressWarnings("unused")
	private void DistributedAgentDAO() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao#
	 * initializeSimulationFrameworkCommonMessagingInterface(java.lang.String)
	 */
	@Override
	public void initializeSimulationFrameworkCommonMessagingInterface(
			final String messagingConnectionString) {
		redisConnectionManager.initializeRedisConnection(messagingConnectionString);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao#
	 * listenForMessageFromSimulationEngine
	 * (org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE, java.lang.String)
	 */
	@Override
	// FIXME: Pass back the actual value and handle it upstream
	public FrameworkMessage listenForMessageFromSimulationEngine(
			final SYSTEM_TYPE targetSystemType, final String clientID) {
		return retrieveFrameworkMessage(SYSTEM_TYPE.SIMULATION_ENGINE,
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, clientID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao#
	 * readFrameworkMessageFromDistributedSystem(java.lang.String)
	 */
	@Override
	// FIXME: Pass back the actual value and handle it upstream
	public FrameworkMessage readFrameworkMessageFromDistributedSystem(
			final String clientID) {
		return retrieveFrameworkMessage(SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				SYSTEM_TYPE.SIMULATION_ENGINE, clientID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao#
	 * readFrameworkMessageFromSimulationAdministrator(java.lang.String)
	 */
	@Override
	// FIXME: Pass back the actual value and handle it upstream
	public FrameworkMessage readFrameworkMessageFromSimulationAdministrator(
			final String clientID) {
		return retrieveFrameworkMessage(SYSTEM_TYPE.SYSTEM_ADMINISTRATOR,
				SYSTEM_TYPE.SIMULATION_ENGINE, clientID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao#
	 * requestEnvironmentInformation
	 * (org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE, java.lang.String)
	 */
	@Override
	public FrameworkMessage requestEnvironmentInformation(
			final SYSTEM_TYPE targetSystemType, final String clientID) {
		final String redisChannelStr = createRedisChannelStr(
				SYSTEM_TYPE.SIMULATION_ENGINE, targetSystemType, clientID);

		// LOW: Improve performance, use publish/subscribe
		final String messageXML = redisConnectionManager.redisSynchronousPolling(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, redisChannelStr, 0.001d, null);
		FrameworkMessage fm = null;
		try {
			fm = new FrameworkMessageImpl(SYSTEM_TYPE.SIMULATION_ENGINE,
					SYSTEM_TYPE.DISTRIBUTED_SYSTEM, messageXML);
		} catch (final CsfCheckedException e) {
			throw new CsfMessagingRuntimeException(e);
		}

		return fm;
	}

	/**
	 * Retrieve the string message from Redis and return a FrameworkMessage
	 * 
	 * @param sourceSystemType
	 *            the source system type
	 * @param targetSystemType
	 *            the target system type
	 * @param clientID
	 *            the client id
	 * @return the FrameworkMessage
	 */
	private FrameworkMessage retrieveFrameworkMessage(final SYSTEM_TYPE sourceSystemType,
			final SYSTEM_TYPE targetSystemType, final String clientID) {
		final String redisChannelStr = createRedisChannelStr(sourceSystemType,
				targetSystemType, clientID);

		// LOW: Improve performance, use publish/subscribe
		final String xmlString = redisConnectionManager.redisSynchronousPolling(
				targetSystemType, redisChannelStr, 0.001d, null);

		Document doc;
		try {
			doc = MessagingUtilities.createDocumentFromString(xmlString);
		} catch (final JDOMException e) {
			// TODO: Find the rest of the exceptions and set more information
			// like the system IDs.
			throw new CsfMessagingRuntimeException(
					"Failed to understand message from the simulation engine to the distributed system: "
							+ clientID, e);
		} catch (final IOException e) {
			throw new CsfMessagingRuntimeException(
					"Failed to understand message from the simulation engine to the distributed system: "
							+ clientID + "Message: " + xmlString, e);
		}
		// TODO: Maybe remove the boolean and use null on the doc to determine whether
		// cloning is required or not.
		final FrameworkMessage fm = new FrameworkMessageImpl(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM, doc);
		return fm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao
	 * #sendMessagesToDistributedAgents(org.simulationsystems.csf.sim.core.api.
	 * messaging.FrameworkMessage,
	 * org.simulationsystems.csf.sim.core.api.messaging.DistributedSystemAgentSet,
	 * org.simulationsystems.csf.sim.core.api.SimulationRunContext)
	 */
	@Override
	public void sendMessagesToDistributedAgents(
			final SimulationRunContext simulationRunContext,
			final FrameworkMessage frameworkMessage,
			final DistributedSystem distributedSystem) {

		final String redisChannelStr = createRedisChannelStr(
				SYSTEM_TYPE.SIMULATION_ENGINE, SYSTEM_TYPE.DISTRIBUTED_SYSTEM,
				distributedSystem.getDistributedSystemID());
		redisConnectionManager.postMessage(redisChannelStr,
				frameworkMessage.transformToCommonMessagingXMLString(true));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simulationsystems.csf.common.internal.messaging.dao.CommonMessagingDao#
	 * sendMessageToSimulationEngine
	 * (org.simulationsystems.csf.distsys.core.api.DistSysRunContext,
	 * org.simulationsystems.csf.common.csfmodel.messaging.messages.FrameworkMessage,
	 * java.lang.String)
	 */
	@Override
	public void sendMessageToSimulationEngine(final DistSysRunContext distSysRunContext,
			final FrameworkMessage frameworkMessage, final String simulationEngineID) {
		final String redisChannelStr = createRedisChannelStr(
				SYSTEM_TYPE.DISTRIBUTED_SYSTEM, SYSTEM_TYPE.SIMULATION_ENGINE,
				simulationEngineID);
		redisConnectionManager.postMessage(redisChannelStr,
				frameworkMessage.transformToCommonMessagingXMLString(true));

	}
}
